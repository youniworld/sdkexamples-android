package com.easemob.applib.widget.chatrow;

import android.app.Activity;
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
    protected static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    protected static final int REQUEST_CODE_CONTEXT_MENU = 3;
    protected static final int REQUEST_CODE_MAP = 4;
    protected static final int REQUEST_CODE_TEXT = 5;
    protected static final int REQUEST_CODE_VOICE = 6;
    protected static final int REQUEST_CODE_PICTURE = 7;
    protected static final int REQUEST_CODE_LOCATION = 8;
    protected static final int REQUEST_CODE_NET_DISK = 9;
    protected static final int REQUEST_CODE_FILE = 10;
    protected static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    protected static final int REQUEST_CODE_PICK_VIDEO = 12;
    protected static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
    protected static final int REQUEST_CODE_VIDEO = 14;
    protected static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
    protected static final int REQUEST_CODE_SELECT_USER_CARD = 16;
    protected static final int REQUEST_CODE_SEND_USER_CARD = 17;
    protected static final int REQUEST_CODE_CAMERA = 18;
    protected static final int REQUEST_CODE_LOCAL = 19;
    protected static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
    protected static final int REQUEST_CODE_GROUP_DETAIL = 21;
    protected static final int REQUEST_CODE_SELECT_VIDEO = 23;
    protected static final int REQUEST_CODE_SELECT_FILE = 24;
    protected static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

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
    protected Activity activity;

	public EMChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context);
		this.context = context;
		this.activity = (Activity) context;
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
		//TODO: 设置用户昵称头像，bubble背景灯
		setUpView();
	}
	
	


	/**
	 * 填充layout
	 */
	protected abstract void inflatView();
	
	/**
	 * 查找chatrow里的控件
	 */
	protected abstract void findViewById();
	
	/**
	 * 设置控件属性等
	 */
	protected abstract void setUpView();
	
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
