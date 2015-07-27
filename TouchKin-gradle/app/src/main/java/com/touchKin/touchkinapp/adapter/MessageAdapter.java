package com.touchKin.touchkinapp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.touchKin.touchkinapp.custom.RoundedImageView;
import com.touchKin.touchkinapp.model.MessageModel;
import com.touchKin.touckinapp.R;

public class MessageAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater mLayoutInflater;

	List<MessageModel> messageList;

	public MessageAdapter(Context mContext, List<MessageModel> messageList) {
		super();
		this.mContext = mContext;
		this.messageList = messageList;
		mLayoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return messageList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return messageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.messages_list_item,
					null);
		}
		MessageModel item = messageList.get(position);
		RoundedImageView imageView = (RoundedImageView) convertView
				.findViewById(R.id.parentImage);
		imageView.setImageResource(R.drawable.ic_user_image);
		TextView messageTextView = (TextView) convertView
				.findViewById(R.id.message);
		TextView messageDay = (TextView) convertView
				.findViewById(R.id.messageDay);
		TextView messageSender = (TextView) convertView
				.findViewById(R.id.messageSenderName);

		messageTextView.setText(item.getMessage());
		messageDay.setText(item.getMessageDay());
		messageSender.setText(item.getUserName());
		return convertView;
	}

}
