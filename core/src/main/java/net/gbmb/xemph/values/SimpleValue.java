package net.gbmb.xemph.values;

import net.gbmb.xemph.Value;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public class SimpleValue extends Value {

    private String content;

    public SimpleValue(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
