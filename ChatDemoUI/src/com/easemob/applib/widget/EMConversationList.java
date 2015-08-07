package com.easemob.applib.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.applib.controller.HXSDKHelper.UserProvider;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.domain.RobotUser;
import com.easemob.chatuidemo.utils.CommonUtils;
import com.easemob.chatuidemo.utils.DateUtils;
import com.easemob.chatuidemo.utils.SmileUtils;
import com.easemob.chatuidemo.utils.UserUtils;

public class EMConversationList extends ListView {
    
    private int primaryColor;
    private int secondaryColor;
    private int timeColor;
    private int primarySize;
    private int secondarySize;
    private float timeSize;
    
    public interface EMConversationListWidgetUser {
        public void onClick(EMConversation conversation);
    }

    private final int MSG_REFRESH_ADAPTER_DATA = 0;
    
    private Context context;
    private ConverastionListAdapater adapter;
    private List<EMConversation> allConversations = new ArrayList<EMConversation>();
    
    public EMConversationList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public EMConversationList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EMConversationList);
        primaryColor = ta.getColor(R.styleable.EMConversationList_cvsListPrimaryTextColor, R.color.list_item_primary_color);
        secondaryColor = ta.getColor(R.styleable.EMConversationList_cvsListSecondaryTextColor, R.color.list_item_secondary_color);
        timeColor = ta.getColor(R.styleable.EMConversationList_cvsListTimeTextColor, R.color.list_item_secondary_color);
        primarySize = ta.getDimensionPixelSize(R.styleable.EMConversationList_cvsListPrimaryTextSize, 0);
        secondarySize = ta.getDimensionPixelSize(R.styleable.EMConversationList_cvsListSecondaryTextSize, 0);
        timeSize = ta.getDimension(R.styleable.EMConversationList_cvsListTimeTextSize, 0);
        
        ta.recycle();
        
    }
    
    public void init(){
        adapter = new ConverastionListAdapater(context, 0, allConversations);
        setAdapter(adapter);

        refresh();
    }
    
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
            case MSG_REFRESH_ADAPTER_DATA:
                if (adapter != null) {
                    adapter.conversationList.clear();
                    adapter.conversationList.addAll(allConversations);
                    adapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
            }
        }
    };
    

    /**
     * 获取所有会话
     * 
     * @param context
     * @return
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        +    */
    private List<EMConversation> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变 
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     * 
     * @param usernames
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }
    
    public EMConversation getItem(int position) {
        return (EMConversation)adapter.getItem(position);
    }
    
    public void refresh() {
        allConversations = loadConversationsWithRecentChat();

        handler.sendEmptyMessage(MSG_REFRESH_ADAPTER_DATA);
    }
    
    public void filter(CharSequence str) {
        adapter.getFilter().filter(str);
    }
    
    /**
     * adapter
     *
     */
    class ConverastionListAdapater extends ArrayAdapter<EMConversation> {
        List<EMConversation> conversationList;
        private ConversationFilter conversationFilter;

        public ConverastionListAdapater(Context context, int resource,
                List<EMConversation> objects) {
            super(context, resource, objects);
            conversationList = objects;
        }

        @Override
        public int getCount() {
            return conversationList.size();
        }

        @Override
        public EMConversation getItem(int arg0) {
            if (arg0 < conversationList.size()) {
                return conversationList.get(arg0);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.em_row_chat_history, parent, false);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
                holder.message = (TextView) convertView.findViewById(R.id.message);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
                holder.msgState = convertView.findViewById(R.id.msg_state);
                holder.list_item_layout = (RelativeLayout) convertView.findViewById(R.id.list_item_layout);
                convertView.setTag(holder);
            }
            holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem);

            // 获取与此用户/群组的会话
            EMConversation conversation = getItem(position);
            // 获取用户username或者群组groupid
            String username = conversation.getUserName();
            if (conversation.getType() == EMConversationType.GroupChat) {
                // 群聊消息，显示群聊头像
                holder.avatar.setImageResource(R.drawable.group_icon);
                EMGroup group = EMGroupManager.getInstance().getGroup(username);
                holder.name.setText(group != null ? group.getGroupName() : username);
            } else if(conversation.getType() == EMConversationType.ChatRoom){
                holder.avatar.setImageResource(R.drawable.group_icon);
                EMChatRoom room = EMChatManager.getInstance().getChatRoom(username);
                holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
            }else {
                UserUtils.setUserAvatar(getContext(), username, holder.avatar);
                Map<String,RobotUser> robotMap=((DemoHXSDKHelper)HXSDKHelper.getInstance()).getRobotList();
                if(robotMap!=null&&robotMap.containsKey(username)){
                    String nick = robotMap.get(username).getNick();
                    if(!TextUtils.isEmpty(nick)){
                        holder.name.setText(nick);
                    }else{
                        holder.name.setText(username);
                    }
                }else{
                    holder.name.setText(username);
                }
            }

            if (conversation.getUnreadMsgCount() > 0) {
                // 显示与此用户的消息未读数
                holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
                holder.unreadLabel.setVisibility(View.VISIBLE);
            } else {
                holder.unreadLabel.setVisibility(View.INVISIBLE);
            }

            if (conversation.getMsgCount() != 0) {
                // 把最后一条消息的内容作为item的message内容
                EMMessage lastMessage = conversation.getLastMessage();
                holder.message.setText(SmileUtils.getSmiledText(getContext(), CommonUtils.getMessageDigest(lastMessage, (this.getContext()))),
                        BufferType.SPANNABLE);

                holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
                if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
                    holder.msgState.setVisibility(View.VISIBLE);
                } else {
                    holder.msgState.setVisibility(View.GONE);
                }
            }
            
            //设置自定义属性
            holder.name.setTextColor(primaryColor);
            holder.message.setTextColor(secondaryColor);
            holder.time.setTextColor(timeColor);
            if(primarySize != 0)
                holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
            if(secondarySize != 0)
                holder.message.setTextSize(TypedValue.COMPLEX_UNIT_PX, secondarySize);
            if(timeSize != 0)
                holder.time.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeSize);

            return convertView;
        }
        
        @Override
        public Filter getFilter() {
            if (conversationFilter == null) {
                conversationFilter = new ConversationFilter();
            }
            return conversationFilter;
        }
        
        class ConversationFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence filter) {
                FilterResults results = new FilterResults();

                if (filter == null || filter.length() == 0) {
                    results.values = allConversations;
                    results.count = allConversations.size();
                } else {
                    String prefixString = filter.toString();
                    final int count = allConversations.size();
                    final List<EMConversation> newValues = new ArrayList<EMConversation>();
                    Map<String, EMConversation> container = new HashMap<String, EMConversation>();
                    
                    // prepare container data, if data match filter
                    // data will be added to newValues, and removed from container 
                    for (int i = 0; i < count; i++) {
                        final EMConversation conversation = allConversations.get(i);
                        String username = conversation.getUserName();

                        EMGroup group = EMGroupManager.getInstance().getGroup(
                                username);
                        if (group != null) {
                            username = group.getGroupName();
                        }
                        container.put(username, conversation);
                    }
                    
                    // startWith
                    List<String> toRemove = new ArrayList<String>();
                    for (String username : container.keySet()) {
                        if (username.startsWith(prefixString)) {
                            newValues.add(container.get(username));
                            toRemove.add(username);
                        }
                    }
                    for (String username : toRemove) {
                        container.remove(username);
                    }
                    
                    // contains
                    for (String username : container.keySet()) {
                        if (username.contains(prefixString)) {
                            newValues.add(container.get(username));
                            toRemove.add(username);
                        }
                    }
                    for (String username : toRemove) {
                        container.remove(username);
                    }

                    results.values = newValues;
                    results.count = newValues.size();
                }
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                conversationList.clear();
                conversationList.addAll((List<EMConversation>) results.values);
                if (results.count > 0) {
//                  notiyfyByFilter = true;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }
    }
    
    
    
    private static class ViewHolder {
        /** 和谁的聊天记录 */
        TextView name;
        /** 消息未读数 */
        TextView unreadLabel;
        /** 最后一条消息的内容 */
        TextView message;
        /** 最后一条消息的时间 */
        TextView time;
        /** 用户头像 */
        ImageView avatar;
        /** 最后一条消息的发送状态 */
        View msgState;
        /** 整个list中每一行总布局 */
        RelativeLayout list_item_layout;

    }

    
}
