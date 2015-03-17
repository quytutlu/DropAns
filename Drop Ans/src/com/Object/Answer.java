package com.Object;

public class Answer {
	String []AnswerContent;
	boolean []IsCorrect;

	public Answer(){
		AnswerContent=new String[4];
		IsCorrect=new boolean[4];
	}
	
	public String getAnswerContent(int pos) {
		return AnswerContent[pos];
	}

	public void setAnswerContent(String answerContent,int pos) {
		AnswerContent[pos] = answerContent;
	}

	public boolean isCorrect(int pos) {
		return IsCorrect[pos];
	}

	public void setIsCorrect(boolean isCorrect,int pos) {
		IsCorrect[pos] = isCorrect;
	}
}
