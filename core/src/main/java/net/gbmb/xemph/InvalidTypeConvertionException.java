package net.gbmb.xemph;

/**
 * Created by Guillaume Bailleul on 02/09/2017.
 */
public class InvalidTypeConvertionException extends Exception {

    public InvalidTypeConvertionException() {
    }

    public InvalidTypeConvertionException(String message) {
        super(message);
    }

    public InvalidTypeConvertionException(String message, Throwable cause) {
        super(message, cause);
    }

}
