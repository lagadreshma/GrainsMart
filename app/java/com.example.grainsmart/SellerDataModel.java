package com.example.grainsmart;

public class SellerDataModel {

    private String fid, fname, femail, fpassword, fmobileno, faddress, fprofilepic;

    public SellerDataModel(){

    }
    public SellerDataModel(String fid, String fname, String femail, String fpassword, String fmobileno, String faddress, String fprofilepic) {
        this.fid = fid;
        this.fname = fname;
        this.femail = femail;
        this.fpassword = fpassword;
        this.fmobileno = fmobileno;
        this.faddress = faddress;
        this.fprofilepic = fprofilepic;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getFaddress() {
        return faddress;
    }

    public void setFaddress(String faddress) {
        this.faddress = faddress;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFemail() {
        return femail;
    }

    public void setFemail(String femail) {
        this.femail = femail;
    }

    public String getFpassword() {
        return fpassword;
    }

    public void setFpassword(String fpassword) {
        this.fpassword = fpassword;
    }

    public String getFmobileno() {
        return fmobileno;
    }

    public void setFmobileno(String fmobileno) {
        this.fmobileno = fmobileno;
    }

    public String getFprofilepic() {
        return fprofilepic;
    }

    public void setFprofilepic(String fprofilepic) {
        this.fprofilepic = fprofilepic;
    }
}
