package ru.sc222.devslife.ui.main;


public class ErrorInfo {

    public enum Error {
        NO_ERRORS,
        CANT_LOAD_POST,
        CANT_LOAD_IMAGE,
        COUB_NOT_SUPPORTED
    }

    private String retryUrl;
    private Error error;

    public ErrorInfo(Error error) {
        this(error, null);
    }

    public ErrorInfo(Error error, String retryUrl) {
        this.error = error;
        this.retryUrl = retryUrl;
    }

    public boolean hasErrors() {
        return error != Error.NO_ERRORS;
    }

    public String getRetryUrl() {
        return retryUrl;
    }


    public Error getError() {
        return error;
    }
}
