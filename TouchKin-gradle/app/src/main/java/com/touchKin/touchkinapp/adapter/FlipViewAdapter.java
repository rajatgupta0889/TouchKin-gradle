package com.touchKin.touchkinapp.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.touchKin.touchkinapp.DashBoardActivity;
import com.touchKin.touchkinapp.Fragment1;
import com.touchKin.touchkinapp.VideoFullScreen;
import com.touchKin.touchkinapp.Interface.ButtonClickListener;
import com.touchKin.touchkinapp.custom.ImageLoader;
import com.touchKin.touchkinapp.custom.RoundedImageView;
import com.touchKin.touchkinapp.model.TouchKinBookModel;
import com.touchKin.touckinapp.R;

public class FlipViewAdapter extends BaseAdapter {
	List<TouchKinBookModel> touckinBook;
	Context context;
	LayoutInflater inflater;
	// List<TouchKinComments> comments;
	// CommentListAdapter adapter;
	TouchKinBookModel touchKinBook;
	ButtonClickListener customListener;
	TextView videoText, videoTime, videoDay, videoSenderName, videoViewCount;
	RoundedImageView userImage, videoSenderImage;
	// EditText commentEditText;
	Button profilepic, backbutton, likebutton, delete;
	VideoView videoView = null;
	ImageView imageView, cancel;
	FragmentTabHost host;

	Bitmap thumbnail;
	String serverPath = "https://s3-ap-southeast-1.amazonaws.com/touchkin-dev/avatars/";

	public FlipViewAdapter(List<TouchKinBookModel> touckinBook, Context context) {
		super();
		this.touckinBook = touckinBook;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public void setCustomButtonListner(ButtonClickListener listener) {
		this.customListener = listener;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return touckinBook.size();
	}

	@Override
	public TouchKinBookModel getItem(int position) {
		// TODO Auto-generated method stub
		return touckinBook.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private static class ViewHolder {
		TextView videoText, videoTime, videoDay;

		ImageView imageView, cancel;
		RoundedImageView videoSenderImage;
		// EditText commentEditText;
		Button profilepic, likebutton, delete;
		// VideoView videoView;
		// ListView commentList;
	}

	ViewHolder viewHolder;

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (position == 0) {
			convertView = inflater.inflate(R.layout.kinbook_front_page, null);
			return convertView;
		} else {
			// if (convertView == null && viewHolder == null) {
			convertView = inflater.inflate(R.layout.flipview_layout, null);
			viewHolder = new ViewHolder();
			// videoText = (TextView) convertView
			// .findViewById(R.id.videoCommentTextView);
			// viewHolder.videoText = (TextView) convertView
			// .findViewById(R.id.videoCommentTextView);
			// viewHolder.videoTime = (TextView) convertView
			// .findViewById(R.id.videoTimeTextView);
			// viewHolder.videoDay = (TextView) convertView
			// .findViewById(R.id.videoDayTextView);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView);
			viewHolder.cancel = (ImageView) convertView
					.findViewById(R.id.cancel);
			// viewHolder.userImage = (RoundedImageView) convertView
			// .findViewById(R.id.userImage);
			// videoSenderName = (TextView) convertView
			// .findViewById(R.id.videoSenderNameTextView);
			// videoViewCount = (TextView) convertView
			// .findViewById(R.id.videoSeenCountTextView);
			viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
			viewHolder.videoSenderImage = (RoundedImageView) convertView
					.findViewById(R.id.senderImage);
			// viewHolder.videoView = (VideoView) convertView
			// .findViewById(R.id.videoView);
			viewHolder.profilepic = (Button) convertView
					.findViewById(R.id.profile_pic);
			// backbutton = (Button)
			// convertView.findViewById(R.id.back_button);
			viewHolder.likebutton = (Button) convertView
					.findViewById(R.id.like_button);
			// viewHolder.commentEditText = (EditText) convertView
			// .findViewById(R.id.commentEditText);

			// viewHolder.commentList = (ListView) convertView
			// .findViewById(R.id.commentList);

			convertView.setTag(viewHolder);
			// } else {
			// viewHolder = (ViewHolder) convertView.getTag();
			// }

			final TouchKinBookModel touchKinBook = getItem(position);
			Log.d("videouri", "" + touchKinBook.getVideouri());
			// viewHolder.videoView.setVideoURI(videouri);
			ImageLoader imageLoader = new ImageLoader(context);
			imageLoader.DisplayImage(serverPath + touchKinBook.getUserId()
					+ ".jpeg", R.drawable.ic_user_image,
					viewHolder.videoSenderImage);
			viewHolder.profilepic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(context, VideoFullScreen.class);
					intent.putExtra("thumbnail", thumbnail);
					intent.putExtra("videopath", getItem(position)
							.getVideouri());
					context.startActivity(intent);

				}
			});
			viewHolder.cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Toolbar toolbar = (Toolbar) ((DashBoardActivity) context)
					// .findViewById(R.id.tool_bar);
					// host = ((DashBoardActivity) context).getTabHost();
					// host.setVisibility(View.VISIBLE);
					// toolbar.setVisibility(View.VISIBLE);
					// host.setCurrentTab(0);
					//
					// context.getSupportFragmentManager().executePendingTransactions();
					//
					// ((Fragment1)
					// getSupportFragmentManager().findFragmentByTag(
					// "DashBoard")).notifyFrag();
					customListener.onButtonClickListner(1000, "", false);
				}
			});

			viewHolder.likebutton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(
							context,
							"You like video of your kin "
									+ touchKinBook.getVideoSenderName(),
							Toast.LENGTH_LONG).show();
					customListener.onButtonClickListner(0,
							touchKinBook.getMessageId(), true);
				}
			});
			viewHolder.delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					customListener.onButtonClickListner(1,
							touchKinBook.getMessageId(), false);
				}
			});
			// viewHolder.videoText.setText(touchKinBook.getVideoText());
			// viewHolder.videoTime.setText(touchKinBook.getVideoDate());
			// viewHolder.videoDay.setText(touchKinBook.getVideoDay());
			ImageLoader imageloader = new ImageLoader(context);
			imageloader.DisplayImage(
					"https://s3-ap-southeast-1.amazonaws.com/touchkin-dev/"
							+ touchKinBook.getVideoId() + "-thumb-00001.png",
					R.drawable.ic_user_image, viewHolder.imageView);

		}
		return convertView;
	}

}
