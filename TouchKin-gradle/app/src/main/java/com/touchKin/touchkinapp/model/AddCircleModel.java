package com.touchKin.touchkinapp.model;

public class AddCircleModel {
	String userImage, userId, userName;

	public AddCircleModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AddCircleModel(String userImage, String userId, String userName) {
		super();
		this.userImage = userImage;
		this.userId = userId;
		this.userName = userName;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
