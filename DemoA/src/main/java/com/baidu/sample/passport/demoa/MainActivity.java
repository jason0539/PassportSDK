package com.baidu.sample.passport.demoa;

import com.baidu.sample.passportsdk.callback.OnPullResultListener;
import com.baidu.sample.passportsdk.model.ShareEvent;
import com.baidu.sample.passportsdk.model.ShareModel;
import com.baidu.sample.passportsdk.ShareManager;
import com.baidu.sample.passportsdk.utils.MLog;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
        bnPush.setOnClickListener(getOnClickLis());
        bnPull.setOnClickListener(getOnClickLis());
    }

    private View.OnClickListener getOnClickLis() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bn_pull:
                        Log.d("lzh", "pull");
                        ShareManager.getInstance().pullMessage(new OnPullResultListener() {
                            @Override
                            public void onSuccess(ShareModel shareModel) {
                                MLog.d("我是Demo A，收到pull的回应" + shareModel.getData());
                            }
                        });
                        break;
                    case R.id.bn_push:
                        ShareManager.getInstance().pushMessage(new ShareModel(ShareEvent.PUSH, "我是Demo A，我发出Push"));
                        break;
                }

            }
        };
    }

}
