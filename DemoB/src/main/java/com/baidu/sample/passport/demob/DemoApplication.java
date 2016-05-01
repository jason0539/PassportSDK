package com.baidu.sample.passport.demob;

import com.baidu.sample.passportsdk.callback.OnReceivePullEventListener;
import com.baidu.sample.passportsdk.callback.OnReceivePushEventListener;
import com.baidu.sample.passportsdk.model.ShareModel;
import com.baidu.sample.passportsdk.ShareManager;
import com.baidu.sample.passportsdk.utils.MLog;

import android.app.Application;

/**
 * Created by liuzhenhui on 15/8/8.下午9:50
 */
public class DemoApplication extends Application {
    private static final String TAG = "DemoApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        initPassportSDK();
    }

    private void initPassportSDK() {
        ShareManager.getInstance().init(this, pushEventLis, pullEventLis);

    }

    OnReceivePullEventListener pullEventLis = new OnReceivePullEventListener() {
        @Override
        public void onReceivePullEvent() {
            MLog.d("我是DemoB，收到pull事件");
        }
    };
    OnReceivePushEventListener pushEventLis = new OnReceivePushEventListener() {
        @Override
        public void onReceivePushEvent(ShareModel shareModel) {
            MLog.d("我是DemoB，收到push事件：" + shareModel.getData());
        }
    };
}
