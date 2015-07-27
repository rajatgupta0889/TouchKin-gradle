package com.touchKin.touchkinapp.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.touchKin.touchkinapp.ContactDialogFragment;
import com.touchKin.touchkinapp.Interface.ButtonClickListener;
import com.touchKin.touchkinapp.adapter.ExpandableListAdapter.Group.Type;
import com.touchKin.touchkinapp.adapter.ExpandableListAdapter.GroupChild.TypeChild;
import com.touchKin.touchkinapp.custom.ImageLoader;
import com.touchKin.touchkinapp.custom.RoundedImageView;
import com.touchKin.touchkinapp.model.ExpandableListGroupItem;
import com.touchKin.touchkinapp.model.ParentListModel;
import com.touchKin.touchkinapp.model.RequestModel;
import com.touchKin.touckinapp.R;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	/*
	 * view types
	 */
	private static final int PAST_TRAVEL_VIEW = 1;
	private static final int FUTURE_TRAVEL_VIEW = 0;
	public static final int ADD_CR = 2;
	public static final int ADD_CG = 3;
	public static final int CONN_REQ = 5;

	String serverPath = "https://s3-ap-southeast-1.amazonaws.com/touchkin-dev/avatars/";

	/*
	 * data
	 */
	private Context context = null;
	ArrayList<Group> groups = new ArrayList<Group>();
	ArrayList<GroupChild> childGroups = new ArrayList<GroupChild>();
	ButtonClickListener listener;

	ImageHolder viewholder;

	public ExpandableListAdapter(Context context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseExpandableListAdapter#getChildType(int, int)
	 */

	public void setButtonListener(ButtonClickListener listener) {
		this.listener = listener;
	}

	@Override
	public int getChildType(int groupPosition, int childPosition) {
		int type = -1;
		if (groups.size() == 2 && groupPosition == 1) {
			type = PAST_TRAVEL_VIEW;
		} else {
			type = FUTURE_TRAVEL_VIEW;
		}

		return type;
	}

	public static class ImageHolder {
		RoundedImageView image;
		TextView name;
		LinearLayout view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseExpandableListAdapter#getChildTypeCount()
	 */
	@Override
	public int getChildTypeCount() {
		// Past and Future Travel Plans
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).child;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getChildView(final int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		// get the type of the group this child belongs
		Type viewType = groups.get(groupPosition).type;
		Log.d("Positon", " child" + childPosition + " Parent" + groupPosition);
		final GroupChild child = (GroupChild) getChild(groupPosition,
				childPosition);

		View view = convertView;
		ImageLoader imageLoader = new ImageLoader(context);
		// imageLoader.DisplayImage(
		// serverPath + groupMember.getUserId() + ".jpeg",
		// R.drawable.ic_user_image, image);

		// if the type is future travel, use the future travel layout
		if (viewType == Type.CONN) {

			view = LayoutInflater.from(context).inflate(
					R.layout.expand_list_first_child, parent, false);
			LinearLayout linearLayout = (LinearLayout) view
					.findViewById(R.id.futureTravelLineItemLayout);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (child._listDataChild != null && child._listDataChild.size() > 0) {
				int resID;
				View view1 = null;
				// if (view1 == null) {
				// viewholder = new ImageHolder();
				//
				// view1 = inflater.inflate(R.layout.image_item, linearLayout,
				// false);
				//
				// viewholder.image = (RoundedImageView) view1
				// .findViewById(R.id.parentImage);
				// viewholder.name = (TextView) view1
				// .findViewById(R.id.parentname);
				//
				// viewholder.view = linearLayout;
				// view1.setTag(viewholder);
				//
				// }
				// viewholder.view.removeAllViews();
				for (int i = 0; i < child._listDataChild.size(); i++) {
					imageLoader = new ImageLoader(context);
					viewholder = new ImageHolder();
					imageLoader = new ImageLoader(context);
					view1 = inflater.inflate(R.layout.image_item_myfamily,
							linearLayout, false);
					viewholder.image = (RoundedImageView) view1
							.findViewById(R.id.parentImage);
					viewholder.name = (TextView) view1
							.findViewById(R.id.parentname);

					viewholder.view = linearLayout;
					view1.setTag(viewholder);
					final String cut = child._listDataChild.get(i)
							.getParentName().substring(0, 1).toLowerCase();
					resID = context.getResources().getIdentifier(cut,
							"drawable", context.getPackageName());
					Log.d("cut", cut + " " + resID);
					viewholder.name.setText(child._listDataChild.get(i)
							.getParentName());
					imageLoader.DisplayImage(serverPath
							+ child._listDataChild.get(i).getParentId()
							+ ".jpeg", resID, viewholder.image);
					viewholder.view.addView(view1);
					// linearLayout.addView(view1);

				}

			}
			View view2 = inflater.inflate(R.layout.image_item_myfamily,
					linearLayout, false);
			final RoundedImageView image = (RoundedImageView) view2
					.findViewById(R.id.parentImage);
			TextView childName = (TextView) view2.findViewById(R.id.parentname);
			childName.setText("Add Kin");
			image.setImageResource(R.drawable.accept);
			image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					listener.onButtonClickListner(
							ADD_CG,
							groups.get(groupPosition).groupMemebr.getMobileNo(),
							false);
				}
			});
			linearLayout.addView(view2);
			if (child.connReq != null) {
				LinearLayout linearLayout1 = (LinearLayout) view
						.findViewById(R.id.futureTravel);
				for (int i = 0; i < child.connReq.size(); i++) {
					int resID;
					imageLoader = new ImageLoader(context);
					View view1 = inflater.inflate(R.layout.connection_req_item,
							linearLayout, false);
					final RoundedImageView tv = (RoundedImageView) view1
							.findViewById(R.id.parentImage);
					final String cut = child.connReq.get(i)
							.getCare_reciever_name().substring(0, 1)
							.toLowerCase();
					resID = context.getResources().getIdentifier(cut,
							"drawable", context.getPackageName());
					Log.d("cut", cut + " " + resID);
					imageLoader.DisplayImage(serverPath
							+ child.connReq.get(i).getUserId() + ".jpeg",
							resID, tv);
					ImageView accept = (ImageView) view1
							.findViewById(R.id.accept);
					ImageView reject = (ImageView) view1
							.findViewById(R.id.reject);
					TextView textMessage = (TextView) view1
							.findViewById(R.id.textMessage);
					textMessage.setText(child.connReq.get(i).getReqMsg());
					final String id = child.connReq.get(i).getRequestID();
					final int pos = i;
					accept.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							ImageLoader imageLoader = new ImageLoader(context);
							LayoutInflater li = LayoutInflater.from(context);
							View custom = li.inflate(R.layout.accept_popup,
									null);
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									context);

							alertDialogBuilder.setView(custom);
							alertDialogBuilder.setCancelable(true);

							// create alert dialog
							final AlertDialog alertDialog = alertDialogBuilder
									.create();
							RoundedImageView image = (RoundedImageView) custom
									.findViewById(R.id.parentImage);
							String cut1 = cut;
							int resID = context.getResources().getIdentifier(
									cut1, "drawable", context.getPackageName());
							imageLoader.DisplayImage(serverPath
									+ child.connReq.get(pos).getUserId()
									+ ".jpeg", resID, image);
							listener.onButtonClickListner(CONN_REQ, id, true);
							child.connReq.remove(pos);
							notifyDataSetChanged();
							Button add = (Button) custom
									.findViewById(R.id.addbutton);
							add.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Toast.makeText(context, "Nick name added",
											Toast.LENGTH_SHORT).show();
									alertDialog.dismiss();
								}
							});

							alertDialog.show();

						}
					});
					reject.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							listener.onButtonClickListner(CONN_REQ, id, false);
							child.connReq.remove(pos);
							notifyDataSetChanged();

						}
					});

					linearLayout1.addView(view1);

				}

			}

		} else {
			return null;
		}
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		Group grp = groups.get(groupPosition);
		if (grp.child != null)
			return 1;
		else {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount() {
		return groups.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		View view = convertView;
		MyLaoutHolder viewHolder;
		if (view == null) {
			viewHolder = new MyLaoutHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.expandable_group_item, parent, false);
			viewHolder.icon = (ImageView) view.findViewById(R.id.drawer);
			viewHolder.userImage = (RoundedImageView) view
					.findViewById(R.id.parentImageView);
			viewHolder.kinCount = (TextView) view.findViewById(R.id.kinInfo);
			viewHolder.name = (TextView) view.findViewById(R.id.userName);
			view.setTag(viewHolder);
		} else {
			viewHolder = (MyLaoutHolder) view.getTag();
		}
		Group grp = groups.get(groupPosition);

		ExpandableListGroupItem groupMember = grp.groupMemebr;
		if (grp.type == Type.CONN) {

			/*
			 * if this is not the first group (future travel) show the arrow
			 * image and change state if necessary
			 */
			ImageLoader imageLoader = new ImageLoader(context);
			int resID;
			String cut = groupMember.getUserName().substring(0, 1)
					.toLowerCase();
			resID = context.getResources().getIdentifier(cut, "drawable",
					context.getPackageName());
			Log.d("cut", cut + " " + resID);
			imageLoader.DisplayImage(serverPath + groupMember.getUserId()
					+ ".jpeg", resID, viewHolder.userImage);

			if (viewHolder.kinCount.getVisibility() == View.INVISIBLE) {
				viewHolder.kinCount.setVisibility(View.VISIBLE);
			}
			if (groupPosition != 0) {
				viewHolder.name.setText(groupMember.getUserName());
			} else {
				viewHolder.name.setText("Me");
			}
			viewHolder.name.setTextSize(20);
			if (groupMember.getReqCount() != null
					&& groupMember.getKinCount() != null) {
				if (groupMember.getKinCount().equals("0")) {
					viewHolder.kinCount
							.setText("Invite someone to care for you");
				} else {
					if (groupMember.getReqCount().equals("0")) {
						viewHolder.kinCount.setText("Your circle has "
								+ groupMember.getKinCount() + " Kin");
					} else {
						viewHolder.kinCount.setText("Your circle has "
								+ groupMember.getKinCount() + " Kin and "
								+ groupMember.getReqCount() + "requests");
					}
				}
			} else if (groupMember.getKinCount() != null) {
				if (groupMember.getKinCount().equalsIgnoreCase("1")) {
					viewHolder.kinCount.setText("Invite someone to care for "
							+ groupMember.getUserName());
				} else {
					viewHolder.kinCount.setText(groupMember.getUserName()
							+ "'s circle" + " has " + groupMember.getKinCount()
							+ " Kin");
				}

			} else {
				viewHolder.kinCount.setText("Click to get details");
			}
			int imageResourceId = isExpanded ? R.drawable.list_open
					: R.drawable.list_close;
			viewHolder.icon.setImageResource(imageResourceId);

		} else {
			ImageLoader imageLoader = new ImageLoader(context);
			int resID;
			String cut = groupMember.getUserName().substring(0, 1)
					.toLowerCase();
			resID = context.getResources().getIdentifier(cut, "drawable",
					context.getPackageName());
			Log.d("cut", cut + " " + resID);
			imageLoader.DisplayImage(serverPath + groupMember.getUserId()
					+ ".jpeg", resID, viewHolder.userImage);
			viewHolder.name.setText("Waiting for " + groupMember.getUserName()
					+ " to accept your request");
			viewHolder.name.setTextSize(16);
			viewHolder.kinCount.setVisibility(View.INVISIBLE);
			int imageResourceId = R.drawable.info;
			viewHolder.icon.setImageResource(imageResourceId);

		}
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	/*
	 * setup travel plans and past trips into groups
	 */
	public void setupTrips(
			HashMap<String, ArrayList<ParentListModel>> careGiver,
			ArrayList<RequestModel> requests,
			ArrayList<ExpandableListGroupItem> CareReciever,
			ArrayList<ExpandableListGroupItem> pendingReq) {
		groups.clear();
		childGroups.clear();

		if (CareReciever != null) {

			for (ExpandableListGroupItem item : CareReciever) {
				Group group = new Group();
				group.type = Type.CONN;
				GroupChild child = new GroupChild();
				group.groupMemebr = item;
				child._listDataChild = careGiver.get(item.getUserId());
				child.typeChild = TypeChild.CR;
				group.child = child;
				groups.add(group);
			}

		}
		if (requests != null) {
			Group temp = groups.get(0);
			temp.child.connReq = requests;
			temp.child.typeChild = TypeChild.ME;

		}
		if (pendingReq != null) {

			for (ExpandableListGroupItem item : pendingReq) {
				Group group = new Group();
				group.type = Type.REQ;
				group.groupMemebr = item;
				groups.add(group);
			}
		}

		notifyDataSetChanged();
	}

	public void setupTripsForCR(
			HashMap<String, ArrayList<ParentListModel>> careGiver, int position) {
		ExpandableListGroupItem item = groups.get(position).groupMemebr;
		groups.get(position).child._listDataChild = careGiver.get(item
				.getUserId());
		notifyDataSetChanged();

	}

	/*
	 * Holder for the Past view type
	 */
	class MyLaoutHolder {
		TextView name;
		TextView kinCount;
		RoundedImageView userImage;
		ImageView icon;
	}

	/*
	 * Holder for the Future view type
	 */

	/*
	 * Wrapper for each group that contains the list elements and the type of
	 * travel.
	 */
	public static class Group {
		public enum Type {
			CONN, REQ;
		};

		public Type type;
		public int groupSIze;
		GroupChild child = null;
		ExpandableListGroupItem groupMemebr;
	}

	public static class GroupChild {
		public enum TypeChild {
			ME, CR;
		};

		public TypeChild typeChild;

		private ArrayList<ParentListModel> _listDataChild = null;
		ArrayList<RequestModel> connReq = null;
	}

}