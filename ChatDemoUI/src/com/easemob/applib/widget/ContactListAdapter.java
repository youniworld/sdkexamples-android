package com.easemob.applib.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.SectionIndexer;

import com.easemob.chatuidemo.domain.User;
import com.easemob.util.EMLog;

public class ContactListAdapter extends ArrayAdapter<User> implements SectionIndexer{
    private static final String TAG = "ContactAdapter";
    List<String> list;
    List<User> userList;
    List<User> copyUserList;
    private SparseIntArray positionForSection;
    private SparseIntArray sectionForPosition;
    private MyFilter myFilter;
    private boolean notiyfyByFilter;

    public ContactListAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.userList = objects;
        copyUserList = new ArrayList<User>();
        copyUserList.addAll(objects);
    }
    
    public void updateSections() {
        getSections();
    }

    public int getPositionForSection(int section) {
        return positionForSection.get(section);
    }

    public int getSectionForPosition(int position) {
        return sectionForPosition.get(position);
    }
    
    @Override
    public Object[] getSections() {
        positionForSection = new SparseIntArray();
        sectionForPosition = new SparseIntArray();
        int count = getCount();
        list = new ArrayList<String>();
        int section = 0;
        String prev = "";
        for (int i = 0; i < count; i++) {
            String header = getItem(i).getHeader();
            sectionForPosition.put(i, section);
            if (getItem(i).getHeader() != null && !prev.equals(header)) {
                list.add(header);
                positionForSection.put(section, i);
                section++;
                prev = header;
            }
        }
        return list.toArray(new String[list.size()]);
    }
    
    @Override
    public Filter getFilter() {
        if(myFilter==null){
            myFilter = new MyFilter(userList);
        }
        return myFilter;
    }
    
    private class  MyFilter extends Filter{
        List<User> mOriginalList = null;
        
        public MyFilter(List<User> myList) {
            this.mOriginalList = myList;
        }

        @Override
        protected synchronized FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if(mOriginalList==null){
                mOriginalList = new ArrayList<User>();
            }
            EMLog.d(TAG, "contacts original size: " + mOriginalList.size());
            EMLog.d(TAG, "contacts copy size: " + copyUserList.size());
            
            if(prefix==null || prefix.length()==0){
                results.values = copyUserList;
                results.count = copyUserList.size();
            }else{
                String prefixString = prefix.toString();
                final int count = mOriginalList.size();
                final ArrayList<User> newValues = new ArrayList<User>();
                for(int i=0;i<count;i++){
                    final User user = mOriginalList.get(i);
                    String username = user.getUsername();
                    
                    if(username.startsWith(prefixString)){
                        newValues.add(user);
                    }
                    else{
                         final String[] words = username.split(" ");
                         final int wordCount = words.length;
    
                         // Start at index 0, in case valueText starts with space(s)
                         for (int k = 0; k < wordCount; k++) {
                             if (words[k].startsWith(prefixString)) {
                                 newValues.add(user);
                                 break;
                             }
                         }
                    }
                }
                results.values=newValues;
                results.count=newValues.size();
            }
            EMLog.d(TAG, "contacts filter results size: " + results.count);
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected synchronized void publishResults(CharSequence constraint,
                FilterResults results) {
            userList.clear();
            userList.addAll((List<User>)results.values);
            EMLog.d(TAG, "publish contacts filter results size: " + results.count);
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
                notiyfyByFilter = false;
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
    
    
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(!notiyfyByFilter){
            copyUserList.clear();
            copyUserList.addAll(userList);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = EMContactWidgetFactory.getInstance(context).generateView(position, convertView, parent);
        if (view != null) {
            return view;
        }
        boolean isSectionIndex = false;
        if (positionForSection != null) {
            isSectionIndex = positionForSection.indexOfValue(position) >= 0;
        }
        User user = null;
        if (position < userList.size()) {
            user = userList.get(position);
        } else {
            return null;
        }
        return EMContactWidgetFactory.getInstance(context).generateView(user, convertView, parent, isSectionIndex);
    }
}
