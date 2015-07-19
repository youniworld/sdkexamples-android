package com.easemob.applib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.MessageAdapter;

public class EMChatMessageList extends RelativeLayout{
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
	
	
    protected ListView messageListView;
	private Context context;

	public EMChatMessageList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseStyle(context, attrs, defStyle);
        this.context = context;
//        init(context, attrs);
    }

    public EMChatMessageList(Context context, AttributeSet attrs) {
    	this(context, attrs,0);
    }

    public EMChatMessageList(Context context) {
        super(context);
        this.context = context;
//        init(context, null);
    }

    public void init(String toChatUsername, int chatType) {
        LayoutInflater.from(context).inflate(R.layout.em_chat_message_list, this);
        messageListView = (ListView) findViewById(R.id.list);
        MessageAdapter adapter = new MessageAdapter(context, toChatUsername, chatType);
        // 显示消息
        messageListView.setAdapter(adapter);
        
        adapter.refreshSelectLast();
        
    }
    
    protected void parseStyle(Context context, AttributeSet attrs, int defStyle) {
    	TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EMChatMessageList, 0, defStyle);
		ta.recycle();
	}

	public ListView getListView() {
		return messageListView;
	} 
}
