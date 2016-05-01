package com.baidu.sample.passportsdk;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.baidu.sample.passportsdk.callback.OnPackageUtilsGetIntentListener;
import com.baidu.sample.passportsdk.callback.OnPullResultListener;
import com.baidu.sample.passportsdk.callback.OnReceivePullEventListener;
import com.baidu.sample.passportsdk.callback.OnReceivePushEventListener;
import com.baidu.sample.passportsdk.model.ShareModel;
import com.baidu.sample.passportsdk.utils.MLog;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by liuzhenhui on 15/8/8.
 */
public class ShareManager {
    public static final String SERVICE_ACTION = "me.intent.action.account.SHARE_SERVICE";

    private Context mContext;
    private OnReceivePushEventListener mReceivePushLis = null;
    private OnReceivePullEventListener mReceivePullLis = null;

    private ShareManager() {
    }

    private static class LazyHolder {
        private static final ShareManager INSTANCE = new ShareManager();
    }

    public static final ShareManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void init(Context context, OnReceivePushEventListener pushEventListener,
                     OnReceivePullEventListener pullEventListener) {
        mContext = context;
        mReceivePushLis = pushEventListener;
        mReceivePullLis = pullEventListener;
        MLog.init(mContext);
    }

    public void pushMessage(ShareModel msg) {
        HandlerThread pushThread = new HandlerThread("PushThread");
        pushThread.start();
        Handler pushHandler = new Handler(pushThread.getLooper());
        pushHandler.post(new PushRunnable(pushHandler, msg));
    }

    public void pullMessage(OnPullResultListener pullResultListener) {
        HandlerThread pullThread = new HandlerThread("PullThread");
        pullThread.start();
        Handler pullHandler = new Handler(pullThread.getLooper());
        pullHandler.post(new PullRunnable(pullHandler, pullResultListener));
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
            callAllApps(getIntentListener, false);
        }

        OnPackageUtilsGetIntentListener getIntentListener = new OnPackageUtilsGetIntentListener() {

            @Override
            public void onGetIntentSuccess(Intent intent) {
                mContext.bindService(intent, new MyServiceConnection(), Context.BIND_AUTO_CREATE);
            }

            class MyServiceConnection implements ServiceConnection {

                @Override
                public void onServiceConnected(ComponentName name, IBinder mIBinder) {
                    MLog.d("bind Service连接建立成功");
                    ServiceConnection connection = this;
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
                            ShareModel pullData = new ShareModel(
                                    ShareModel.ShareEvent.PULL, "This is a PullEvent from " + mContext.getPackageName());
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

    class PushRunnable implements Runnable {
        private Handler pushHandler;
        private ShareModel pushData;

        public PushRunnable(Handler pushHandler, ShareModel msg) {
            this.pushHandler = pushHandler;
            this.pushData = msg;
        }

        @Override
        public void run() {
            callAllApps(getIntentListener, false);
        }

        OnPackageUtilsGetIntentListener getIntentListener = new OnPackageUtilsGetIntentListener() {

            @Override
            public void onGetIntentSuccess(Intent intent) {
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

    public void callAllApps(OnPackageUtilsGetIntentListener callback, boolean includeMyselfe) {
        Map appsIntentMap = getAllAppsIntent(includeMyselfe);
        Iterator iterator = appsIntentMap.values().iterator();
        while (iterator.hasNext()) {
            Intent intent = (Intent) iterator.next();
            if (intent != null) {
                callback.onGetIntentSuccess(intent);
            }
        }
    }

    private Map<String, Intent> getAllAppsIntent(boolean includeMyselfe) {
        HashMap serviceMap = new HashMap();
        List packageList = mContext.getPackageManager().
                queryIntentServices(new Intent(SERVICE_ACTION), PackageManager.GET_INTENT_FILTERS);
        MLog.d("查询到内置了SDK的应用个数为：" + packageList.size());
        if (packageList != null) {
            Iterator iterator = packageList.iterator();
            ServiceInfo serviceInfo;
            ResolveInfo resolveInfo;
            Intent intent;
            do {
                do {
                    resolveInfo = (ResolveInfo) iterator.next();
                    serviceInfo = resolveInfo.serviceInfo;
                } while (serviceInfo == null);
                if (!includeMyselfe && serviceInfo.packageName.equals(mContext.getPackageName())) {
                    continue;
                }
                intent = new Intent(SERVICE_ACTION);
                intent.setClassName(serviceInfo.packageName, serviceInfo.name);
                if (Build.VERSION.SDK_INT > 11) {
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                }
                serviceMap.put(serviceInfo.packageName, intent);
            }
            while (iterator.hasNext());

        } else {
            MLog.d("本机没有安装内置Passport SDK的应用");
        }
        MLog.d("成功获取到启动应用的intent个数：" + serviceMap.size());
        return serviceMap;
    }

    public OnReceivePullEventListener getReceivePullLis() {
        return mReceivePullLis;
    }

    public OnReceivePushEventListener getReceiveShareLis() {
        return mReceivePushLis;
    }
}