package myjavabean.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TextPosted {
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private String text;
	private String time;
	
	public TextPosted() {
		text = null;
		time = new SimpleDateFormat(DATETIME_FORMAT).format(new Date());
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
