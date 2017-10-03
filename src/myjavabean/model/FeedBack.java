package myjavabean.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FeedBack {
	private String uemailaddress;
	private String feedback;
	private String inputtext;
    private String udatetime;
    private Date date;
    
    public FeedBack() {
    	date = new Date();
    	uemailaddress = "";
    	feedback = "";
    	inputtext = "";
    	udatetime = new SimpleDateFormat().format(date);
    }

	public String getUemailaddress() {
		return uemailaddress;
	}

	public void setUemailaddress(String uemailaddress) {
		this.uemailaddress = uemailaddress;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public String getInputtext() {
		return inputtext;
	}

	public void setInputtext(String inputtext) {
		this.inputtext = inputtext;
	}

	public String getUdatetime() {
		return udatetime;
	}

	public void setUdatetime(String udatetime) {
		this.udatetime = udatetime;
	}
	
}
