package com.easemob.applib.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;

import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.domain.User;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;

public class EMContactList extends RelativeLayout {
    
    public interface EMContactListWidgetUser {
        public void onItemClick(int position, Object obj);
    }
    
    Context context;
    ListView listView;
    ContactAdapter adapter;
    List<User> contactList = new ArrayList<User>();
    private Sidebar sidebar;
    private EMContactListWidgetUser user;
    
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
        init(context);
    }

    public EMContactList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public EMContactList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    
    private void init(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.em_widget_contact_list, this);
        listView = (ListView)view.findViewById(R.id.list);
        adapter = new ContactAdapter(context, 0, contactList);
        listView.setAdapter(adapter);
        sidebar = (Sidebar) view.findViewById(R.id.sidebar);
        sidebar.setListView(listView);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (user != null) {
                    user.onItemClick(position, getItem(position));
                }
            }
        });
        
        refresh();
    }
    
    private void setUserHeader(String username, User user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
                    .toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
    }
    
    /**
     * 用户可以派生自己的EMContactListWidget
     * 并重写getContactList，来提供自定义的联系人列表
     * @return
     */
    protected List<User> getContactList() {
        List<String> contacts = null;
        List<User> results = new ArrayList<User>();
        try {
            contacts = EMChatManager.getInstance().getContactUserNames();
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
        if (contacts == null) {
            return results;
        }
        for (String username : contacts) {
            User user = new User(username);
            results.add(user);
            setUserHeader("", user);
        }
        return results;
    }
    
    public void refresh() {
        contactList.clear();
        contactList.addAll(getContactList());
        Collections.sort(contactList, new Comparator<User>() {
            @Override
            public int compare(final User user1, final User user2) {
                return user1.getUsername().compareTo(user2.getUsername());
            }
        });
        
        EMContactWidgetFactory.getInstance(context).onPreUpdateData(contactList);

        adapter.updateSections();
        
        Message msg = handler.obtainMessage(MSG_UPDATE_LIST);
        handler.sendMessage(msg);
    }
    
    public void filter(CharSequence str) {
        adapter.getFilter().filter(str);
    }
    
    
    public ListView getListView() {
        return listView;
    }

    public Object getItem(int position) {
        return adapter.getItem(position);
    }
    
    public void setUser(EMContactListWidgetUser user) {
        this.user = user;
    }

}
