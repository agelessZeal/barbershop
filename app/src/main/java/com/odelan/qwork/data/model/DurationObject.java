package com.odelan.qwork.data.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LuckyMan on 2/2/2018.
 */

public class DurationObject {
    private String duration;
    public DurationObject(String duration) {
        this.duration = duration;
    }

    public DurationObject(JSONObject jsonObject) {
        try {
            this.duration = jsonObject.getString("text");
        } catch (JSONException e) {
            this.duration="";
        }
    }
    public String getText() {
        return duration;
    }
}
