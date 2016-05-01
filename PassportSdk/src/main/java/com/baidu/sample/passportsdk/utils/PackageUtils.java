package com.baidu.sample.passportsdk.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.baidu.sample.passportsdk.AccountManager;
import com.baidu.sample.passportsdk.callback.OnPackageUtilsGetIntentListener;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;

/**
 * Created by liuzhenhui on 15/8/7.
 */
public class PackageUtils {
    public static final String SERVICE_ACTION = "me.intent.action.account.SHARE_SERVICE";


    /**
     * 获取植入共享SDK的所有应用对应服务的启动intent
     *
     * @param callback       intent回掉接口
     * @param includeMyselfe 是否包含自身
     */
    public static void getAllSharedAppIntent(OnPackageUtilsGetIntentListener callback, boolean includeMyselfe) {
        Map shareblePackages = getPackageIntent(includeMyselfe);
        Iterator iterator = shareblePackages.values().iterator();
        while (iterator.hasNext()) {
            Intent intent = (Intent) iterator.next();
            if (intent != null) {
                callback.intentSuccess(intent);
            }
        }
    }

    /**
     * 获取所有声明了共享服务的应用的对应intent
     *
     * @param includeMyselfe 是否包括自身
     * @return map key=包名 value=intent
     */
    private static Map<String, Intent> getPackageIntent(boolean includeMyselfe) {
        HashMap serviceMap = new HashMap();
        List packageList = AccountManager.getInstance().getContext().getPackageManager().queryIntentServices(new Intent(SERVICE_ACTION), PackageManager.GET_INTENT_FILTERS);
        MLog.d("获取到包个数" + packageList.size());
        if (packageList != null) {
            Iterator iterator = packageList.iterator();
            ServiceInfo serviceInfo;
            ResolveInfo resolveInfo;
            Intent intent;
            Context context = AccountManager.getInstance().getContext();
            do {
                do {
                    resolveInfo = (ResolveInfo) iterator.next();
                    serviceInfo = resolveInfo.serviceInfo;
                } while (serviceInfo == null);
                if (!includeMyselfe && serviceInfo.packageName.equals(context.getPackageName())) {
                    continue;
                }
                intent = new Intent(SERVICE_ACTION);
                intent.setClassName(serviceInfo.packageName, serviceInfo.name);
                if (Build.VERSION.SDK_INT > 11) {
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                }
                serviceMap.put(serviceInfo.packageName, intent);
            }
//            while (!TextUtils.isEmpty(serviceInfo.permission) && context.checkCallingOrSelfPermission(serviceInfo.permission) != PackageManager.PERMISSION_GRANTED);
            while (iterator.hasNext());

            context = null;
        } else {
            MLog.d("getPackageIntent 获取到的包为空");
        }
        MLog.d("成功返回intent个数" + serviceMap.size());
        return serviceMap;
    }
}
