package com.touchKin.touchkinapp.model;

public class ExpandableListGroupItem {

	String userId, userName, kinCount, reqCount, mobileNo;

	public ExpandableListGroupItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ExpandableListGroupItem(String userId, String userName,
			String kinCount, String reqCount, String mobileNo) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.kinCount = kinCount;
		this.reqCount = reqCount;
		this.mobileNo = mobileNo;
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

	public String getKinCount() {
		return kinCount;
	}

	public void setKinCount(String kinCount) {
		this.kinCount = kinCount;
	}

	public String getReqCount() {
		return reqCount;
	}

	public void setReqCount(String reqCount) {
		this.reqCount = reqCount;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

}
