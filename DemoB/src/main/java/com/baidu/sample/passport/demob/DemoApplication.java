package com.baidu.sample.passport.demob;

import android.app.Application;

import com.baidu.sample.passportsdk.AccountManager;
import com.baidu.sample.passportsdk.callback.OnReceivePullEventListener;
import com.baidu.sample.passportsdk.callback.OnReceivePushEventListener;
import com.baidu.sample.passportsdk.model.ShareModel;
import com.baidu.sample.passportsdk.utils.MLog;

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
        AccountManager.getInstance().init(this, pushEventLis, pullEventLis);

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
