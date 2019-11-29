package dev.karolkoltun.exceptions;

public class DivisionByZeroException extends Exception {

    String phrase;

    public DivisionByZeroException(String phrase){
        this.phrase = phrase;
    }

    public String getPhrase() {
        return phrase;
    }
}
