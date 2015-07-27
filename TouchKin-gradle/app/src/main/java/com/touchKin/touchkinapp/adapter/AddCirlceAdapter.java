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
import com.touchKin.touchkinapp.model.AddCircleModel;
import com.touchKin.touckinapp.R;

public class AddCirlceAdapter extends BaseAdapter {
	List<AddCircleModel> circles;
	Context context;
	LayoutInflater inflater;
	ButtonClickListener buttonListener;
	String serverPath = "https://s3-ap-southeast-1.amazonaws.com/touchkin-dev/avatars/";

	public AddCirlceAdapter(List<AddCircleModel> circles, Context context) {
		super();
		this.circles = circles;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return circles.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return circles.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private static class ViewHolder {
		TextView userName;
		Button addKin, removeKin;
		RoundedImageView userImage;
	}

	public void setCustomButtonListner(ButtonClickListener listener) {
		this.buttonListener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		AddCircleModel item = (AddCircleModel) getItem(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.add_to_circle_item, null);
			viewHolder.userName = (TextView) convertView
					.findViewById(R.id.parentName);
			viewHolder.addKin = (Button) convertView
					.findViewById(R.id.addKinButton);
			viewHolder.removeKin = (Button) convertView
					.findViewById(R.id.removeKinButton);
			viewHolder.userImage = (RoundedImageView) convertView
					.findViewById(R.id.parentImage);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.userName.setText(item.getUserName());
		viewHolder.addKin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buttonListener.onButtonClickListner(position, null, true);

			}
		});
		viewHolder.removeKin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buttonListener.onButtonClickListner(position, null, false);
			}
		});
		ImageLoader imageLoader = new ImageLoader(context);
		imageLoader.DisplayImage(serverPath + item.getUserId() + ".jpeg",
				R.drawable.ic_user_image, viewHolder.userImage);

		// viewHolder.userImage.setImageUrl("", AppController.getInstance()
		// .getImageLoader());
		return convertView;
	}

}
