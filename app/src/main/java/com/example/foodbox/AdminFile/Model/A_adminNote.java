package com.example.foodbox.AdminFile.Model;

public class A_adminNote {
    private String admin_name;
    private String email;
    private String password;
    private String admin_phone;

    public A_adminNote() {
    }

    public A_adminNote(String admin_name, String email, String password, String admin_phone) {
        this.admin_name = admin_name;
        this.email = email;
        this.password = password;
        this.admin_phone = admin_phone;
    }

    public String getAdmin_name() {
        return admin_name;
    }

    public void setAdmin_name(String admin_name) {
        this.admin_name = admin_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdmin_phone() {
        return admin_phone;
    }

    public void setAdmin_phone(String admin_phone) {
        this.admin_phone = admin_phone;
    }
}
