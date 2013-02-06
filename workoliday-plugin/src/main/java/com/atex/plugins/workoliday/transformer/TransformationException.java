package com.atex.plugins.workoliday.transformer;

@SuppressWarnings("serial")
public class TransformationException extends Exception {
    public TransformationException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public TransformationException(String message) {
        super(message);
    }
}
