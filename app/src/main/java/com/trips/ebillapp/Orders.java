package com.trips.ebillapp;

/**
 * This is Model class
 * This will fetch all the parameters from the Firestore Database
 * and will pass them to the adapter class
 */

//Defined all the data to be fetched
//Also set up the Constructors and Getters & Setters for each and every data
public class Orders {
    String image;
    String day_date;
    String id;
    String type;
    String status;
    String job_assignment;
    float grand_total;

    public String getJob_assignment() {
        return job_assignment;
    }

    public void setJob_assignment(String job_assignment) {
        this.job_assignment = job_assignment;
    }

    public Orders(String job_assignment) {
        this.job_assignment = job_assignment;
    }


    public Orders(float grand_total) {
        this.grand_total = grand_total;
    }

    public float getGrand_total() {
        return grand_total;
    }

    public void setGrand_total(float grand_total) {
        this.grand_total = grand_total;
    }



    public Orders(){

    }

    public Orders(String image, String day_date, String id, String type, String status) {
        this.image = image;
        this.day_date = day_date;
        this.id = id;
        this.type = type;
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDay_date() {
        return day_date;
    }

    public void setDay_date(String day_date) {
        this.day_date = day_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
