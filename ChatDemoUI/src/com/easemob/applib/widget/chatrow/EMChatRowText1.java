package com.easemob.applib.widget.chatrow;

import android.content.Context;
import android.widget.BaseAdapter;

import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.R;

public class EMChatRowText1 extends EMChatRow{

	public EMChatRowText1(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void inflatView() {
		inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ?
				R.layout.em_row_received_message : R.layout.em_row_sent_message, this);
	}

	@Override
	protected void findViewById() {
		
	}

}
