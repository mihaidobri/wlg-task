package me.maxwu.wlg.pages;

import org.openqa.selenium.WebDriver;

public class WrongPageException extends RuntimeException{
    private String title = null;
    private String url = null;

    public WrongPageException(String msg){
        super(msg);
    }

    public WrongPageException(String msg, String title, String url){
        super(msg + ", title=" + title + ", url=" + url);
        this.title = title;
        this.url = url;
    }

    public WrongPageException(String msg, WebDriver dr){
        this(msg, dr.getTitle(), dr.getCurrentUrl());
    }
}
