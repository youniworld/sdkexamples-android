package com.easemob.applib.widget;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.widget.PasteEditText;
import com.easemob.util.DensityUtil;

public class EMChatMenu extends RelativeLayout implements OnClickListener {
    private int itemSize;
    private Context context;
    private LinearLayout menuContainer;
    private LinearLayout rowContainer1;
    private LinearLayout rowContainer2;
    private View recordingContainer;
    private ImageView micImage;
    private TextView recordingHint;
    private PasteEditText mEditTextContent;
    private View buttonSetModeKeyboard;
    private RelativeLayout edittext_layout;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    private ImageView iv_emoticons_normal;
    private ImageView iv_emoticons_checked;
    private Button buttonMore;
    private View moreLayout;
    private ChatMenuListener listener;
    private EMEmojicon emojicon;
    private Activity activity;
    private InputMethodManager inputManager;

    public EMChatMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public EMChatMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EMChatMenu(Context context) {
        super(context);
        init(context, null);
    }

    void init(Context context, AttributeSet attrs) {
        this.context = context;
        this.activity = (Activity) context;
        LayoutInflater.from(context).inflate(R.layout.em_chat_menu, this);
        recordingContainer = findViewById(R.id.recording_container);
        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
        iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
        buttonMore = (Button) findViewById(R.id.btn_more);
        moreLayout = findViewById(R.id.fl_more);
        edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
        menuContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
        emojicon = (EMEmojicon) findViewById(R.id.emojicon);
        
        inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        
        buttonSend.setOnClickListener(this);
        buttonSetModeKeyboard.setOnClickListener(this);
        buttonSetModeVoice.setOnClickListener(this);
        buttonMore.setOnClickListener(this);
        
        
    }

    /**
     * 注册聊天页面底下的menu item,不register默认只有发送语音、文字、表情功能
     * 
     * @param name
     *            item名字
     * @param drawableRes
     *            item背景
     * @param clickListener
     *            item点击事件
     */
    public void registerMenuItem(String name, int drawableRes, OnClickListener clickListener) {
        itemSize++;
        if (itemSize == 1) {
            rowContainer1 = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, DensityUtil.dip2px(context, 6), 0, DensityUtil.dip2px(context, 6));
            rowContainer1.setLayoutParams(params);
            rowContainer1.setWeightSum(4);
            menuContainer.addView(rowContainer1);
        } else if (itemSize == 5) {
            rowContainer2 = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, DensityUtil.dip2px(context, 6), 0, DensityUtil.dip2px(context, 6));
            rowContainer2.setLayoutParams(params);
            rowContainer2.setWeightSum(4);
            menuContainer.addView(rowContainer2);
        }
        ChatMenuItem menuItem = new ChatMenuItem(context);
        menuItem.setImage(drawableRes);
        menuItem.setText(name);
        menuItem.setOnClickListener(clickListener);
        if (itemSize < 5)
            rowContainer1.addView(menuItem);
        else
            rowContainer2.addView(menuItem);
    }
    
    /**
     * 注册聊天页面底下的menu item，不register默认只有发送语音、文字、表情功能
     * 
     * @param nameRes
     *            item名字的resource id
     * @param drawableRes
     *            item背景
     * @param clickListener
     *            item点击事件
     */
    public void registerMenuItem(int nameRes, int drawableRes, OnClickListener clickListener) {
        registerMenuItem(context.getString(nameRes), drawableRes, clickListener);
    }
    
    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view){
        switch (view.getId()) {
        case R.id.btn_send: //发送
            if(listener != null){
                String s = mEditTextContent.getText().toString();
                listener.onSendBtnClicked(s);
            }
            break;
        case R.id.btn_set_mode_voice:
            setModeVoice();
            break;
        case R.id.btn_set_mode_keyboard:
            setModeKeyboard();
            break;
        case R.id.btn_more:
            toggleMore();
            break;
        default:
            break;
        }
    }
    
    /**
     * 显示语音图标按钮
     * 
     */
    protected void setModeVoice() {
        hideKeyboard();
        edittext_layout.setVisibility(View.GONE);
        moreLayout.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
        buttonMore.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        menuContainer.setVisibility(View.VISIBLE);
        emojicon.setVisibility(View.GONE);

    }

    /**
     * 显示键盘图标
     */
    protected void setModeKeyboard() {
        edittext_layout.setVisibility(View.VISIBLE);
        moreLayout.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        mEditTextContent.requestFocus();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            buttonMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            buttonMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }

    }
    
    /**
     * 显示或隐藏图标按钮页
     * 
     */
    protected void toggleMore() {
        if (moreLayout.getVisibility() == View.GONE) {
            hideKeyboard();
            moreLayout.setVisibility(View.VISIBLE);
            menuContainer.setVisibility(View.VISIBLE);
            emojicon.setVisibility(View.GONE);
        } else {
            if (emojicon.getVisibility() == View.VISIBLE) {
                emojicon.setVisibility(View.GONE);
                menuContainer.setVisibility(View.VISIBLE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
            } else {
                moreLayout.setVisibility(View.GONE);
            }

        }

    }
    
    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    
    /**
     * 设置相关listener
     * @param listener
     */
    public void setChatMenuListener(ChatMenuListener listener){
        this.listener = listener;
    }
    
    public interface ChatMenuListener{
        /**
         * 发送按钮点击事件
         * @param content 发送内容
         */
        void onSendBtnClicked(String content);
    }


}
