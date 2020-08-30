package ru.sc222.devslife.utils;


public class ErrorInfo {

    private String retryUrl;
    private LoadError error;

    public ErrorInfo(LoadError error) {
        this(error, null);
    }

    public ErrorInfo(LoadError error, String retryUrl) {
        this.error = error;
        this.retryUrl = retryUrl;
    }

    public boolean hasErrors() {
        return error != LoadError.NO_ERRORS;
    }

    public String getRetryUrl() {
        return retryUrl;
    }


    public LoadError getError() {
        return error;
    }
}
