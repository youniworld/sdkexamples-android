package com.easemob.chatuilib.widget.chatrow;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Direct;
import com.easemob.chatuilib.R;
import com.easemob.chatuilib.utils.UserUtils;
import com.easemob.chatuilib.widget.EMChatMessageList;
import com.easemob.chatuilib.widget.EMChatMessageList.MessageListItemClickListener;
import com.easemob.chatuilib.widget.adapter.MessageAdapter;
import com.easemob.util.DateUtils;

public abstract class EMChatRow extends LinearLayout {
    protected static final String TAG = EMChatRow.class.getSimpleName();

    protected LayoutInflater inflater;
    protected Context context;
    protected BaseAdapter adapter;
    protected EMMessage message;
    protected int position;

    protected TextView timeStampView;
    protected ImageView userAvatarView;
    protected View bubbleLayout;
    protected TextView usernickView;

    protected ProgressBar progressBar;
    protected ImageView statusView;
    protected Activity activity;

    protected TextView ackedView;
    protected TextView deliveredView;

    protected EMCallBack sendMessageCallBack;

    protected MessageListItemClickListener itemClickListener;

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
        bubbleLayout = findViewById(R.id.bubble);
        usernickView = (TextView) findViewById(R.id.tv_userid);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        statusView = (ImageView) findViewById(R.id.msg_status);
        ackedView = (TextView) findViewById(R.id.tv_ack);
        deliveredView = (TextView) findViewById(R.id.tv_delivered);

        onFindViewById();
    }

    /**
     * 根据当前message和position设置控件属性等
     * 
     * @param message
     * @param position
     */
    public void setUpView(EMMessage message, int position,
            EMChatMessageList.MessageListItemClickListener itemClickListener) {
        this.message = message;
        this.position = position;
        this.itemClickListener = itemClickListener;

        setUpBaseView();
        onSetUpView();
        setClickListener();
    }

    private void setUpBaseView() {
        // 设置用户昵称头像，bubble背景等
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
        if (timestamp != null) {
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
        }
        //设置头像和nick
        if(message.direct == Direct.SEND){
            UserUtils.setUserAvatar(context, EMChatManager.getInstance().getCurrentUser(), userAvatarView);
            UserUtils.setUserNick(EMChatManager.getInstance().getCurrentUser(), usernickView);
        }else{
            UserUtils.setUserAvatar(context, message.getFrom(), userAvatarView);
            UserUtils.setUserNick(message.getFrom(), usernickView);
        }

        if (adapter instanceof MessageAdapter) {
            if (((MessageAdapter) adapter).showAvatar)
                userAvatarView.setVisibility(View.VISIBLE);
            else
                userAvatarView.setVisibility(View.GONE);
            if (usernickView != null) {
                if (((MessageAdapter) adapter).showUserNick)
                    usernickView.setVisibility(View.VISIBLE);
                else
                    usernickView.setVisibility(View.GONE);
            }
            if (message.direct == Direct.SEND) {
                if (((MessageAdapter) adapter).myBubbleBg != null)
                    bubbleLayout.setBackgroundDrawable(((MessageAdapter) adapter).myBubbleBg);
                // else
                // bubbleLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.chatto_bg));
            } else if (message.direct == Direct.RECEIVE) {
                if (((MessageAdapter) adapter).otherBuddleBg != null)
                    bubbleLayout.setBackgroundDrawable(((MessageAdapter) adapter).otherBuddleBg);
//                else
//                    bubbleLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.em_chatfrom_bg));
            }
        }


    }

    private void setClickListener() {
        bubbleLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (itemClickListener != null){
                    if(!itemClickListener.onBubbleClick(message)){
                        onBuubleClick();
                    }
                }
            }
        });

        bubbleLayout.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onBubbleLongClick(message);
                }
                return true;
            }
        });

        if (statusView != null) {
            statusView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onResendClick(message);
                    }
                }
            });
        }

        userAvatarView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    if (message.direct == Direct.SEND) {
                        itemClickListener.onUserAvatarClick(EMChatManager.getInstance().getCurrentUser());
                    } else {
                        itemClickListener.onUserAvatarClick(message.getFrom());
                    }
                }
            }
        });
    }

    protected void sendMsgInBackground(final EMMessage message) {
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

            @Override
            public void onSuccess() {
                updateView();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String error) {
                updateView();
            }
        });
    }

    protected void updateView() {
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
    
    /**
     * 聊天气泡被点击事件
     */
    protected abstract void onBuubleClick();

}
