package com.easemob.applib.ui;

import android.os.Bundle;

import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.BaseActivity;

/**
 * 聊天页面，需要fragment的使用{@link #EMChatFragment}
 *
 */
public class EMChatActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_chat);
        
        EMChatFragment chatFragment = new EMChatFragment();
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
        
    }
}
