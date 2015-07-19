package com.easemob.applib.widget;

import com.easemob.chatuidemo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public class EMChatMenu extends RelativeLayout{

	public EMChatMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public EMChatMenu(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public EMChatMenu(Context context) {
		super(context);
		init(context, null);
	}
	
	void init(Context context, AttributeSet attrs){
		LayoutInflater.from(context).inflate(R.layout.em_chat_menu, this);
	}
	
}
