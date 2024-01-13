package com.example.grainsmart;

public class OrdersModel {

    private String oid, gid, cid, fid;
    private Integer gquantity;
    private Double gprice, gtotalprice;
    private String gname;
    private String orderstatus, paymentstatus, orderdate;

    public OrdersModel(){

    }

    public OrdersModel(String oid, String gid, String cid, String fid, String gname, Integer gquantity,
                       Double gprice, Double gtotalprice, String orderstatus, String paymentstatus, String orderdate) {
        this.oid = oid;
        this.gid = gid;
        this.cid = cid;
        this.fid = fid;
        this.gname = gname;
        this.gquantity = gquantity;
        this.gprice = gprice;
        this.gtotalprice = gtotalprice;
        this.orderstatus = orderstatus;
        this.paymentstatus = paymentstatus;
        this.orderdate = orderdate;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
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

    public Integer getGquantity() {
        return gquantity;
    }

    public void setGquantity(Integer gquantity) {
        this.gquantity = gquantity;
    }

    public Double getGprice() {
        return gprice;
    }

    public void setGprice(Double gprice) {
        this.gprice = gprice;
    }

    public Double getGtotalprice() {
        return gtotalprice;
    }

    public void setGtotalprice(Double gtotalprice) {
        this.gtotalprice = gtotalprice;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    public String getPaymentstatus() {
        return paymentstatus;
    }

    public void setPaymentstatus(String paymentstatus) {
        this.paymentstatus = paymentstatus;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
    }
}
