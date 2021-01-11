package com.odelan.qwork.event;

/**
 * Created by LuckyMan on 2/2/2018.
 */

public class Event {

    public  enum EVENT_TYPE {USER_REMOVED, FREEZED, BS_DELETED};

    EVENT_TYPE e;

    public Event(EVENT_TYPE e){
        this.e = e;
    }

    public EVENT_TYPE getEvent() {
        return e;
    }
}
