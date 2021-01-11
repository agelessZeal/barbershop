package com.odelan.qwork.data.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LuckyMan on 2/2/2018.
 */

public class DirectionObject {
    private List<RouteObject> routes;
    private String status;
    public DirectionObject(List<RouteObject> routes, String status) {
        this.routes = routes;
        this.status = status;
    }

    public DirectionObject(JSONObject jsonObject) {
        try {
            this.status = jsonObject.getString("status");
            this.routes = new ArrayList<>();

            if("OK".equals(this.status)) {
                JSONArray jsonRoutes = jsonObject.getJSONArray("routes");
                for (int i = 0; i < jsonRoutes.length(); i++) {
                    routes.add(new RouteObject(jsonRoutes.getJSONObject(i)));
                }
            }
        }catch (Exception e){
            this.status = "";
        }

    }

    public List<RouteObject> getRoutes() {
        return routes;
    }
    public String getStatus() {
        return status;
    }
}
