package com.odelan.qwork.data.model;

/**
 * Created by Administrator on 7/13/2017.
 */

public class NotifyUser {
    public User user;
    public boolean isSelected;

    public NotifyUser() {
        isSelected = false;
    }

    public NotifyUser(User u) {
        user = u;
        isSelected = false;
    }
}
