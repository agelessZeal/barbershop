package com.odelan.qwork.data.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LuckyMan on 2/2/2018.
 */

public class RouteObject {
    private List<LegsObject> legs;
    public RouteObject(List<LegsObject> legs) {
        this.legs = legs;
    }

    public RouteObject(JSONObject jsonObject) {
        try {
            legs = new ArrayList<>();
            JSONArray jsonLegs = jsonObject.getJSONArray("legs");
            for (int i = 0; i < jsonLegs.length(); i++) {
                legs.add(new LegsObject(jsonLegs.getJSONObject(i)));
            }
        }catch (Exception e){

        }
    }
    public List<LegsObject> getLegs() {
        return legs;
    }
}
