package com.easemob.applib.widget.chatrow;

import com.easemob.applib.Constant;
import com.easemob.chat.EMMessage;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EMChatRow extends LinearLayout{

    protected Context context;

    public EMChatRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EMChatRow(Context context) {
        super(context);
        init(context, null);
    }
    
    private void init(Context context, AttributeSet attrs){
        this.context = context;
    }
    
    public EMChatRow createChatRow(EMMessage message){
        switch (message.getType()) {
        case LOCATION:
            return new EMChatRowLocation(context, message, position, parent, this);
        case IMAGE:
            return new EMChatRowImage(context, message, position, parent, this);
        case VOICE:
            return new EMChatRowVoice(context, message, position, parent, this);
        case VIDEO:
            return new EMChatRowVideo(context, message, position, parent, this);
        case FILE:
            return new EMChatRowFile(context, message, position, parent, this);
        default:
            // 语音通话,  视频通话
            if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
                message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
                return new EMChatRowCall(context, message, position, parent, this);
            else
                return new EMChatRowText(context, message, position, parent, this);
    }
    
    
    class ChatRowBaseViewHolder {
        TextView timeStampView;
        ImageView userAvatarView;
        RelativeLayout bubbleLayout;
        TextView usernickView;
        
    }
}
