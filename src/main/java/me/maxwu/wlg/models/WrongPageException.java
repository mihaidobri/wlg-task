package me.maxwu.wlg.models;

import org.openqa.selenium.WebDriver;

/**
 * The exception to describe check failure within PageBase on common test supports.
 * URL and Title are current checkpoints to highlight in exception message.
 */
public class WrongPageException extends RuntimeException{
    public WrongPageException(String msg){
        super(msg);
    }

    public WrongPageException(String msg, String title, String url){
        super(msg + ", title=" + title + ", url=" + url);
    }

    public WrongPageException(String msg, WebDriver dr){
        this(msg, dr.getTitle(), dr.getCurrentUrl());
    }
}
