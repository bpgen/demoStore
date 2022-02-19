package com.store.demo.exception;

public class StoreException extends RuntimeException {

    public StoreException(String message, Throwable exception) {
        super(message, exception);
    }

    public StoreException(String message) {
        super(message);
    }

    public StoreException() {
        super();
    }
}
