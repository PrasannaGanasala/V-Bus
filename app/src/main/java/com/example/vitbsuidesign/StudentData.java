package com.example.vitbsuidesign;

public class StudentData {
    String Name, RegNo, Email, BusNo, Password;

    public StudentData() { }

    public StudentData(String name, String regNo, String email, String busNo, String password) {
        Name = name;
        RegNo = regNo;
        Email = email;
        BusNo = busNo;
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRegNo() {
        return RegNo;
    }

    public void setRegNo(String regNo) {
        RegNo = regNo;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setBusNo(String busNo) {
        BusNo = busNo;
    }

    public String getBusNo() {
        return BusNo;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
