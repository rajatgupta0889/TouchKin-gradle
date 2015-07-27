package com.touchKin.touchkinapp.model;

public class TouchKinComments {

	String commentText, commentTime, commentDay, userName, userImageUrl,
			userId;

	public TouchKinComments(String commentText, String commentTime,
			String commentDay, String userName, String userImageUrl) {
		super();
		this.commentText = commentText;
		this.commentTime = commentTime;
		this.commentDay = commentDay;
		this.userName = userName;
		this.userImageUrl = userImageUrl;
	}

	public TouchKinComments() {

	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(String commentTime) {
		this.commentTime = commentTime;
	}

	public String getCommentDay() {
		return commentDay;
	}

	public void setCommentDay(String commentDay) {
		this.commentDay = commentDay;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	public String getUserImageUrl() {
		return userImageUrl;
	}

	public void setUserImageUrl(String userImageUrl) {
		this.userImageUrl = userImageUrl;
	}

}
