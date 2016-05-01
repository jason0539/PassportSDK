package com.baidu.sample.passportsdk.service;

import com.baidu.sample.passportsdk.model.ShareEvent;
import com.baidu.sample.passportsdk.model.ShareModel;
import com.baidu.sample.passportsdk.ShareManager;
import com.baidu.sample.passportsdk.utils.MLog;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by liuzhenhui on 15/8/7.
 */
public class ShareService extends Service {

    private Handler mHandler;
    private MyBinder myBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    class MyBinder extends Binder {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (mHandler == null) {
                mHandler = new Handler(Looper.getMainLooper());
            }
            ShareModel shareModel = ShareModel.CREATOR.createFromParcel(data);

            switch (shareModel.getEvent().ordinal()) {
                case 0://pull
                    reply.writeSerializable(ShareEvent.ACK);
                    reply.writeString(getPackageName());
                    MLog.d("我是Service，收到pull消息：" + shareModel.getData());
                    noticePullListener();
                    break;
                case 1://push
                    noticePushListener(shareModel);
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private void noticePushListener(final ShareModel shareModel) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (ShareManager.getInstance().getReceiveShareLis() != null) {
                    ShareManager.getInstance().getReceiveShareLis().onReceivePushEvent(shareModel);
                }
            }
        });
    }

    private void noticePullListener() {
        //回调消息放到主线程
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (ShareManager.getInstance().getReceivePullLis() != null) {
                    ShareManager.getInstance().getReceivePullLis().onReceivePullEvent();
                }
            }
        });
    }
}

