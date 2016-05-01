package com.baidu.sample.passportsdk.callback;

import com.baidu.sample.passportsdk.model.ShareModel;

/**
 * Created by liuzhenhui on 15/8/8.
 */
public interface OnReceivePushEventListener {
    /**
     * 收到其他应用push过来的共享数据，上层自行决定如何使用
     *
     * @param data
     */
    void onReceivePushEvent(ShareModel data);
}
