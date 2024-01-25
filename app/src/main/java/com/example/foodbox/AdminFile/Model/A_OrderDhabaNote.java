package com.example.foodbox.AdminFile.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class A_OrderDhabaNote {
    private String order;
    private Integer total;
    private String userid;
    private String cust_name;
    private String area;
    private String phone_no;
    private String flat_no;
    private String pin_no;
    private String order_id;
    private String cust_id;
    private String pay_method;
    private String status;
    private String u_email;
    private @ServerTimestamp Date timestamp;


    public A_OrderDhabaNote() {
    }

    public A_OrderDhabaNote(String order, Integer total, String userid, String cust_name, String pin_no, String phone_no, String area, String flat_no, String order_id, String cust_id, String pay_method, String status, String u_email, Date timestamp) {
        this.order = order;
        this.total = total;
        this.userid = userid;
        this.cust_name = cust_name;
        this.pin_no = pin_no;
        this.phone_no = phone_no;
        this.area = area;
        this.flat_no = flat_no;
        this.order_id = order_id;
        this.cust_id = cust_id;
        this.pay_method = pay_method;
        this.status = status;
        this.u_email = u_email;
        this.timestamp = timestamp;
    }

    public A_OrderDhabaNote(String order, Integer total, String userid, String cust_name, String pin_no, String phone_no, String area, String flat_no, String order_id, String cust_id, String pay_method, String status, String u_email) {
        this.order = order;
        this.total = total;
        this.userid = userid;
        this.cust_name = cust_name;
        this.pin_no = pin_no;
        this.phone_no = phone_no;
        this.flat_no = flat_no;
        this.area = area;
        this.order_id = order_id;
        this.cust_id = cust_id;
        this.pay_method = pay_method;
        this.status = status;
        this.u_email = u_email;
    }

    public String getU_email() {
        return u_email;
    }

    public void setU_email(String u_email) {
        this.u_email = u_email;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getPin_no() {
        return pin_no;
    }

    public void setPin_no(String pnr_no) {
        this.pin_no = pnr_no;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String train_no) {
        this.area = train_no;
    }

    public String getFlat_no() {
        return flat_no;
    }

    public void setFlat_no(String berth) {
        this.flat_no = berth;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

    public String getPay_method() {
        return pay_method;
    }

    public void setPay_method(String pay_method) {
        this.pay_method = pay_method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}