package org.firespeed.kensei.SVNLogConvertor;

import java.util.List;

public class CSVLine {
	
	public static final String formatLineHead = "user;issue;review;date_review;file/dir;LOC:I;LOC:D";
	public static final String formatLine = "%s;%s;%s;%s;%s;%s;%s";
	
	private String user;	
	private String review;
	private String date_review;
	private String file;
	private String LOG_I;
	private String LOG_D;
	
	private String msg;
	private BlocoCode blocoCode;
	private List<Issue> issues;
	
	public CSVLine() {
		// TODO Auto-generated constructor stub
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public String getDate_review() {
		return date_review;
	}

	public void setDate_review(String date_review) {
		this.date_review = date_review;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getLOG_I() {
		return LOG_I;
	}

	public void setLOG_I(String lOG_I) {
		LOG_I = lOG_I;
	}

	public String getLOG_D() {
		return LOG_D;
	}

	public void setLOG_D(String lOG_D) {
		LOG_D = lOG_D;
	}

	public static String getFormatlinehead() {
		return formatLineHead;
	}

	public static String getFormatline() {
		return formatLine;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public BlocoCode getBlocoCode() {
		return blocoCode;
	}

	public void setBlocoCode(BlocoCode blocoCode) {
		this.blocoCode = blocoCode;
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}

	
	
	

}
