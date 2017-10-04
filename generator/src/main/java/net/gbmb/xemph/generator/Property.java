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

/**
 * the property description class of yaml namespace description
 */
public class Property {

    @JsonProperty
    private String name;

    @JsonProperty
    private String type;

    @JsonProperty
    private String element;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getElement() {
        return element;
    }
}
