package com.baidu.sample.passportsdk.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liuzhenhui on 15/8/7.
 */
public class ShareModel implements Parcelable {
    private ShareEvent event;
    private String data;

    protected ShareModel(Parcel in) {
        event = (ShareEvent) in.readSerializable();
        data = in.readString();
    }

    public ShareModel(ShareEvent event, String data) {
        this.data = data;
        this.event = event;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ShareEvent getEvent() {
        return event;
    }

    public void setEvent(ShareEvent event) {
        this.event = event;
    }

    public static final Creator<ShareModel> CREATOR = new Creator<ShareModel>() {
        @Override
        public ShareModel createFromParcel(Parcel in) {
            return new ShareModel(in);
        }

        @Override
        public ShareModel[] newArray(int size) {
            return new ShareModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(event);
        dest.writeString(data);
    }
}
