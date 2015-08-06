package com.easemob.applib.widget.chatrow;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMMessage;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.activity.ContextMenu;
import com.easemob.chatuidemo.adapter.VoicePlayClickListener;
import com.easemob.util.EMLog;

public class EMChatRowVoice extends EMChatRowFile{

    private ImageView voiceImageView;
    private TextView voiceLengthView;
    private ImageView readStutausView;

    public EMChatRowVoice(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ?
                R.layout.em_row_received_voice : R.layout.em_row_sent_voice, this);
    }

    @Override
    protected void onFindViewById() {
        voiceImageView = ((ImageView) findViewById(R.id.iv_voice));
        voiceLengthView = (TextView) findViewById(R.id.tv_length);
        readStutausView = (ImageView) findViewById(R.id.iv_unread_voice);
    }

    @Override
    protected void onSetUpView() {
        VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
        int len = voiceBody.getLength();
        if(len>0){
            voiceLengthView.setText(voiceBody.getLength() + "\"");
            voiceLengthView.setVisibility(View.VISIBLE);
        }else{
            voiceLengthView.setVisibility(View.INVISIBLE);
        }
        if (VoicePlayClickListener.playMsgId != null
                && VoicePlayClickListener.playMsgId.equals(message.getMsgId()) && VoicePlayClickListener.isPlaying) {
            AnimationDrawable voiceAnimation;
            if (message.direct == EMMessage.Direct.RECEIVE) {
                voiceImageView.setImageResource(R.anim.voice_from_icon);
            } else {
                voiceImageView.setImageResource(R.anim.voice_to_icon);
            }
            voiceAnimation = (AnimationDrawable) voiceImageView.getDrawable();
            voiceAnimation.start();
        } else {
            if (message.direct == EMMessage.Direct.RECEIVE) {
                voiceImageView.setImageResource(R.drawable.chatfrom_voice_playing);
            } else {
                voiceImageView.setImageResource(R.drawable.chatto_voice_playing);
            }
        }
        
        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (message.isListened()) {
                // 隐藏语音未听标志
                readStutausView.setVisibility(View.INVISIBLE);
            } else {
                readStutausView.setVisibility(View.VISIBLE);
            }
            EMLog.d(TAG, "it is receive msg");
            if (message.status == EMMessage.Status.INPROGRESS) {
                progressBar.setVisibility(View.VISIBLE);
                showDownloadPregress(voiceBody);
            } else {
                progressBar.setVisibility(View.INVISIBLE);

            }
            return;
        }

        // until here, deal with send voice msg
        handleSendMessage(voiceBody);
    }

    @Override
    protected void onUpdateView() {
        super.onUpdateView();
    }

    @Override
    protected void onBuubleClick() {
        new VoicePlayClickListener(message, voiceImageView, readStutausView, adapter, activity).onClick(bubbleLayout);
    }
    
}
