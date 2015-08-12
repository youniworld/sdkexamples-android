package com.easemob.chatuilib.domain;

public class SystemUser extends User{
    protected int unreadMsgCount;
    
    public SystemUser(String username) {
        super(username);
    }
    
    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

}
