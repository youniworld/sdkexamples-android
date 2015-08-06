package com.easemob.applib.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.applib.model.GroupRemoveListener;
import com.easemob.applib.widget.EMAlertDialog;
import com.easemob.applib.widget.EMAlertDialog.AlertDialogUser;
import com.easemob.applib.widget.EMChatExtendMenu.ChatExtendMenuItemClickListener;
import com.easemob.applib.widget.EMChatInputMenu;
import com.easemob.applib.widget.EMChatInputMenu.ChatInputMenuListener;
import com.easemob.applib.widget.EMChatMessageList;
import com.easemob.applib.widget.EMChatMessageList.MessageListItemClickListener;
import com.easemob.applib.widget.EMTitleBar;
import com.easemob.applib.widget.EMVoiceRecorderView;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.AlertDialog;
import com.easemob.chatuidemo.activity.BaiduMapActivity;
import com.easemob.chatuidemo.activity.ContextMenu;
import com.easemob.chatuidemo.activity.ForwardMessageActivity;
import com.easemob.chatuidemo.activity.GroupDetailsActivity;
import com.easemob.chatuidemo.activity.ImageGridActivity;
import com.easemob.chatuidemo.activity.VideoCallActivity;
import com.easemob.chatuidemo.activity.VoiceCallActivity;
import com.easemob.chatuidemo.utils.CommonUtils;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;

public class EMChatFragment extends Fragment implements EMEventListener {
    private static final String TAG = "ChatActivity";
    private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    private static final int REQUEST_CODE_MAP = 4;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int REQUEST_CODE_PICK_VIDEO = 12;
    public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
    public static final int REQUEST_CODE_VIDEO = 14;
    public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
    public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
    public static final int REQUEST_CODE_SEND_USER_CARD = 17;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;
    public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
    public static final int REQUEST_CODE_GROUP_DETAIL = 21;
    public static final int REQUEST_CODE_SELECT_VIDEO = 23;
    public static final int REQUEST_CODE_SELECT_FILE = 24;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;


    protected int chatType;
    protected String toChatUsername;
    protected EMChatMessageList messageList;
    protected EMChatInputMenu inputMenu;

    protected EMConversation conversation;
    protected EMTitleBar titleBar;
    
    protected InputMethodManager inputManager;
    private ClipboardManager clipboard;

    protected Handler handler = new Handler();
    protected File cameraFile;
    protected EMVoiceRecorderView voiceRecorder;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ListView listView;

    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected final int pagesize = 20;
    private GroupListener groupListener;
    private EMMessage contextMenuMessage;
    
    static final int ITEM_TAKE_PICTURE = 1;
    static final int ITEM_PICTURE = 2;
    static final int ITEM_LOCATION = 3;
    static final int ITEM_VIDEO = 4;
    static final int ITEM_FILE = 5;
    static final int ITEM_VOICE_CALL = 6;
    static final int ITEM_VIDEO_CALL = 7;
    
    protected int[] itemStrings = { R.string.attach_take_pic, R.string.attach_picture, R.string.attach_location,
            R.string.attach_video, R.string.attach_file, R.string.attach_voice_call, R.string.attach_video_call };
    protected int[] itemdrawables = { R.drawable.chat_takepic_selector, R.drawable.chat_image_selector,
            R.drawable.chat_location_selector, R.drawable.chat_video_selector, R.drawable.chat_file_selector,
            R.drawable.chat_voice_call_selector, R.drawable.chat_video_call_selector };
    protected int[] itemIds = { ITEM_TAKE_PICTURE, ITEM_PICTURE, ITEM_LOCATION, ITEM_VIDEO, ITEM_FILE, ITEM_VOICE_CALL,
            ITEM_VIDEO_CALL };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.em_fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        // 判断单聊还是群聊
        chatType = args.getInt("chatType", EMChatMessageList.CHATTYPE_SINGLE);
        // 会话人或群组id
        toChatUsername = args.getString("userId");

