package com.baidu.sample.passport.democ;

import com.baidu.sample.passportsdk.ShareManager;
import com.baidu.sample.passportsdk.callback.OnReceivePullEventListener;
import com.baidu.sample.passportsdk.callback.OnReceivePushEventListener;
import com.baidu.sample.passportsdk.model.ShareModel;
import com.baidu.sample.passportsdk.utils.MLog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPassportSDK();
    }

    private void initPassportSDK() {
        ShareManager.getInstance().init(this, pushEventLis, pullEventLis);
    }

    OnReceivePullEventListener pullEventLis = new OnReceivePullEventListener() {
        @Override
        public ShareModel onReceivePullEvent() {
            MLog.d("我是DemoC，收到pull消息");
            ShareModel shareModel = new ShareModel(ShareModel.ShareEvent.ACK, "我是C，我回应了Pull消息");
            return shareModel;
        }
    };

    OnReceivePushEventListener pushEventLis = new OnReceivePushEventListener() {
        @Override
        public void onReceivePushEvent(ShareModel shareModel) {
            MLog.d("我是DemoC，收到push消息：" + shareModel.getData());
        }
    };

}
