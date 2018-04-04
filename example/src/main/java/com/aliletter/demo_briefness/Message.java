package com.aliletter.demo_briefness;

/**
 * author  : Black Chopper
 * e-mail  : 4884280@qq.com
 * github  : http://github.com/BlackChopper
 * project : Briefness
 */

public class Message {
    private String username;
    private String name;
    private String alias;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Message(String username, String name, String alias) {
        this.username = username;
        this.alias = alias;
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }
}
