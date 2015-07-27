package com.touchKin.touchkinapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestModel implements Parcelable {

	String userName, userId, userImage, care_reciever_name, requestID, reqMsg;

	public RequestModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RequestModel(String userName, String userId, String userImage,
			String care_reciever_name) {
		super();
		this.userName = userName;
		this.userId = userId;
		this.userImage = userImage;
		this.care_reciever_name = care_reciever_name;
	}

	public RequestModel(Parcel in) {
		// TODO Auto-generated constructor stub

		userName = in.readString();
		userId = in.readString();
		userImage = in.readString();
		care_reciever_name = in.readString();
		requestID = in.readString();
		reqMsg = in.readString();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public String getCare_reciever_name() {
		return care_reciever_name;
	}

	public void setCare_reciever_name(String care_reciever_name) {
		this.care_reciever_name = care_reciever_name;
	}

	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	public String getReqMsg() {
		return reqMsg;
	}

	public void setReqMsg(String reqMsg) {
		this.reqMsg = reqMsg;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(userName);
		dest.writeString(userId);
		dest.writeString(userImage);
		dest.writeString(care_reciever_name);
		dest.writeString(requestID);
		dest.writeString(reqMsg);
	}

	public static final Parcelable.Creator<RequestModel> CREATOR = new Parcelable.Creator<RequestModel>() {
		public RequestModel createFromParcel(Parcel in) {
			return new RequestModel(in);
		}

		public RequestModel[] newArray(int size) {
			return new RequestModel[size];
		}
	};
}
