package com.touchKin.touchkinapp;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.touchKin.touchkinapp.adapter.MessageAdapter;
import com.touchKin.touchkinapp.model.MessageModel;
import com.touchKin.touckinapp.R;

public class MessagesFragment extends Fragment {

	List<MessageModel> messageList;
	MessageAdapter adapter;
	TabHost host;
	String baseImageUrl = "https://s3-ap-southeast-1.amazonaws.com/touchkin-dev/";
	ListView messageListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.messages, null);

		init(v);
		setHasOptionsMenu(true);
		messageList.add(new MessageModel("Hii", "today", "12:20:AM", "Rajat",
				"", ""));
		messageList.add(new MessageModel("Hii how are you??", "today",
				"12:20:AM", "Tom", "", ""));
		messageList.add(new MessageModel("Hii", "today", "12:20:AM", "Rajat",
				"", ""));
		messageList.add(new MessageModel("Hii", "today", "12:20:AM", "Rajat",
				"", ""));
		messageList.add(new MessageModel("Hii", "today", "12:20:AM", "Rajat",
				"", ""));
		adapter.notifyDataSetChanged();
		messageListView.setAdapter(adapter);
		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.tool_bar);
		TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
		mTitle.setText("Messages");
		return v;
	}

	void init(View v) {
		messageList = new ArrayList<MessageModel>();
		adapter = new MessageAdapter(getActivity(), messageList);
		messageListView = (ListView) v.findViewById(R.id.messageListView);

	}

}
