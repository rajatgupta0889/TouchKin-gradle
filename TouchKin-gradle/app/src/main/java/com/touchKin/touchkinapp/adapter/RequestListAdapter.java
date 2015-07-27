package com.touchKin.touchkinapp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.touchKin.touchkinapp.Interface.ButtonClickListener;
import com.touchKin.touchkinapp.custom.ImageLoader;
import com.touchKin.touchkinapp.custom.RoundedImageView;
import com.touchKin.touchkinapp.model.RequestModel;
import com.touchKin.touckinapp.R;

public class RequestListAdapter extends BaseAdapter {
	List<RequestModel> requestList;
	Context context;
	LayoutInflater inflater;
	ButtonClickListener customListener;
	String serverPath = "https://s3-ap-southeast-1.amazonaws.com/touchkin-dev/avatars/";

	public RequestListAdapter(List<RequestModel> requestList, Context context) {

		super();
		this.requestList = requestList;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return requestList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return requestList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void setCustomButtonListner(ButtonClickListener listener) {
		this.customListener = listener;
	}

	private static class ViewHolder {
		Button accept, reject;
		TextView request;
		RoundedImageView userImage;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final RequestModel request = (RequestModel) getItem(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.parent_request_item, null);
			viewHolder = new ViewHolder();
			viewHolder.request = (TextView) convertView
					.findViewById(R.id.requestTextView);
			viewHolder.accept = (Button) convertView
					.findViewById(R.id.accept_button);
			viewHolder.reject = (Button) convertView
					.findViewById(R.id.rejectButton);
			viewHolder.userImage = (RoundedImageView) convertView
					.findViewById(R.id.parentImage);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.request.setText(request.getReqMsg());
		viewHolder.accept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (customListener != null)
					customListener.onButtonClickListner(position,
							request.getRequestID(), true);
				requestList.remove(request);
				notifyDataSetChanged();
			}
		});
		viewHolder.reject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (customListener != null)
					customListener.onButtonClickListner(position,
							request.getRequestID(), false);
				requestList.remove(request);
				notifyDataSetChanged();
			}
		});
		ImageLoader imageLoader = new ImageLoader(context);

		imageLoader.DisplayImage(serverPath + request.getUserId() + ".jpeg",
				R.drawable.ic_user_image, viewHolder.userImage);

		return convertView;
	}
}
