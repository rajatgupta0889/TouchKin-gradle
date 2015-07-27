package com.touchKin.touchkinapp.model;

public class BluetoothDeviceModel {
	String deviceId, deviceName, deviceRssi;
	Boolean check;

	public BluetoothDeviceModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BluetoothDeviceModel(String deviceId, String deviceName,
			Boolean check) {
		super();
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.check = check;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Boolean getCheck() {
		return check;
	}

	public void setCheck(Boolean check) {
		this.check = check;
	}

	public String getDeviceRssi() {
		return deviceRssi;
	}

	public void setDeviceRssi(String deviceRssi) {
		this.deviceRssi = deviceRssi;
	}
}
