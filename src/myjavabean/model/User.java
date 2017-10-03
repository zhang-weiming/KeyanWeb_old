package myjavabean.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // uname, uemailaddress， upassword, uorganization, ucontactway, udatetime
    private String uname;
    private String uemailaddress;
    private String upassword;
    private String uorganization;
    private String ucontactway;
    private String udatetime;
    private Date date;

    public User() {
        date = new Date();
        uname = "user" + String.valueOf(date.getTime()); // 设置默认昵称
        upassword = "";
        uemailaddress = "";
        uorganization = "";
        ucontactway = "";
        udatetime = new SimpleDateFormat(DATETIME_FORMAT).format(date);
    }

    public String getUname() {
        return uname;
    }
    public void setUname(String uname) {
        this.uname = uname;
    }
    public String getUemailaddress() {
        return uemailaddress;
    }
    public void setUemailaddress(String uemailaddress) {
        this.uemailaddress = uemailaddress;
    }
    public String getUpassword() {
        return upassword;
    }
    public void setUpassword(String upassword) {
        this.upassword = upassword;
    }
    public String getUorganization() {
        return uorganization;
    }
    public void setUorganization(String uorganization) {
        this.uorganization = uorganization;
    }
    public String getUcontactway() {
        return ucontactway;
    }
    public void setUcontactway(String ucontactway) {
        this.ucontactway = ucontactway;
    }
    public String getUdatetime() {
        return udatetime;
    }
    public void setUdatetime(String udatetime) {
        this.udatetime = udatetime;
    }

}