package org.apache.cordova.media;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;


public class PlayerManager extends Activity {

    HeadsetPlugReceiver headsetPlugReceiver;
    /**
     * 外放模式
     */
    public static final String MODE_SPEAKER = "speaker";
    /**
     * 耳机模式
     */
    public static final String MODE_HEADSET = "headsetMode";
    /**
     * 听筒模式
     */
    public static final String MODE_EARPIECE = "receiver";
    private static PlayerManager playerManager;
    private AudioManager audioManager;

    private Context context;
    private String PlayMode;   //当前播放模式
    private String currentMode = MODE_SPEAKER;

    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        audioManager = (AudioManager) PlayerManager.this.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }
        audioManager.setSpeakerphoneOn(true);  //默认为扬声器播放
        PlayMode = this.getIntent().getStringExtra( "PlayMode" );
        registerHeadsetPlugReceiver();  // 注册监听
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(headsetPlugReceiver);  //注销监听
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }
    /**
     * 切换到听筒模式
     */
    public void changeToEarpieceMode(){
        currentMode = MODE_EARPIECE;
        audioManager.setSpeakerphoneOn(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.MODE_IN_COMMUNICATION), AudioManager.FX_KEY_CLICK);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.MODE_IN_CALL), AudioManager.FX_KEY_CLICK);
        }
    }

    /**
     * 切换到耳机模式
     */
    public void changeToHeadsetMode(){
        currentMode = MODE_HEADSET;
        audioManager.setSpeakerphoneOn(false);
    }

    /**
     * 切换到外放模式
     */
    public void changeToSpeakerMode(){
        currentMode = MODE_SPEAKER;
        audioManager.setSpeakerphoneOn(true);
    }

    private void registerHeadsetPlugReceiver() {
        headsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetPlugReceiver, filter);
    }

    class HeadsetPlugReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) {
                    if(PlayMode.equals( "speaker" )){
                        changeToSpeakerMode();
                    }else if(PlayMode.equals( "receiver" )){
                        changeToEarpieceMode();
                    }
                    Toast.makeText(context, "耳机已拔出", Toast.LENGTH_LONG).show();
                } else if (intent.getIntExtra("state", 0) == 1) {
                    changeToHeadsetMode();
                    Toast.makeText(context, "耳机已连接", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
