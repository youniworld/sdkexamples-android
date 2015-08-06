package com.easemob.applib.widget.chatrow;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.ShowNormalFileActivity;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.FileUtils;
import com.easemob.util.TextFormater;

public class EMChatRowFile extends EMChatRow{

    protected TextView fileNameView;
	protected TextView fileSizeView;
    protected TextView fileStateView;
    protected TextView percentageView;
    
    protected EMCallBack sendfileCallBack;
    
    protected boolean isNotifyProcessed;
    private NormalFileMessageBody fileMessageBody;

    public EMChatRowFile(Context context, EMMessage message, int position, BaseAdapter adapter) {
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
	    fileMessageBody = (NormalFileMessageBody) message.getBody();
        String filePath = fileMessageBody.getLocalUrl();
        fileNameView.setText(fileMessageBody.getFileName());
        fileSizeView.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
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
        handleSendMessage(fileMessageBody);
	}

	/**
	 * 处理发送消息
	 * @param fileMessageBody
	 */
    protected void handleSendMessage(final FileMessageBody fileMessageBody) {
        switch (message.status) {
        case SUCCESS:
            progressBar.setVisibility(View.INVISIBLE);
            if(percentageView != null)
                percentageView.setVisibility(View.INVISIBLE);
            statusView.setVisibility(View.INVISIBLE);
            break;
        case FAIL:
            progressBar.setVisibility(View.INVISIBLE);
            if(percentageView != null)
                percentageView.setVisibility(View.INVISIBLE);
            statusView.setVisibility(View.VISIBLE);
            break;
        case INPROGRESS:
            progressBar.setVisibility(View.VISIBLE);
            if(percentageView != null){
                percentageView.setVisibility(View.VISIBLE);
                percentageView.setText(message.progress + "%");
            }
            statusView.setVisibility(View.INVISIBLE);
            if(sendfileCallBack == null){
                sendfileCallBack = new EMCallBack() {
                    
                    @Override
                    public void onSuccess() {
                        updateView();
                    }
                    
                    @Override
                    public void onProgress(final int progress, String status) {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                if(percentageView != null){
                                    percentageView.setText(progress + "%");
                                }
                            }
                        });
                    }
                    
                    @Override
                    public void onError(int code, String message) {
                        updateView();
                    }
                };
                fileMessageBody.setSendCallBack(sendfileCallBack);
            }
            break;
        default:
            progressBar.setVisibility(View.VISIBLE);
            if(percentageView != null){
                percentageView.setVisibility(View.VISIBLE);
                percentageView.setText(message.progress + "%");
            }
            statusView.setVisibility(View.INVISIBLE);
            // 发送消息
            sendMsgInBackground(message);
            break;
        }
    }
	
    /**
     * 显示接收文件进度
     * @param fileMessageBody
     */
    protected void showDownloadPregress(FileMessageBody fileMessageBody) {
        final FileMessageBody msgbody = (FileMessageBody) message.getBody();
        progressBar.setVisibility(View.VISIBLE);
        if(percentageView !=null)
            percentageView.setVisibility(View.VISIBLE);

        msgbody.setDownloadCallback(new EMCallBack() {

            @Override
            public void onSuccess() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateView();
                    }
                });
            }

            @Override
            public void onError(int code, String message) {
                updateView();
            }

            @Override
            public void onProgress(final int progress, String status) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(percentageView != null)
                            percentageView.setText(progress + "%");

                    }
                });

            }

        });
    }
    
	
	@Override
	protected void sendMsgInBackground(final EMMessage message) {
	    EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            
            @Override
            public void onSuccess() {
                updateView();
            }
            
            @Override
            public void onProgress(int progress, String status) {
                if(!isNotifyProcessed){
                    isNotifyProcessed = true;
                    updateView();
                }
            }
            
            @Override
            public void onError(int code, String error) {
                updateView();
            }
        });
	}

	@Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onBuubleClick() {
        String filePath = fileMessageBody.getLocalUrl();
        File file = new File(filePath);
        if (file != null && file.exists()) {
            // 文件存在，直接打开
            FileUtils.openFile(file, (Activity) context);
        } else {
            // 下载
            context.startActivity(new Intent(context, ShowNormalFileActivity.class).putExtra("msgbody", message.getBody()));
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
}
