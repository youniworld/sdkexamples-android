package com.easemob.chatuilib.widget;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.easemob.chatuilib.R;
import com.easemob.chatuilib.domain.User;
import com.easemob.chatuilib.widget.adapter.ContactAdapter;
import com.easemob.util.EMLog;

public class EMContactList extends RelativeLayout {
    protected static final String TAG = EMContactList.class.getSimpleName();
    
    protected Context context;
    protected ListView listView;
    protected ContactAdapter adapter;
    protected List<User> contactList;
    protected EMSidebar sidebar;
    
    protected int primaryColor;
    protected int primarySize;
    protected boolean showSiderBar;
    protected Drawable initialLetterBg;
    
    static final int MSG_UPDATE_LIST = 0;
    
    Handler handler = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_UPDATE_LIST:
                if(adapter != null)
                    adapter.notifyDataSetChanged();
                break;
            default:
                break;
            }
            super.handleMessage(msg);
        }
    };

    protected int initialLetterColor;

    
    public EMContactList(Context context) {
        super(context);
        init(context, null);
    }

    public EMContactList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public EMContactList(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EMContactList);
        primaryColor = ta.getColor(R.styleable.EMContactList_ctsListPrimaryTextColor, 0);
        primarySize = ta.getDimensionPixelSize(R.styleable.EMContactList_ctsListPrimaryTextSize, 0);
        showSiderBar = ta.getBoolean(R.styleable.EMContactList_ctsListShowSiderBar, false);
        initialLetterBg = ta.getDrawable(R.styleable.EMContactList_ctsListInitialLetterBg);
        initialLetterColor = ta.getColor(R.styleable.EMContactList_ctsListInitialLetterColor, 0);
        ta.recycle();
        
        
        LayoutInflater.from(context).inflate(R.layout.em_widget_contact_list, this);
        listView = (ListView)findViewById(R.id.list);
        
        
    }
    
    /*
     * init view
     */
    public void init(){
        if(contactList == null){
            EMLog.e(TAG, "plese set contact list before invoke init method");
            return;
        }
        adapter = new ContactAdapter(context, 0, contactList);
        adapter.setPrimaryColor(primaryColor).setPrimarySize(primarySize).setInitialLetterBg(initialLetterBg)
            .setInitialLetterColor(initialLetterColor);
        listView.setAdapter(adapter);
        if(showSiderBar){
            sidebar = (EMSidebar) findViewById(R.id.sidebar);
            sidebar.setListView(listView);
        }
    }
    
    /**
     * 设置联系人列表,init方法需在此方法后面调用
     * @param contactList
     */
    public void setContactList(List<User> contactList){
        this.contactList = contactList;
    }
    
    public void refresh(){
        Message msg = handler.obtainMessage(MSG_UPDATE_LIST);
        handler.sendMessage(msg);
    }
    
    public void filter(CharSequence str) {
        adapter.getFilter().filter(str);
    }
    
    public ListView getListView(){
        return listView;
    }


}
