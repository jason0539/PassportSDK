package com.baidu.sample.passportsdk.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liuzhenhui on 15/8/7.
 */
public class AccountData implements Parcelable{
    protected AccountData(Parcel in) {
    }

    public static final Creator<AccountData> CREATOR = new Creator<AccountData>() {
        @Override
        public AccountData createFromParcel(Parcel in) {
            return new AccountData(in);
        }

        @Override
        public AccountData[] newArray(int size) {
            return new AccountData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
