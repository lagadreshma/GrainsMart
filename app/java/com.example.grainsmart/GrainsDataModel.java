package com.example.grainsmart;

public class GrainsDataModel {

    private String fid, gid;
    private String gname;
    private String gvariety;
    private double gprice;
    private int gquantity;
    private String location;
    private String gimage;
    private String upiId;
    private String grainAddedDate;

    public GrainsDataModel() {
        // Default constructor required for DataSnapshot.getValue(GrainDataModel.class)
    }

    public GrainsDataModel(String gid, String fid, String gname, String gvariety, double gprice,
                           int gquantity, String location, String upiId ,String gimage, String grainAddedDate) {
        this.gid = gid;
        this.fid = fid;
        this.gname = gname;
        this.gvariety = gvariety;
        this.gprice = gprice;
        this.gquantity = gquantity;
        this.location = location;
        this.gimage = gimage;
        this.upiId = upiId;
        this.grainAddedDate = grainAddedDate;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public String getGvariety() {
        return gvariety;
    }

    public void setGvariety(String gvariety) {
        this.gvariety = gvariety;
    }

    public double getGprice() {
        return gprice;
    }

    public void setGprice(double gprice) {
        this.gprice = gprice;
    }

    public int getGquantity() {
        return gquantity;
    }

    public void setGquantity(int gquantity) {
        this.gquantity = gquantity;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGimage() {
        return gimage;
    }

    public void setGimage(String gimage) {
        this.gimage = gimage;
    }

    public String getGrainAddedDate() {
        return grainAddedDate;
    }

    public void setGrainAddedDate(String grainAddedDate) {
        this.grainAddedDate = grainAddedDate;
    }
}
