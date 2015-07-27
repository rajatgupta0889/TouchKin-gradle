package com.touchKin.touchkinapp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.touchKin.touchkinapp.custom.RoundedImageView;
import com.touchKin.touchkinapp.model.TouchKinComments;
import com.touchKin.touckinapp.R;

public class CommentListAdapter extends BaseAdapter {
	List<TouchKinComments> commentList;
	Context context;
	static LayoutInflater inflater = null;

	public CommentListAdapter(List<TouchKinComments> commentList,
			Context context) {
		super();
		this.commentList = commentList;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private static class ViewHolder {
		TextView commentText, commmentTime, commentDay;
		RoundedImageView userImage;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return commentList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return commentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TouchKinComments comments = (TouchKinComments) getItem(position);
		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.comment_item, null);
			viewHolder = new ViewHolder();
			viewHolder.commentDay = (TextView) convertView
					.findViewById(R.id.commentDayTextView);
			viewHolder.commentText = (TextView) convertView
					.findViewById(R.id.commentTextView);
			viewHolder.commmentTime = (TextView) convertView
					.findViewById(R.id.commentTimeTextView);
			viewHolder.userImage = (RoundedImageView) convertView
					.findViewById(R.id.parentImage);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.commentDay.setText(comments.getCommentDay());
		viewHolder.commentText.setText(comments.getCommentText());
		viewHolder.commmentTime.setText(comments.getCommentTime());
		viewHolder.userImage.setImageDrawable(context.getResources()
				.getDrawable(R.drawable.ic_user_image));
		return convertView;
	}
}
