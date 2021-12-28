package com.aampower.aampoweradmin.model;

public class DealersModel {

    private String accountNo, designation, deealersCityName, accountName,
            address, telNo, fax, mobileNo, email, web, balance;


    public DealersModel() {
    }

    public DealersModel(String accountNo, String designation, String deealersCityName, String accountName, String address, String telNo, String fax, String mobileNo, String email, String web, String balance) {
        this.accountNo = accountNo;
        this.designation = designation;
        this.deealersCityName = deealersCityName;
        this.accountName = accountName;
        this.address = address;
        this.telNo = telNo;
        this.fax = fax;
        this.mobileNo = mobileNo;
        this.email = email;
        this.web = web;
        this.balance = balance;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDeealersCityName() {
        return deealersCityName;
    }

    public void setDeealersCityName(String deealersCityName) {
        this.deealersCityName = deealersCityName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
