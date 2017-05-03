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
