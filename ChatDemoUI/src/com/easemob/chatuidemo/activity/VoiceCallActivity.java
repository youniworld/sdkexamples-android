/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.easemob.chatuidemo.activity;

import java.util.HashMap;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.R;
import com.easemob.exceptions.EMServiceNotReadyException;

public class VoiceCallActivity extends BaseActivity implements OnClickListener {
	private LinearLayout comingBtnContainer;
	private Button hangupBtn;
	private Button refuseBtn;
	private Button answerBtn;
	private ImageView muteImage;
	private ImageView handsFreeImage;

	private boolean isMuteState;
	private boolean isHandsfreeState;
	private boolean isComingCall;
	private TextView callStateTextView;
	private SoundPool soundPool;
	private int streamID;
	private boolean endCallTriggerByMe = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_call);

		comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
		refuseBtn = (Button) findViewById(R.id.btn_refuse_call);
		answerBtn = (Button) findViewById(R.id.btn_answer_call);
		hangupBtn = (Button) findViewById(R.id.btn_hangup_call);
		muteImage = (ImageView) findViewById(R.id.iv_mute);
		handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
		callStateTextView = (TextView) findViewById(R.id.tv_call_state);
		nickTextView = (TextView) findViewById(R.id.tv_nick);

		refuseBtn.setOnClickListener(this);
		answerBtn.setOnClickListener(this);
		hangupBtn.setOnClickListener(this);
		muteImage.setOnClickListener(this);
		handsFreeImage.setOnClickListener(this);

		// 注册语音电话的状态的监听
		EMChatManager.getInstance().addVoiceCallStateChangeListener(new EMCallStateChangeListener() {

			@Override
			public void onCallStateChanged(CallState callState, CallError error) {
//				Message msg = handler.obtainMessage();
				switch (callState) {

				case CONNECTING: // 正在连接对方
				    VoiceCallActivity.this.runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            callStateTextView.setText("正在连接对方...");
                        }
                        
                    });
				    break;
				case CONNECTED: // 双方已经建立连接
				    VoiceCallActivity.this.runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            callStateTextView.setText("已经和对方建立连接，等待对方接受...");
                        }
				        
				    });
					break;
				
				case ACCEPTED: // 电话接通成功
				    VoiceCallActivity.this.runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            callStateTextView.setText("通话中...");
                        }
				        
				    });
				    break;
				case DISCONNNECTED: // 电话断了
				    final CallError fError = error;
				    VoiceCallActivity.this.runOnUiThread(new Runnable(){
                        private void postDelayedCloseMsg(){
                            handler.postDelayed(new Runnable(){

                                @Override
                                public void run() {
                                    finish();                                       
                                }
                                
                            }, 3000);
                        }
                        
                        @Override
                        public void run() {
                            if(fError == CallError.REJECTED){
                                callStateTextView.setText("对方拒绝接受！...");
                                postDelayedCloseMsg();
                            }else if(fError == CallError.ERROR_TRANSPORT){
                                callStateTextView.setText("连接建立失败！...");
                                postDelayedCloseMsg();
                            }else if(fError == CallError.ERROR_INAVAILABLE){
                                callStateTextView.setText("对方不在线，请稍后再拨...");
                                postDelayedCloseMsg();
                            }else{
                                if(endCallTriggerByMe){
                                    callStateTextView.setText("挂断...");
                                }else{
                                    callStateTextView.setText("对方已经挂断...");
                                }
                                postDelayedCloseMsg();
                            }
                        }
                        
                    });
					
					break;
					
				default:
					break;
				}

			}
		});

		// 对方username
		String username = getIntent().getStringExtra("username");
		nickTextView.setText(username);
		
		// 语音电话是否为接收的
		isComingCall = getIntent().getBooleanExtra("isComingCall", false);
		
		
		if (!isComingCall) {//拨打电话
			//soundpool
			//soundPool = new SoundPool(1,AudioManager.STREAM_RING,0);
			//outgoing = soundPool.load(this, R.raw.outgoing, 1);
			try {
				comingBtnContainer.setVisibility(View.INVISIBLE);
				hangupBtn.setVisibility(View.VISIBLE);
				callStateTextView.setText("正在呼叫...");
				/*handler.postDelayed(new Runnable() {
					public void run() {
						streamID = playSounds();
					}
				}, 200);*/
				// 拨打语言电话
				EMChatManager.getInstance().makeVoiceCall(username);
			} catch (EMServiceNotReadyException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(VoiceCallActivity.this, "尚未连接至服务器", 0);
					}
				});
			}
		}else{ //有电话进来
			Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			ringtone = RingtoneManager.getRingtone(this, ringUri);
			ringtone.play();
		}
	}

	private Handler handler = new Handler();
	private Ringtone ringtone;
	private int outgoing;
	private TextView nickTextView;
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_refuse_call: // 拒绝接听
			if(ringtone != null)
				ringtone.stop();
		    EMChatManager.getInstance().rejectCall();
			finish();
			break;

		case R.id.btn_answer_call: // 接听电话
			comingBtnContainer.setVisibility(View.INVISIBLE);
			hangupBtn.setVisibility(View.VISIBLE);
			if(ringtone != null)
				ringtone.stop();
			if (isComingCall) {
				EMChatManager.getInstance().answerCall();
			}
			break;

		case R.id.btn_hangup_call: // 挂断电话
			//soundPool.stop(streamID);
		    endCallTriggerByMe = true;
			try {
				EMChatManager.getInstance().endCall();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finish();
			break;

		case R.id.iv_mute: // 静音开关
			if (isMuteState) {
				// 关闭静音开关
				muteImage.setImageResource(R.drawable.icon_mute_normal);
				isMuteState = false;
			} else {
				muteImage.setImageResource(R.drawable.icon_mute_on);
				isMuteState = true;
			}
			break;
		case R.id.iv_handsfree: // 免提开关
			if (isHandsfreeState) {
				// 关闭静音开关
				handsFreeImage.setImageResource(R.drawable.icon_speaker_normal);
				isHandsfreeState = false;
			} else {
				handsFreeImage.setImageResource(R.drawable.icon_speaker_on);
				isHandsfreeState = true;
			}
			break;
		default:
			break;
		}
	}
	

	/**
	 * 播放响铃
	 * @param sound
	 * @param number
	 */
	private int playSounds(){
	    try {
			AudioManager am = (AudioManager)this.getSystemService(this.AUDIO_SERVICE);
			//最大音量
			float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_RING);
			//当前音量
			float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_RING);
			float volumnRatio = audioCurrentVolumn/audioMaxVolumn;
			
			//播放
			int id = soundPool.play(outgoing,     //声音资源
			    volumnRatio,         //左声道
			    volumnRatio,         //右声道
			    1,             //优先级，0最低
			    -1,         //循环次数，0是不循环，-1是永远循环
			    1);            //回放速度，0.5-2.0之间。1为正常速度
			return id;
		} catch (Exception e) {
			System.out.println(e);
			return -1;
		}
	}

}
