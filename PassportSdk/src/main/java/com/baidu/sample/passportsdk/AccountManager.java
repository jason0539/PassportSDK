package com.baidu.sample.passportsdk;

import android.content.Context;

import com.baidu.sample.passportsdk.callback.OnPullResultListener;
import com.baidu.sample.passportsdk.callback.OnReceivePullEventListener;
import com.baidu.sample.passportsdk.callback.OnReceivePushEventListener;
import com.baidu.sample.passportsdk.model.ShareModel;
import com.baidu.sample.passportsdk.share.ShareManager;

/**
 * Created by liuzhenhui on 15/8/7.
 */
public class AccountManager {
    private Context mContext;
    private ShareManager share;

    public Context getContext() {
        return mContext;
    }

    public void pushMessage(ShareModel msg) {
        share.pushRemote(msg);
    }

    /**
     * 发送pull请求
     *
     * @param pullResultListener pull结果监听
     */
    public void pullMessage(OnPullResultListener pullResultListener) {
        share.pullRemote(pullResultListener);
    }


    /**
     * 初始化SDK，监听pull和push分享事件
     *
     * @param context
     * @param pushEventListener
     * @param pullEventListener
     */
    public void init(Context context, OnReceivePushEventListener pushEventListener, OnReceivePullEventListener pullEventListener) {
        mContext = context;
        mReceivePushLis = pushEventListener;
        mReceivePullLis = pullEventListener;
        share = new ShareManager();
    }

    /**
     * 初始化SDK，不监听分享事件
     *
     * @param context
     */
    public void init(Context context) {
        this.init(context, null, null);

    }

    private AccountManager() {
    }

    private static class LazyHolder {
        private static final AccountManager INSTANCE = new AccountManager();
    }

    public static final AccountManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private OnReceivePushEventListener mReceivePushLis;
    private OnReceivePullEventListener mReceivePullLis;

    public OnReceivePullEventListener getReceivePullLis() {
        return mReceivePullLis;
    }

    public void setmReceivePullLis(OnReceivePullEventListener mReceivePullLis) {
        this.mReceivePullLis = mReceivePullLis;
    }

    public OnReceivePushEventListener getReceiveShareLis() {
        return mReceivePushLis;
    }

    public void setmReceivePushLis(OnReceivePushEventListener mReceivePushLis) {
        this.mReceivePushLis = mReceivePushLis;
    }

}
