package com.easemob.applib.widget.chatrow;

import android.content.Context;

import com.easemob.applib.Constant;
import com.easemob.chat.EMMessage;

public class EMChatRowFactory {
//    public static EMChatRow createChatRow(Context context, EMMessage message){
//        switch (message.getType()) {
//        //先让自定义的处理，如果返回false则还是lib处理
//        case LOCATION:
//            return new EMChatRowLocation(context, message, position, parent, this);
//        case IMAGE:
//            return new EMChatRowImage(context, message, position, parent, this);
//        case VOICE:
//            return new EMChatRowVoice(context, message, position, parent, this);
//        case VIDEO:
//            return new EMChatRowVideo(context, message, position, parent, this);
//        case FILE:
//            return new EMChatRowFile(context, message, position, parent, this);
//        default:
//            // 语音通话,  视频通话
//            if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
//                message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
//                return new EMChatRowCall(context, message, position, parent, this);
//            else
//                return new EMChatRowText(context, message, position, parent, this);
//        
//        
//        return null;
//    }
}
