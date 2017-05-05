package org.openhab.binding.volkszaehler.internal;

public class FutureErrorException extends Exception {
    private static final long serialVersionUID = 1L;

    public FutureErrorException(String cause) {
        super(cause);
    }
}
