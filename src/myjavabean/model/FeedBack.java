package myjavabean.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FeedBack {
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
	private String uemailaddress;
	private String feedinfo;
	private String inputtext;
    private String fbdatetime;
    private Date date;
    
    public FeedBack() {
    	date = new Date();
    	uemailaddress = "";
    	feedinfo = "";
    	inputtext = "";
    	fbdatetime = new SimpleDateFormat(DATETIME_FORMAT).format(date);
    }

	public String getUemailaddress() {
		return uemailaddress;
	}

	public void setUemailaddress(String uemailaddress) {
		this.uemailaddress = uemailaddress;
	}

	public String getFeedInfo() {
		return feedinfo;
	}

	public void setFeedInfo(String feedinfo) {
		this.feedinfo = feedinfo;
	}

	public String getInputtext() {
		return inputtext;
	}

	public void setInputtext(String inputtext) {
		this.inputtext = inputtext;
	}

	public String getFbdatetime() {
		return fbdatetime;
	}

	public void setFbdatetime(String fbdatetime) {
		this.fbdatetime = fbdatetime;
	}
	
}
