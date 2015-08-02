package com.easemob.applib.widget.chatrow;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.R;

public abstract class EMChatRow extends LinearLayout {

	protected LayoutInflater inflater;
	protected Context context;
	protected BaseAdapter adapter;
	protected EMMessage message;
	protected int position;
	
	protected TextView timeStampView;
	protected ImageView userAvatarView;
	protected RelativeLayout bubbleLayout;
	protected TextView usernickView;
//	protected ChatRowViewHolder viewHolder;

	public EMChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context);
		this.context = context;
		this.message = message;
		this.position = position;
		this.adapter = adapter;
		inflater = LayoutInflater.from(context);
		
		initView();
	}
	
	private void initView(){
		inflatView();
		timeStampView = (TextView) findViewById(R.id.timestamp);
		userAvatarView = (ImageView) findViewById(R.id.iv_userhead);
		bubbleLayout = (RelativeLayout) findViewById(R.id.rl_bubble);
		usernickView = (TextView) findViewById(R.id.tv_userid);
		findViewById();
		
	}
	
	/**
	 * 设置属性等
	 */
	public void setUpView() {
		
	}

	
	


	/**
	 * 填充layout
	 */
	protected abstract void inflatView();
	
	protected abstract void findViewById();
	
//	/**
//	 * 设置ViewHolder
//	 * @return
//	 */
//	protected abstract ChatRowViewHolder setViewHolder();

	
	public static EMChatRow createChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
		EMChatRow chatRow = null;
		switch (message.getType()) {
		case TXT:
			chatRow = new EMChatRowText1(context, message, position, adapter);
			break;
		default:
			break;
		}
		
		return chatRow;
	}

//	class ChatRowViewHolder {
//		
//
//	}
}
