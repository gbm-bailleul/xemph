package net.gbmb.xemph;

/**
 * Created by Guillaume Bailleul on 02/09/2017.
 */
public class InvalidTypeConvertException extends Exception {

    public InvalidTypeConvertException(String message) {
        super(message);
    }

    public InvalidTypeConvertException(String message, Throwable cause) {
        super(message, cause);
    }

}
