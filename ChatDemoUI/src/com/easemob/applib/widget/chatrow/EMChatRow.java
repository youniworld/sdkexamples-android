package com.easemob.applib.widget.chatrow;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.R;
import com.easemob.util.DateUtils;

public abstract class EMChatRow extends LinearLayout {
    protected static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    protected static final int REQUEST_CODE_CONTEXT_MENU = 3;
    protected static final int REQUEST_CODE_MAP = 4;
    protected static final int REQUEST_CODE_TEXT = 5;
    protected static final int REQUEST_CODE_VOICE = 6;
    protected static final int REQUEST_CODE_PICTURE = 7;
    protected static final int REQUEST_CODE_LOCATION = 8;
    protected static final int REQUEST_CODE_NET_DISK = 9;
    protected static final int REQUEST_CODE_FILE = 10;
    protected static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    protected static final int REQUEST_CODE_PICK_VIDEO = 12;
    protected static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
    protected static final int REQUEST_CODE_VIDEO = 14;
    protected static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
    protected static final int REQUEST_CODE_SELECT_USER_CARD = 16;
    protected static final int REQUEST_CODE_SEND_USER_CARD = 17;
    protected static final int REQUEST_CODE_CAMERA = 18;
    protected static final int REQUEST_CODE_LOCAL = 19;
    protected static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
    protected static final int REQUEST_CODE_GROUP_DETAIL = 21;
    protected static final int REQUEST_CODE_SELECT_VIDEO = 23;
    protected static final int REQUEST_CODE_SELECT_FILE = 24;
    protected static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

    protected LayoutInflater inflater;
    protected Context context;
    protected BaseAdapter adapter;
    protected EMMessage message;
    protected int position;

    protected TextView timeStampView;
    protected ImageView userAvatarView;
    protected RelativeLayout bubbleLayout;
    protected TextView usernickView;
    
    protected ProgressBar progressBar;
    protected ImageView statusView;
    
    // protected ChatRowViewHolder viewHolder;
    protected Activity activity;

    public EMChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context);
        this.context = context;
        this.activity = (Activity) context;
        this.message = message;
        this.position = position;
        this.adapter = adapter;
        inflater = LayoutInflater.from(context);

        initView();
    }

    private void initView() {
        onInflatView();
        timeStampView = (TextView) findViewById(R.id.timestamp);
        userAvatarView = (ImageView) findViewById(R.id.iv_userhead);
        bubbleLayout = (RelativeLayout) findViewById(R.id.rl_bubble);
        usernickView = (TextView) findViewById(R.id.tv_userid);
        
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        statusView = (ImageView) findViewById(R.id.msg_status);
        onFindViewById();
    }

    private void setUpBaseView() {
        // 设置用户昵称头像，bubble背景等
        try {
            TextView timestamp = (TextView) findViewById(R.id.timestamp);

            if (position == 0) {
                timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            } else {
                // 两条消息时间离得如果稍长，显示时间
                EMMessage prevMessage = (EMMessage) adapter.getItem(position - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
                    timestamp.setVisibility(View.GONE);
                } else {
                    timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * 根据当前message和position设置控件属性等
     * 
     * @param message
     * @param position
     */
    public void setUpView(EMMessage message, int position) {
        this.message = message;
        this.position = position;
        setUpBaseView();
        onSetUpView();
    }

    protected void sendMsgInBackground(final EMMessage message) {
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

            @Override
            public void onSuccess() {
                updateView(message);
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String error) {
                updateView(message);
            }
        });
    }

    protected void updateView(final EMMessage message) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (message.status == EMMessage.Status.FAIL) {

                    if (message.getError() == EMError.MESSAGE_SEND_INVALID_CONTENT) {
                        Toast.makeText(
                                activity,
                                activity.getString(R.string.send_fail)
                                        + activity.getString(R.string.error_send_invalid_content), 0).show();
                    } else if (message.getError() == EMError.MESSAGE_SEND_NOT_IN_THE_GROUP) {
                        Toast.makeText(
                                activity,
                                activity.getString(R.string.send_fail)
                                        + activity.getString(R.string.error_send_not_in_the_group), 0).show();
                    } else {
                        Toast.makeText(
                                activity,
                                activity.getString(R.string.send_fail)
                                        + activity.getString(R.string.connect_failuer_toast), 0).show();
                    }
                }

                onUpdateView();
            }
        });

    }

    /**
     * 填充layout
     */
    protected abstract void onInflatView();

    /**
     * 查找chatrow里的控件
     */
    protected abstract void onFindViewById();

    /**
     * 消息状态改变，刷新listview
     */
    protected abstract void onUpdateView();

    /**
     * 设置更新控件属性
     */
    protected abstract void onSetUpView();

    // /**
    // * 设置ViewHolder
    // * @return
    // */
    // protected abstract ChatRowViewHolder setViewHolder();

    public static EMChatRow createChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        EMChatRow chatRow = null;
        switch (message.getType()) {
        case TXT:
            chatRow = new EMChatRowText(context, message, position, adapter);
            break;
        case LOCATION:
        	chatRow = new EMChatRowLocation(context, message, position, adapter);
        	break;
        default:
            break;
        }

        return chatRow;
    }

    // class ChatRowViewHolder {
    //
    //
    // }
}
