package com.mahankalstatus.android.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.mahankalstatus.android.BR;

public class AddStatus extends BaseObservable {
    private String id = "";
    private String status = "New status";
    private String serverKey = "";
    private String isApproved = "0";

    public AddStatus() {
    }

    public AddStatus(String id, String status, String serverKey, String isApproved) {
        this.id = id;
        this.status = status;
        this.serverKey = serverKey;
        this.isApproved = isApproved;
    }

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    @Bindable
    public String getServerKey() {
        return serverKey;
    }

    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
        notifyPropertyChanged(BR.serverKey);
    }

    @Bindable
    public String getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(String isApproved) {
        this.isApproved = isApproved;
        notifyPropertyChanged(BR.isApproved);
    }
}
