package dev.karolkoltun.expenses;

public class NoExpenseInCategoryException extends Exception{

    private String phrase;

    public NoExpenseInCategoryException(){
        this.phrase = "There is no expense in this category,";
    }

    public String getPhrase() {
        return phrase;
    }
}
