package com.easemob.applib.widget.chatrow;

import android.content.Context;
import android.widget.BaseAdapter;

import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.R;

public class EMChatRowLocation1 extends EMChatRow{

    public EMChatRowLocation1(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ?
                R.layout.em_row_received_location : R.layout.em_row_sent_location, this);
    }

    @Override
    protected void onFindViewById() {
        
    }

    @Override
    protected void onUpdateView() {
        
    }

    @Override
    protected void onSetUpView() {
        
    }

}
