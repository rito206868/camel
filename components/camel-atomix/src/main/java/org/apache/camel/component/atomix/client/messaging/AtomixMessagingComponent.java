/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.atomix.client.messaging;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.atomix.client.AbstractAtomixClientComponent;
import org.apache.camel.spi.annotations.Component;

@Component("atomix-messaging")
public final class AtomixMessagingComponent extends AbstractAtomixClientComponent<AtomixMessagingConfiguration> {
    private AtomixMessagingConfiguration configuration = new AtomixMessagingConfiguration();

    public AtomixMessagingComponent() {
    }

    public AtomixMessagingComponent(CamelContext camelContext) {
        super(camelContext);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        AtomixMessagingConfiguration configuration = this.configuration.copy();

        // Bind options to the configuration object
        setConfigurationProperties(configuration, parameters);

        AtomixMessagingEndpoint endpoint = new AtomixMessagingEndpoint(uri, this, remaining);
        endpoint.setConfiguration(configuration);

        setProperties(endpoint, parameters);

        return endpoint;
    }

    // **********************************************
    // Properties
    // **********************************************

    public AtomixMessagingConfiguration getConfiguration() {
        return this.configuration;
    }

    /**
     * The shared component configuration
     */
    public void setConfiguration(AtomixMessagingConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected AtomixMessagingConfiguration getComponentConfiguration() {
        return getConfiguration();
    }
}
