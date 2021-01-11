package com.odelan.qwork.data.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LuckyMan on 2/2/2018.
 */

public class DistanceObject {
    double carSpeedVal = 1000;//60km/h=66.67m/min
    double walkSpeedVal = 66.67;//4km/h=66.67m/min
    double bicycleSpeedVal = 250.0;//15km/h

    private String distance;
    private int distancevalue=0;
    public DistanceObject(String distance) {
        this.distance = distance;
    }
    public DistanceObject(JSONObject jsonObject) {
        try {
            this.distance = jsonObject.getString("text");
            this.distancevalue = jsonObject.getInt("value");
        } catch (JSONException e) {
            this.distance="";
            this.distancevalue=0;
        }
    }
    public String getText() {
        return distance;
    }
    public int getValue() {
        return distancevalue;
    }

    public String getBicycleTime() {

        return time2String(distancevalue / bicycleSpeedVal);
    }

    public String getWalkTime() {
        return time2String(distancevalue / walkSpeedVal);
    }

    public String getCarTime() {
        return time2String(distancevalue / carSpeedVal);
    }

    private String time2String(double estTime){
        String strTime = "";
        if(estTime < 1) estTime = 1;
        int wtime = (int)estTime;
        if (wtime < 0) wtime = 0;
        int h=0;
        int m=wtime;
        if(wtime>59){
            h = wtime/60;
            m = wtime%60;
            strTime = h + " hour";
            if(h > 1)
                strTime += "s";
            if(m > 1)
                strTime += " " + m + " mins";
            else if(m == 1)
                strTime += " 1 min";

        } else {
            strTime =  m+" min";
            if(m > 1) strTime += "s";
        }
        return strTime;

    }
}
