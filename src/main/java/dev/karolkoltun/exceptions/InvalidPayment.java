package dev.karolkoltun.exceptions;

public class InvalidPayment extends Exception {

    private String phrase;

    public InvalidPayment(){
        phrase = "Payment cannot contain future date or amount over 1000.";
    }

    public String getPhrase() {
        return phrase;
    }
}
