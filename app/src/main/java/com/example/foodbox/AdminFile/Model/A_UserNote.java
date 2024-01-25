package com.example.foodbox.AdminFile.Model;

public class A_UserNote {
    private String username;
    private String email;
    private String phone;
    private String rest_name;
    private String city;
    private String Address;

    public A_UserNote() {
    }

    public A_UserNote(String username, String email, String phone, String rest_name, String city, String address) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.rest_name = rest_name;
        this.city = city;
        Address = address;
    }

    public A_UserNote(String email, String username, String currentuser, String phone, String deviceToken) {
    }

    public String getUid() {
        return email;
    }

    public void setUid(String uid) {
        this.email = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRest_name() {
        return rest_name;
    }

    public void setRest_name(String rest_name) {
        this.rest_name = rest_name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}