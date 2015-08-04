package com.easemob.applib.widget.chatrow;

import java.io.File;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.ShowNormalFileActivity;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.FileUtils;
import com.easemob.util.TextFormater;

public class EMChatRowFile1 extends EMChatRow{

    protected TextView fileNameView;
	protected TextView fileSizeView;
    protected TextView fileStateView;
    protected TextView percentageView;
    
    protected EMCallBack sendfileCallBack;
    
    /**
     * 是否为当前页面发送的消息，当用户finish此页面，然后再进来，如果消息还是发送状态，此属性为false
     * 目的是为了防止重复注册send callback
     */
    protected boolean isCurrentSending;

    public EMChatRowFile1(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflatView() {
	    inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ? 
	            R.layout.em_row_received_file : R.layout.em_row_sent_file, this);
	}

	@Override
	protected void onFindViewById() {
	    fileNameView = (TextView) findViewById(R.id.tv_file_name);
        fileSizeView = (TextView) findViewById(R.id.tv_file_size);
        fileStateView = (TextView) findViewById(R.id.tv_file_state);
        percentageView = (TextView) findViewById(R.id.percentage);
	}


	@Override
	protected void onSetUpView() {
	    final NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) message.getBody();
        final String filePath = fileMessageBody.getLocalUrl();
        fileNameView.setText(fileMessageBody.getFileName());
        fileSizeView.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
        bubbleLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                File file = new File(filePath);
                if (file != null && file.exists()) {
                    // 文件存在，直接打开
                    FileUtils.openFile(file, (Activity) context);
                } else {
                    // 下载
                    context.startActivity(new Intent(context, ShowNormalFileActivity.class).putExtra("msgbody", fileMessageBody));
                }
                if (message.direct == EMMessage.Direct.RECEIVE && !message.isAcked) {
                    try {
                        EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                        message.isAcked = true;
                    } catch (EaseMobException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        if (message.direct == EMMessage.Direct.RECEIVE) { // 接收的消息
            File file = new File(filePath);
            if (file != null && file.exists()) {
                fileStateView.setText(R.string.Have_downloaded);
            } else {
                fileStateView.setText(R.string.Did_not_download);
            }
            return;
        }

        // until here, deal with send voice msg
        switch (message.status) {
        case SUCCESS:
            progressBar.setVisibility(View.INVISIBLE);
            percentageView.setVisibility(View.INVISIBLE);
            statusView.setVisibility(View.INVISIBLE);
            break;
        case FAIL:
            progressBar.setVisibility(View.INVISIBLE);
            percentageView.setVisibility(View.INVISIBLE);
            statusView.setVisibility(View.VISIBLE);
            break;
        case INPROGRESS:
            progressBar.setVisibility(View.VISIBLE);
            percentageView.setVisibility(View.INVISIBLE);
            statusView.setVisibility(View.INVISIBLE);
            if(sendfileCallBack == null){
                sendfileCallBack = new EMCallBack() {
                    
                    @Override
                    public void onSuccess() {
                        onUpdateView();
                    }
                    
                    @Override
                    public void onProgress(int progress, String status) {
                        percentageView.setText(progress + "%");
                    }
                    
                    @Override
                    public void onError(int code, String message) {
                        onUpdateView();
                    }
                };
            }
            fileMessageBody.setDownloadCallback(sendfileCallBack);
            break;
        default:
            progressBar.setVisibility(View.VISIBLE);
            percentageView.setVisibility(View.INVISIBLE);
            statusView.setVisibility(View.INVISIBLE);
            // 发送消息
            sendMsgInBackground(message);
            break;
        }
	}
	
	@Override
	protected void sendMsgInBackground(final EMMessage message) {
	    EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            
            @Override
            public void onSuccess() {
                updateView(message);
            }
            
            @Override
            public void onProgress(int progress, String status) {
//                if(isCurrentSending)
            }
            
            @Override
            public void onError(int code, String error) {
                updateView(message);
            }
        });
	}

	@Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }
}
