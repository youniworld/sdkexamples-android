package com.easemob.applib.widget;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.domain.User;
import com.easemob.util.EMLog;

public class EMContactList extends RelativeLayout {
    protected static final String TAG = EMContactList.class.getSimpleName();
    
    Context context;
    ListView listView;
    ContactListAdapter adapter;
    List<User> contactList;
    private Sidebar sidebar;
    
    public static final int MSG_UPDATE_LIST = 0;
    
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
        adapter = new ContactListAdapter(context, 0, contactList);
        listView.setAdapter(adapter);
        sidebar = (Sidebar) findViewById(R.id.sidebar);
        sidebar.setListView(listView);
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
