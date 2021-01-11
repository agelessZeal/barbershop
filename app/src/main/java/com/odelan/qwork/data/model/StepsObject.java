package com.odelan.qwork.data.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LuckyMan on 2/2/2018.
 */

public class StepsObject {
    private PolylineObject polyline;
    public StepsObject(PolylineObject polyline) {
        this.polyline = polyline;
    }

    public StepsObject(JSONObject jsonObject) {
        try {
            this.polyline = new PolylineObject(jsonObject.getJSONObject("polyline"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public PolylineObject getPolyline() {
        return polyline;
    }
}
