package com.Object;

public class Topic {
	private int SubjectId;
	private String Title;
	private String Urlicon;

	public Topic() {
		Title = "quy tu";
		SubjectId = 10;
	}

	public int getSubjectId() {
		return SubjectId;
	}

	public void setSubjectId(int subjectId) {
		SubjectId = subjectId;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getUrlicon() {
		return Urlicon;
	}

	public void setUrlicon(String urlicon) {
		Urlicon = urlicon;
	}

	@Override
	public String toString() {
		return Title;
	}

}