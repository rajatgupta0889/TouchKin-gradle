package com.touchKin.touchkinapp.model;

public class ParentListModel {
	String imageUrl;
	Boolean isSelected;
	String parentName;
	String parentId;
	String parentUserId;
	Boolean reqStatus;
	String mobilenumber;
	Boolean isPendingTouch;
	Boolean isTouchMedia;
	Boolean isMale;

	public ParentListModel(String imageUrl, Boolean isSelected,
			String parentName, String parentId, String parentUserId,
			Boolean reqStatus, String mobilenumber, Boolean isPendingTouch,
			Boolean isTouchMedia, Boolean isMale) {
		super();
		this.imageUrl = imageUrl;
		this.isSelected = isSelected;
		this.parentName = parentName;
		this.parentId = parentId;
		this.parentUserId = parentUserId;
		this.reqStatus = reqStatus;
		this.mobilenumber = mobilenumber;
		this.isPendingTouch = isPendingTouch;
		this.isTouchMedia = isTouchMedia;
		this.isMale = isMale;
	}

	public Boolean getIsMale() {
		return isMale;
	}

	public void setIsMale(Boolean isMale) {
		this.isMale = isMale;
	}

	public Boolean getIsPendingTouch() {
		return isPendingTouch;
	}

	public void setIsPendingTouch(Boolean isPendingTouch) {
		this.isPendingTouch = isPendingTouch;
	}

	public Boolean getIsTouchMedia() {
		return isTouchMedia;
	}

	public void setIsTouchMedia(Boolean isTouchMedia) {
		this.isTouchMedia = isTouchMedia;
	}

	public String getMobilenumber() {
		return mobilenumber;
	}

	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}

	public ParentListModel() {
		super();
	}

	public String getParentUserId() {
		return parentUserId;
	}

	public void setParentUserId(String parentUserId) {
		this.parentUserId = parentUserId;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public Boolean getReqStatus() {
		return reqStatus;
	}

	public void setReqStatus(Boolean reqStatus) {
		this.reqStatus = reqStatus;
	}

}
