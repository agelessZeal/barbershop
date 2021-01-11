package com.odelan.qwork.data.model;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LuckyMan on 2/2/2018.
 */

public class PolylineObject {
    private String points;
    public PolylineObject(String points) {
        this.points = points;
    }
    public PolylineObject(JSONObject jsonObject) {
        try {
            this.points = jsonObject.getString("points");
        } catch (JSONException e) {
            this.points = "";
        }
    }
    public String getPoints() {
        return points;
    }
}
