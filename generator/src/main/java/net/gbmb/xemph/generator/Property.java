package net.gbmb.xemph.generator;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Guillaume Bailleul on 03/05/2017.
 */
public class Property {

    @JsonProperty
    private String name;

    @JsonProperty
    private String type;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
