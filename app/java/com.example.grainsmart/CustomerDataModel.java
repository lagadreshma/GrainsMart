package com.example.grainsmart;

public class CustomerDataModel {

    private String cid, cname, cemail, cpassword, ccontact, caddress, cprofilepic;

    public CustomerDataModel(){

    }

    public CustomerDataModel(String cid, String cname, String cemail, String cpassword, String ccontact, String caddress, String cprofilepic) {
        this.cid = cid;
        this.cname = cname;
        this.cemail = cemail;
        this.cpassword = cpassword;
        this.ccontact = ccontact;
        this.caddress = caddress;
        this.cprofilepic = cprofilepic;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCemail() {
        return cemail;
    }

    public void setCemail(String cemail) {
        this.cemail = cemail;
    }

    public String getCpassword() {
        return cpassword;
    }

    public void setCpassword(String cpassword) {
        this.cpassword = cpassword;
    }

    public String getCcontact() {
        return ccontact;
    }

    public void setCcontact(String ccontact) {
        this.ccontact = ccontact;
    }

    public String getCaddress() {
        return caddress;
    }

    public void setCaddress(String caddress) {
        this.caddress = caddress;
    }

    public String getCprofilepic() {
        return cprofilepic;
    }

    public void setCprofilepic(String cprofilepic) {
        this.cprofilepic = cprofilepic;
    }
}
