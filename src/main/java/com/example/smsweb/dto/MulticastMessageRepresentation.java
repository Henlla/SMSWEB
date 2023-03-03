package com.example.smsweb.dto;

import java.util.List;

public class MulticastMessageRepresentation {
    private String data;
    private String title;
    private List<String> registrationTokens;
    /**
     * @return the message
     */
    public String getData() {
        return data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setData(String data) {
        this.data = data;
    }
    /**
     * @return the registrationTokens
     */
    public List<String> getRegistrationTokens() {
        return registrationTokens;
    }
    /**
     * @param registrationTokens the registrationTokens to set
     */
    public void setRegistrationTokens(List<String> registrationTokens) {
        this.registrationTokens = registrationTokens;
    }
}
