package com.example.vitbsuidesign;

public class BusData {

    String BusID;
    String DriverID;
    String DriverName;
    String StudentList;
    int SeatAvailability;
    double Latitude;
    double Longitude;

    public BusData() {

    }

    public BusData(String busID, String driverID, String driverName, String studentList, int seatAvailability, double latitude, double longitude) {
        BusID = busID;
        DriverID = driverID;
        DriverName = driverName;
        StudentList = studentList;
        SeatAvailability = seatAvailability;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getBusID() {
        return BusID;
    }

    public void setBusID(String busID) {
        BusID = busID;
    }

    public String getDriverID() {
        return DriverID;
    }

    public void setDriverID(String driverID) {
        DriverID = driverID;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getStudentList() {
        return StudentList;
    }

    public void setStudentList(String studentList) {
        StudentList = studentList;
    }

    public int getSeatAvailability() {
        return SeatAvailability;
    }

    public void setSeatAvailability(int seatAvailability) {
        SeatAvailability = seatAvailability;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }
}
