/*
 * Copyright 2016-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.openshift.booster.service;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.openshift.booster.service.exception.CustomErrorException;

import io.micrometer.core.instrument.Metrics;

@Path("/greeting")
@Component
@ConfigurationProperties
public class GreetingEndpoint {
    private String projectVersion;

    @GET
    @Produces("application/json")
    public Greeting greeting(
        @QueryParam("name") @DefaultValue("World") String name,
        @QueryParam("error") String error) {
        // String format
        final String message = String.format(Greeting.FORMAT, name);

        // Prometheus metric
        Metrics.counter("api.http.requests.total", "api", "greeting", "method", "GET", "endpoint", "/greeting")
                .increment();

        if (error != null) {
            // Prometheus metric
            Metrics.counter("api.http.errors.total", "api", "greeting", "method", "GET", "endpoint", "/greeting")
                    .increment();
            throw new CustomErrorException(error);
        }

        return new Greeting(message, this.projectVersion);
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    
}
