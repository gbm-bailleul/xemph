/*
 * Copyright 2017 Guillaume Bailleul.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.gbmb.xemph.generator;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Guillaume Bailleul on 03/05/2017.
 */
public class Namespace {

    @JsonProperty(value = "default-prefix")
    private String defaultPrefix;

    @JsonProperty
    private String uri;

    @JsonProperty
    private String classname;

    @JsonProperty
    private List<Property> properties;

    public String getDefaultPrefix() {
        return defaultPrefix;
    }

    public String getUri() {
        return uri;
    }

    public String getClassname() {
        return classname;
    }

    public List<Property> getProperties() {
        return properties;
    }
}
