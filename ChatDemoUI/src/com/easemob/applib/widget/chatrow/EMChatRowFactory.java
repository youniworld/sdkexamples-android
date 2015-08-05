package com.easemob.applib.widget.chatrow;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

import com.easemob.chat.EMMessage;

public class EMChatRowFactory {
    
    
    public static EMChatRow createChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        EMChatRow chatRow = null;
        
        switch (message.getType()) {
        case TXT:
            chatRow = new EMChatRowText(context, message, position, adapter);
            break;
        case LOCATION:
            chatRow = new EMChatRowLocation(context, message, position, adapter);
            break;
        case FILE:
            chatRow = new EMChatRowFile(context, message, position, adapter);
            break;
        case IMAGE:
            chatRow = new EMChatRowImage(context, message, position, adapter);
            break;
        case VOICE:
            chatRow = new EMChatRowVoice(context, message, position, adapter);
            break;
        case VIDEO:
            chatRow = new EMChatRowVideo(context, message, position, adapter);
            break;
        default:
            break;
        }

        return chatRow;
    }
    
}
