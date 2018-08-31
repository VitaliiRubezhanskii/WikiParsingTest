package com.wikipedia.parser.exceptions;

public class HtmlParsingException extends RuntimeException {

    public HtmlParsingException(String message, Throwable e) {
        super(message, e);
    }
}
