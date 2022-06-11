package com.example.flower;

import java.io.Serializable;

public class User implements Serializable {
    private int user_id;
    private String username;
    private String password;
    private String tel;
    private String realname;

    public User() {
    }/*
    public User(String a,String b){
        switch (a){
            case "id":int c=(int)b;this.user_id=b-'0';
        }
    }*/
    public User(int user_id, String username, String password) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
    }
    public User(String username, String password,String tel,String realname){
        this.username = username;
        this.password = password;
        this.tel=tel;
        this.realname=realname;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    // public void setUser_id(String user_id) {
    //this.user_id = Integer.getInteger(user_id);
    //}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setUsertel(String usertel) {
        this.tel = usertel;
    }

    public void setUserRealname(String userrealname) {
        this.realname =userrealname ;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public String getUsertel() {
        return tel;
    }

    public String getUserRealname() {
        return realname;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", tel='" + tel + '\''+
                ", realname='" + realname + '\''+
        '}';
    }




}
