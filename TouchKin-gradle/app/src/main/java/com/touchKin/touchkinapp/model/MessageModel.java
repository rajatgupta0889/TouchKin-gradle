package com.touchKin.touchkinapp.model;

public class MessageModel {

	String message, messageDay, messageTime, userName, userImageUrl, userId;

	public MessageModel(String message, String messageDay, String messageTime,
			String userName, String userImageUrl, String userId) {
		super();
		this.message = message;
		this.messageDay = messageDay;
		this.messageTime = messageTime;
		this.userName = userName;
		this.userImageUrl = userImageUrl;
		this.userId = userId;
	}

	public MessageModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessageDay() {
		return messageDay;
	}

	public void setMessageDay(String messageDay) {
		this.messageDay = messageDay;
	}

	public String getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(String messageTime) {
		this.messageTime = messageTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserImageUrl() {
		return userImageUrl;
	}

	public void setUserImageUrl(String userImageUrl) {
		this.userImageUrl = userImageUrl;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
