package com.baidu.sample.passportsdk.share;

import com.baidu.sample.passportsdk.AccountManager;
import com.baidu.sample.passportsdk.callback.OnPackageUtilsGetIntentListener;
import com.baidu.sample.passportsdk.callback.OnPullResultListener;
import com.baidu.sample.passportsdk.model.ShareEvent;
import com.baidu.sample.passportsdk.model.ShareModel;
import com.baidu.sample.passportsdk.utils.MLog;
import com.baidu.sample.passportsdk.utils.PackageUtils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by liuzhenhui on 15/8/8.
 */
public class ShareManager {
    private Context mContext;

    public ShareManager() {
        mContext = AccountManager.getInstance().getContext();
    }

    public void pullRemote(OnPullResultListener resultListener) {
        HandlerThread pullThread = new HandlerThread("PullThread");
        pullThread.start();
        Handler pullHandler = new Handler(pullThread.getLooper());
        pullHandler.post(new PullRunnable(pullHandler, resultListener));
    }

    class PullRunnable implements Runnable {
        Handler pullHandler;
        OnPullResultListener resultListener;

        public PullRunnable(Handler pullHandler, OnPullResultListener resultListener) {
            this.pullHandler = pullHandler;
            this.resultListener = resultListener;
        }

        @Override
        public void run() {
            PackageUtils.getAllSharedAppIntent(getIntentListener, false);
        }

        OnPackageUtilsGetIntentListener getIntentListener = new OnPackageUtilsGetIntentListener() {
            @Override
            public void intentSuccess(Intent intent) {
                mContext.bindService(intent, new MyServiceConnection(), Context.BIND_AUTO_CREATE);
            }

            class MyServiceConnection implements ServiceConnection {


                @Override
                public void onServiceConnected(ComponentName name, IBinder mIBinder) {
                    MLog.d("bind Service连接建立成功");
                    final ServiceConnection connection = this;
                    pullHandler.post(new ServiceConnectedRunnable(mIBinder, connection));
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    MLog.d("bind Service连接断开");
                }

                class ServiceConnectedRunnable implements Runnable {
                    ServiceConnection pullServiceConnection;
                    IBinder mIBinder;

                    public ServiceConnectedRunnable(IBinder iBinder, ServiceConnection connection) {
                        pullServiceConnection = connection;
                        mIBinder = iBinder;
                    }

                    @Override
                    public void run() {
                        try {
                            Parcel data = Parcel.obtain();
                            Parcel reply = Parcel.obtain();
                            ShareModel pullData = new ShareModel(ShareEvent.PULL, "This is a PullEvent from " + mContext.getPackageName());
                            pullData.writeToParcel(data, 0);
                            boolean result = mIBinder.transact(0, data, reply, 0);
                            if (result) {
                                resultListener.onSuccess(ShareModel.CREATOR.createFromParcel(reply));
                                MLog.d("pull请求发送成功");
                            } else {
                                MLog.d("pull请求发送失败");
                            }
                            mContext.unbindService(pullServiceConnection);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    /**
     * 向其他共享SDK应用共享消息
     *
     * @param shareMsg
     */
    public void pushRemote(ShareModel shareMsg) {
        HandlerThread pushThread = new HandlerThread("PushThread");
        pushThread.start();
        Handler pushHandler = new Handler(pushThread.getLooper());
        pushHandler.post(new PushRunnable(pushHandler, shareMsg));
    }

    class PushRunnable implements Runnable {
        Handler pushHandler;
        ShareModel pushData;

        public PushRunnable(Handler pushHandler, ShareModel msg) {
            this.pushHandler = pushHandler;
            this.pushData = msg;
        }

        @Override
        public void run() {
            PackageUtils.getAllSharedAppIntent(getIntentListener, false);
        }

        OnPackageUtilsGetIntentListener getIntentListener = new OnPackageUtilsGetIntentListener() {
            @Override
            public void intentSuccess(Intent intent) {
                mContext.bindService(intent, new MyServiceConnection(), Context.BIND_AUTO_CREATE);
            }

            class MyServiceConnection implements ServiceConnection {


                @Override
                public void onServiceConnected(ComponentName name, IBinder mIBinder) {
                    MLog.d("bind Service连接建立成功");
                    final ServiceConnection connection = this;
                    pushHandler.post(new ServiceConnectedRunnable(mIBinder, connection));
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    MLog.d("bind Service连接断开");
                }

                class ServiceConnectedRunnable implements Runnable {
                    ServiceConnection pushServiceConnection;
                    IBinder mIBinder;

                    public ServiceConnectedRunnable(IBinder iBinder, ServiceConnection connection) {
                        pushServiceConnection = connection;
                        mIBinder = iBinder;
                    }

                    @Override
                    public void run() {
                        try {
                            Parcel data = Parcel.obtain();
                            Parcel reply = Parcel.obtain();
                            pushData.writeToParcel(data, 0);
                            boolean result = mIBinder.transact(0, data, reply, 0);
                            if (result) {
                                MLog.d("数据发送成功");
                            } else {
                                MLog.d("数据发送失败");
                            }
                            mContext.unbindService(pushServiceConnection);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }
}