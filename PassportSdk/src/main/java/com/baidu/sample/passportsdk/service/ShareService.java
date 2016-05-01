package com.baidu.sample.passportsdk.service;

import com.baidu.sample.passportsdk.ShareManager;
import com.baidu.sample.passportsdk.model.ShareModel;

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
                    ShareModel replyShareModel = getPullReply();
                    replyShareModel.writeToParcel(reply,0);
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

    private ShareModel getPullReply() {
        ShareModel shareModel = null;
        if (ShareManager.getInstance().getReceivePullLis() != null) {
            shareModel = ShareManager.getInstance().getReceivePullLis().onReceivePullEvent();
        }
        return shareModel;
    }
}

