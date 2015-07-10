package com.easemob.applib.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class EMMessageList extends LinearLayout{
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public EMMessageList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public EMMessageList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EMMessageList(Context context) {
        super(context);
        init(context, null);
    }

    protected void init(Context context, AttributeSet attrs) {
        
    }
}
