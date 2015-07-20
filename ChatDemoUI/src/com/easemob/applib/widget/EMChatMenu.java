package com.easemob.applib.widget;

import com.easemob.chatuidemo.R;
import com.easemob.util.DensityUtil;

import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EMChatMenu extends RelativeLayout{
    private int itemSize;
    private Context context;
    private LinearLayout menuContainer;
    private LinearLayout rowContainer1;
    private LinearLayout rowContainer2;
    
	public EMChatMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public EMChatMenu(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public EMChatMenu(Context context) {
		super(context);
		init(context, null);
	}
	
	void init(Context context, AttributeSet attrs){
	    this.context = context;
		LayoutInflater.from(context).inflate(R.layout.em_chat_menu, this);
		menuContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
	}
	
	/**
	 * 注册menu item
	 * @param name item名字
	 * @param drawableRes item背景
	 */
	public void registerMenuItem(String name, int drawableRes){
	    itemSize++;
	    if(itemSize == 1){
	        rowContainer1 = new LinearLayout(context);
	        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	        params.setMargins(0, DensityUtil.dip2px(context, 6), 0, DensityUtil.dip2px(context, 6));
	        rowContainer1.setLayoutParams(params);
	        rowContainer1.setWeightSum(4);
	        menuContainer.addView(rowContainer1);
	    }else if(itemSize == 5){
	        rowContainer2 = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, DensityUtil.dip2px(context, 6), 0, DensityUtil.dip2px(context, 6));
            rowContainer2.setLayoutParams(params);
            rowContainer2.setWeightSum(4);
            menuContainer.addView(rowContainer2);
	    }
	    ChatMenuItem menuItem = new ChatMenuItem(context);
	    menuItem.setImage(drawableRes);
	    menuItem.setText(name);
	    if(itemSize < 5)
	        rowContainer1.addView(menuItem);
	    else
	        rowContainer2.addView(menuItem);
	}
	
	/**
     * 注册menu item
     * @param nameRes item名字的resource id
     * @param drawableRes item背景
     */
    public void registerMenuItem(int nameRes, int drawableRes){
        registerMenuItem(context.getString(nameRes), drawableRes);
    }
	
	
	class ChatMenuItem extends LinearLayout{
	    private ImageView imageView;
	    private TextView textView;
	    
        public ChatMenuItem(Context context, AttributeSet attrs, int defStyle) {
            this(context, attrs);
        }

        public ChatMenuItem(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context, attrs);
        }

        public ChatMenuItem(Context context) {
            super(context);
            init(context, null);
        }
        
        private void init(Context context, AttributeSet attrs){
            LayoutInflater.from(context).inflate(R.layout.em_chat_menu_item, this);
            imageView = (ImageView) findViewById(R.id.image);
            textView = (TextView) findViewById(R.id.text);
        }
	    
        public void setImage(int resid){
            imageView.setBackgroundResource(resid);
        }
        
        public void setText(int resid){
            textView.setText(resid);
        }
        
        public void setText(String text){
            textView.setText(text);
        }
	}
}
