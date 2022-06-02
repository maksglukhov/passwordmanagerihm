package com.example.projet.data;

public class Data {
    private String website;
    private String login;
    private String password;

    public Data(String website, String login, String password) {
        this.website = website;
        this.login = login;
        this.password = password;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return
                "Website='" + website + '\'' +
                "\n login='" + login + '\'' +
                "\n password='" + password + '\'';

    }
}
