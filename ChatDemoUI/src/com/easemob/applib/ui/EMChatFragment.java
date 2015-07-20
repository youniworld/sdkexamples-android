package com.easemob.applib.ui;

import com.easemob.applib.widget.EMChatMenu;
import com.easemob.applib.widget.EMChatMessageList;
import com.easemob.chatuidemo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EMChatFragment extends Fragment{
    private static final String TAG = "ChatActivity";
    private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    private static final int REQUEST_CODE_MAP = 4;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_LOCATION = 8;
    public static final int REQUEST_CODE_NET_DISK = 9;
    public static final int REQUEST_CODE_FILE = 10;
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

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;
    protected int chatType;
    protected String toChatUsername;
    protected EMChatMenu chatMenu;
    protected EMChatMessageList messageListLayout;
    
    int[] menuStrings = {R.string.attach_take_pic, R.string.attach_picture, R.string.attach_location, R.string.attach_location,
                R.string.attach_video, R.string.attach_file, R.string.attach_voice_call, R.string.attach_video_call};
    int[] menudrawables = {R.drawable.chat_takepic_selector, R.drawable.chat_image_selector, R.drawable.chat_location_selector,
                R.drawable.chat_video_selector, R.drawable.chat_file_selector, R.drawable.chat_voice_call_selector, R.drawable.chat_video_call_selector}; 
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.em_fragment_chat, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
	    Bundle args = getArguments();
	    // 判断单聊还是群聊
        chatType = args.getInt("chatType", CHATTYPE_SINGLE);
        // 会话人或群组id
        toChatUsername = args.getString("userId");
        
	    messageListLayout = (EMChatMessageList) getView().findViewById(R.id.message_list);
	    messageListLayout.init(toChatUsername, chatType);
	    
	    chatMenu = (EMChatMenu) getView().findViewById(R.id.chat_menu);
	}
	
	protected void registerMenuItems(){
	    chatMenu.registerMenuItem(menuStrings[0], menudrawables[0]);
	    
	}
}
