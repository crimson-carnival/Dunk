package com.ashusinha.android.dunk;

public class StudyMaterial {
    private String title, subject, url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public StudyMaterial(String title, String subject, String url) {

        this.title = title;
        this.subject = subject;
        this.url = url;
    }

    public StudyMaterial() {

    }
}
