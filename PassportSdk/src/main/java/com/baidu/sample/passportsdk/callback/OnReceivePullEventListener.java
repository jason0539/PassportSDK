package com.baidu.sample.passportsdk.callback;

/**
 * Created by liuzhenhui on 15/8/8.
 */
public interface OnReceivePullEventListener {
    /**
     * 收到其他应用的pull请求，把需要回复的内容写入reply，回复给其他应用
     *
     */
    void onReceivePullEvent();

}
