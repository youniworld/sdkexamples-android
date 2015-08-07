package com.easemob.chatuilib.domain;

public class SystemUser extends User{
    protected int unreadMsgCount;
    protected int defautAvatarRes;
    
    public SystemUser(String username) {
        super(username);
    }
    
    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public int getDefautAvatarRes() {
        return defautAvatarRes;
    }

    public void setDefautAvatarRes(int defautAvatarRes) {
        this.defautAvatarRes = defautAvatarRes;
    }
    
    
}
