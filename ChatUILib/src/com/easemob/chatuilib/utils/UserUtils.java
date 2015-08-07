package com.easemob.chatuilib.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chatuilib.R;
import com.easemob.chatuilib.controller.HXSDKHelper;
import com.easemob.chatuilib.controller.HXSDKHelper.UserProvider;
import com.easemob.chatuilib.domain.User;
import com.squareup.picasso.Picasso;

public class UserUtils {
    
    static UserProvider userProvider;
    
    static {
        userProvider = HXSDKHelper.getInstance().getUserInfoProvider();
    }
    
    /**
     * 根据username获取相应user
     * @param username
     * @return
     */
    public static User getUserInfo(String username){
        if(userProvider != null)
            return userProvider.getUser(username);
        
        return null;
    }
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	User user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Picasso.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
            }
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.em_default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(R.drawable.em_default_avatar).into(imageView);
        }
    }
    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
    	User user = getUserInfo(username);
    	if(user != null){
    		textView.setText(user.getNick());
    	}else{
    		textView.setText(username);
    	}
    }
    
}
