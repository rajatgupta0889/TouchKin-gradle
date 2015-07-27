package com.touchKin.touchkinapp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.touchKin.touchkinapp.model.BluetoothDeviceModel;
import com.touchKin.touckinapp.R;

public class BluetoothDeviceAdapter extends BaseAdapter {
	List<BluetoothDeviceModel> devices;
	Context context;
	LayoutInflater inflater;

	// ButtonClickListener buttonListener;

	public BluetoothDeviceAdapter(List<BluetoothDeviceModel> devices,
			Context context) {
		super();
		this.devices = devices;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	 public void addDevice(BluetoothDeviceModel device) {
         if(!devices.contains(device)) {
        	 devices.add(device);
         }
     }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return devices.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return devices.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public static class ViewHolder {
		TextView deviceName;
		CheckBox checkblestatus;
		// Button addKin, removeKin;
		// RoundedImageView userImage;
	}

	// public void setCustomButtonListner(ButtonClickListener listener) {
	// this.buttonListener = listener;
	// }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		BluetoothDeviceModel item = (BluetoothDeviceModel) getItem(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.bluetooth_scan_list_item,
					null);
			viewHolder.deviceName = (TextView) convertView
					.findViewById(R.id.device_name);
			viewHolder.checkblestatus = (CheckBox) convertView
					.findViewById(R.id.checkBox);
			// viewHolder.addKin = (Button) convertView
			// .findViewById(R.id.addKinButton);
			// viewHolder.removeKin = (Button) convertView
			// .findViewById(R.id.removeKinButton);
			// viewHolder.userImage = (RoundedImageView) convertView
			// .findViewById(R.id.parentImage);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.deviceName.setText(item.getDeviceName());

		viewHolder.checkblestatus.setChecked(item.getCheck());

		return convertView;
	}

}
