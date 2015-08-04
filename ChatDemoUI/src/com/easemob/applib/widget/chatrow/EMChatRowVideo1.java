package com.easemob.applib.widget.chatrow;

import android.content.Context;
import android.widget.BaseAdapter;

import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.R;

public class EMChatRowVideo1 extends EMChatRowFile{

	public EMChatRowVideo1(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflatView() {
		inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ?
				R.layout.em_row_received_video : R.layout.em_row_sent_video, this);
	}

	@Override
	protected void onFindViewById() {
		super.onFindViewById();
	}

	@Override
	protected void onSetUpView() {
		super.onSetUpView();
	}
	
	

}