        initView();
        setUpView();
    }

    protected void initView() {
        // 标题栏
        titleBar = (EMTitleBar) getView().findViewById(R.id.title_bar);

        // 按住说话录音控件
        voiceRecorder = (EMVoiceRecorderView) getView().findViewById(R.id.voice_recorder);

        // 消息列表layout
        messageList = (EMChatMessageList) getView().findViewById(R.id.message_list);
        messageList.init(toChatUsername, chatType);
        listView = messageList.getListView();

        inputMenu = (EMChatInputMenu) getView().findViewById(R.id.input_menu);
        if (chatType == EMChatMessageList.CHATTYPE_SINGLE) {
            // 注册扩展菜单栏按钮
            for (int i = 0; i < itemStrings.length; i++) {
                // init()需在这个方法后面调用
                inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i],
                        new MyItemClickListener());
            }
        } else {
            int len = itemStrings.length - 2;
            // 群聊不显示通话按钮
            for (int i = 0; i < len; i++) {
                // init()需在这个方法后面调用
                inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i],
                        new MyItemClickListener());
            }
        }
        // 设置按住说话控件
        inputMenu.setPressToSpeakRecorderView(voiceRecorder);
        inputMenu.init();
        inputMenu.setChatInputMenuListener(new ChatInputMenuListener() {

            @Override
            public void onSendMessage(String content) {
                // 发送文本消息
                sendTextMessage(content);
            }

            @Override
            public void onSendVoiceMessage(String filePath, String fileName, int length) {
                // 发送语音消息
                sendVoiceMessage(filePath, fileName, length);
            }
        });

        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);

        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    protected void setUpView() {

        // 获取当前conversation对象
        conversation = EMChatManager.getInstance().getConversation(toChatUsername);
        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();

        titleBar.setTitle(toChatUsername);
        if (chatType == EMChatMessageList.CHATTYPE_SINGLE) { // 单聊
            // 设置标题
            titleBar.setRightImageResource(R.drawable.mm_title_remove);
        } else {
            if (chatType == EMChatMessageList.CHATTYPE_GROUP) {
                // 群聊
                EMGroup group = EMGroupManager.getInstance().getGroup(toChatUsername);
                if (group != null)
                    titleBar.setTitle(group.getGroupName());
                titleBar.setRightImageResource(R.drawable.to_group_details_normal);
                // 监听当前会话的群聊解散被T事件
                groupListener = new GroupListener();
                EMGroupManager.getInstance().addGroupChangeListener(groupListener);
            } else {
                onChatRoomViewCreation();
            }

        }
        if (chatType != EMChatMessageList.CHATTYPE_CHATROOM) {
            // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
            // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
            final List<EMMessage> msgs = conversation.getAllMessages();
            int msgCount = msgs != null ? msgs.size() : 0;
            if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
                String msgId = null;
                if (msgs != null && msgs.size() > 0) {
                    msgId = msgs.get(0).getMsgId();
                }
                if (chatType == EMChatMessageList.CHATTYPE_SINGLE) {
                    conversation.loadMoreMsgFromDB(msgId, pagesize);
                } else {
                    conversation.loadMoreGroupMsgFromDB(msgId, pagesize);
                }
            }
        }

        setListItemClickListener();
        
        messageList.getListView().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                inputMenu.hideExtendMenuContainer();
                return false;
            }
        });

        // 设置标题栏点击事件
        titleBar.setLeftLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        titleBar.setRightLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chatType == EMChatMessageList.CHATTYPE_SINGLE) {
                    emptyHistory();
                } else {
                    toGroupDetails();
                }
            }
        });

        setRefreshLayoutListener();
    }

    protected void setListItemClickListener() {
        messageList.setItemClickListener(new MessageListItemClickListener() {
            
            @Override
            public void onUserAvatarClick(String username) {
                
            }
            
            @Override
            public void onResendClick(final EMMessage message) {
                new EMAlertDialog(getActivity(), R.string.resend, R.string.confirm_resend, null, new AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (!confirmed) {
                            return;
                        }
                        messageList.resendMessage(message);
                    }
                }, true).show();
            }
            
            @Override
            public void onBubbleLongClick(EMMessage message) {
                contextMenuMessage = message;
                startActivityForResult((new Intent(getActivity(), ContextMenu.class)).putExtra("message",message),
                        REQUEST_CODE_CONTEXT_MENU);
            }
            
            @Override
            public boolean onBubbleClick(EMMessage message) {
                return false;
            }
        });
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                            List<EMMessage> messages;
                            try {
                                if (chatType == EMChatMessageList.CHATTYPE_SINGLE) {
                                    messages = conversation.loadMoreMsgFromDB(messageList.getItem(0).getMsgId(),
                                            pagesize);
                                } else {
                                    messages = conversation.loadMoreGroupMsgFromDB(messageList.getItem(0).getMsgId(),
                                            pagesize);
                                }
                            } catch (Exception e1) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            if (messages.size() > 0) {
                                messageList.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != pagesize) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }

                            isloading = false;

                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.no_more_messages),
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_EXIT_GROUP) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
            return;
        }
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
            case RESULT_CODE_COPY: // 复制消息
                clipboard.setText(((TextMessageBody) contextMenuMessage.getBody()).getMessage());
                break;
            case RESULT_CODE_DELETE: // 删除消息|
                conversation.removeMessage(contextMenuMessage.getMsgId());
                messageList.refresh();
                break;

            case RESULT_CODE_FORWARD: // 转发消息
                Intent intent = new Intent(getActivity(), ForwardMessageActivity.class);
                intent.putExtra("forward_msg_id", contextMenuMessage.getMsgId());
                startActivity(intent);
                
                break;

            default:
                break;
            }
        }
        
        if (resultCode == Activity.RESULT_OK) { // 清空消息
            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
                // 清空会话
                EMChatManager.getInstance().clearConversation(toChatUsername);
                messageList.refresh();
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists())
                    sendImageMessage(cameraFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频

                int duration = data.getIntExtra("dur", 0);
                String videoPath = data.getStringExtra("path");
                sendVideoMessageByResult(duration, videoPath);

            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        sendFileMessage(uri);
                    }
                }

            } else if (requestCode == REQUEST_CODE_MAP) { // 地图
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    sendLocationMessage(latitude, longitude, locationAddress);
                } else {
                    Toast.makeText(getActivity(), R.string.unable_to_get_loaction, 0).show();
                }
                
            } else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
                
            } else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
                EMMessage deleteMsg = (EMMessage) messageList.getItem(data.getIntExtra("position", -1));
