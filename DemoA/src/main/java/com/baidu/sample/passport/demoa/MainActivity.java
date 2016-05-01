package com.baidu.sample.passport.demoa;

import com.baidu.sample.passportsdk.ShareManager;
import com.baidu.sample.passportsdk.callback.OnPullResultListener;
import com.baidu.sample.passportsdk.callback.OnReceivePullEventListener;
import com.baidu.sample.passportsdk.callback.OnReceivePushEventListener;
import com.baidu.sample.passportsdk.model.ShareModel;
import com.baidu.sample.passportsdk.utils.MLog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    Button bnPush;
    Button bnPull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bnPull = (Button) findViewById(R.id.bn_pull);
        bnPush = (Button) findViewById(R.id.bn_push);

        initPassportSDK();

        bnPush.setOnClickListener(getOnClickLis());
        bnPull.setOnClickListener(getOnClickLis());
    }

    private void initPassportSDK() {
        ShareManager.getInstance().init(this, pushEventLis, pullEventLis);
    }

    OnReceivePullEventListener pullEventLis = new OnReceivePullEventListener() {
        @Override
        public ShareModel onReceivePullEvent() {
            MLog.d("我是DemoA，收到pull消息");
            ShareModel shareModel = new ShareModel(ShareModel.ShareEvent.ACK, "我是A，我回应了Pull消息");
            return shareModel;
        }
    };

    OnReceivePushEventListener pushEventLis = new OnReceivePushEventListener() {
        @Override
        public void onReceivePushEvent(ShareModel shareModel) {
            MLog.d("我是DemoA，收到push消息：" + shareModel.getData());
        }
    };

    OnPullResultListener onPullResultListener = new OnPullResultListener() {
        @Override
        public void onSuccess(ShareModel shareModel) {
            MLog.d("A==>我是Demo A，我收到了pull的回应，内容是：" + shareModel.getData());
        }
    };

    private View.OnClickListener getOnClickLis() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bn_pull:
                        ShareManager.getInstance().pullMessage(onPullResultListener);
                        break;
                    case R.id.bn_push:
                        ShareModel shareModel =
                                new ShareModel(ShareModel.ShareEvent.PUSH, "A==>我是Demo A，我发出了这个Push消息");
                        ShareManager.getInstance().pushMessage(shareModel);
                        break;
                }

            }
        };
    }

}