//                addUserToBlacklist(deleteMsg.getFrom());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        messageList.refresh();
        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        sdkHelper.pushActivity(getActivity());
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(
                this,
                new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventDeliveryAck,
                        EMNotifierEvent.Event.EventReadAck });
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
        EMChatManager.getInstance().unregisterEventListener(this);

        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();

        // 把此activity 从foreground activity 列表里移除
        sdkHelper.popActivity(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (groupListener != null) {
            EMGroupManager.getInstance().removeGroupChangeListener(groupListener);
        }
    }

    /**
     * 事件监听,registerEventListener后的回调事件
     * 
     * see {@link EMNotifierEvent}
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
        case EventNewMessage:
            // 获取到message
            EMMessage message = (EMMessage) event.getData();

            String username = null;
            // 群组消息
            if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                // 单聊消息
                username = message.getFrom();
            }

            // 如果是当前会话的消息，刷新聊天页面
            if (username.equals(toChatUsername)) {
                messageList.refreshSelectLast();
                // 声音和震动提示有新消息
                HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
            } else {
                // 如果消息不是和当前聊天ID的消息
                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
            }

            break;
        case EventDeliveryAck:
        case EventReadAck:
            // 获取到message
            messageList.refresh();
            break;
        case EventOfflineMessage:
            // a list of offline messages
            // List<EMMessage> offlineMessages = (List<EMMessage>)
            // event.getData();
            messageList.refresh();
            break;
        default:
            break;
        }

    }

    public void onBackPressed() {
        if (inputMenu.onBackPressed()) {
            getActivity().finish();
            if (chatType == EMChatMessageList.CHATTYPE_CHATROOM) {
                EMChatManager.getInstance().leaveChatRoom(toChatUsername);
            }
        }
    }

    protected void onChatRoomViewCreation() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Joining......");
        EMChatManager.getInstance().joinChatRoom(toChatUsername, new EMValueCallBack<EMChatRoom>() {

            @Override
            public void onSuccess(EMChatRoom value) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        EMChatRoom room = EMChatManager.getInstance().getChatRoom(toChatUsername);
                        if (room != null) {
                            titleBar.setTitle(room.getName());
                        } else {
                            titleBar.setTitle(toChatUsername);
                        }
                        EMLog.d(TAG, "join room success : " + room.getName());

                        addChatRoomChangeListenr();
                        // onConversationInit();
                        //
                        // onListViewCreation();
                    }
                });
            }

            @Override
            public void onError(final int error, String errorMsg) {
                // TODO Auto-generated method stub
                EMLog.d(TAG, "join room failure : " + error);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                });
                getActivity().finish();
            }
        });
    }

    protected void addChatRoomChangeListenr() {
        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(toChatUsername)) {
                    getActivity().finish();
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
            }

            @Override
            public void onMemberExited(String roomId, String roomName, String participant) {

            }

            @Override
            public void onMemberKicked(String roomId, String roomName, String participant) {
                if (roomId.equals(toChatUsername)) {
                    String curUser = EMChatManager.getInstance().getCurrentUser();
                    if (curUser.equals(participant)) {
                        EMChatManager.getInstance().leaveChatRoom(toChatUsername);
                        getActivity().finish();
                    }
                }
            }

        });
    }

    /**
     * 扩展菜单栏item点击事件
     *
     */
    class MyItemClickListener implements ChatExtendMenuItemClickListener {

        @Override
        public void onClick(int itemId, View view) {
            switch (itemId) {
            case ITEM_TAKE_PICTURE: // 拍照
                selectPicFromCamera();
                break;
            case ITEM_PICTURE:
                selectPicFromLocal(); // 图库选择图片
                break;
            case ITEM_LOCATION: // 位置
                startActivityForResult(new Intent(getActivity(), BaiduMapActivity.class), REQUEST_CODE_MAP);
                break;
            case ITEM_VIDEO: // 视频
                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
                break;
            case ITEM_FILE:
                selectFileFromLocal();
                break;
            case ITEM_VOICE_CALL: // 语音通话
                startVoiceCall();
                break;
            case ITEM_VIDEO_CALL: // 视频通话
                startVideoCall();
                break;

            default:
                break;
            }
        }

    }

    protected void sendTextMessage(String content) {
        messageList.sendTextMessage(content, null);
    }

    protected void sendVoiceMessage(String filePath, String fileName, int length) {
        messageList.sendVoiceMessage(filePath, fileName, length, null);
    }

    protected void sendImageMessage(String imagePath) {
        messageList.sendImageMessage(imagePath, false, null);
    }

    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        messageList.sendLocationMessage(latitude, longitude, locationAddress, null);
    }

    protected void sendVideoMessage(String filePath, String thumbPath, int length) {
        messageList.sendVideoMessage(filePath, thumbPath, length, null);
    }

    protected void sendFileMessage(Uri uri) {
        messageList.sendFileMessage(uri, null);
    }

    protected void startVoiceCall() {
        if (!EMChatManager.getInstance().isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connect_to_server, 0).show();
        } else {
            startActivity(new Intent(getActivity(), VoiceCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }

    protected void startVideoCall() {
        if (!EMChatManager.getInstance().isConnected())
            Toast.makeText(getActivity(), R.string.not_connect_to_server, 0).show();
        else {
            startActivity(new Intent(getActivity(), VideoCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }

    /**
     * 根据图库图片uri发送图片
     * 
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, null, null, null, null);
        String st8 = getResources().getString(R.string.cant_find_pictures);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(getActivity(), st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }

    /**
     * 照相获取图片
     */
    protected void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            Toast.makeText(getActivity(), R.string.sd_card_does_not_exist, 0).show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), DemoApplication.getInstance().getUserName()
                + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * 从图库获取图片
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    /**
     * 选择文件
     */
    protected void selectFileFromLocal() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    private void sendVideoMessageByResult(int duration, String videoPath) {
        File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
        Bitmap bitmap = null;
        FileOutputStream fos = null;
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
            if (bitmap == null) {
                EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
            }
            fos = new FileOutputStream(file);

            bitmap.compress(CompressFormat.JPEG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fos = null;
            }
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }

        }
        sendVideoMessage(videoPath, file.getAbsolutePath(), duration / 1000);
    }

    /**
     * 点击清空聊天记录
     * 
     */
    public void emptyHistory() {
        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        startActivityForResult(
                new Intent(getActivity(), AlertDialog.class).putExtra("titleIsCancel", true).putExtra("msg", msg)
                        .putExtra("cancel", true), REQUEST_CODE_EMPTY_HISTORY);
    }

    /**
     * 点击进入群组详情
     * 
     */
    public void toGroupDetails() {
        if (chatType == EMChatMessageList.CHATTYPE_GROUP) {
            EMGroup group = EMGroupManager.getInstance().getGroup(toChatUsername);
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, 0).show();
                return;
            }
            startActivityForResult(
                    (new Intent(getActivity(), GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
                    REQUEST_CODE_GROUP_DETAIL);
        }
    }

    /**
     * 隐藏软键盘
     */
    protected void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 监测群组解散或者被T事件
     * 
     */
    class GroupListener extends GroupRemoveListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            getActivity().runOnUiThread(new Runnable() {

                public void run() {
                    if (toChatUsername.equals(groupId)) {
                        Toast.makeText(getActivity(), R.string.you_are_group, 1).show();
                        if (GroupDetailsActivity.instance != null)
                            GroupDetailsActivity.instance.finish();
                        getActivity().finish();
                    }
                }
            });
        }

        @Override
        public void onGroupDestroy(final String groupId, String groupName) {
            // 群组解散正好在此页面，提示群组被解散，并finish此页面
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (toChatUsername.equals(groupId)) {
                        Toast.makeText(getActivity(), R.string.the_current_group, 1).show();
                        if (GroupDetailsActivity.instance != null)
                            GroupDetailsActivity.instance.finish();
                        getActivity().finish();
                    }
                }
            });
        }

    }
}
