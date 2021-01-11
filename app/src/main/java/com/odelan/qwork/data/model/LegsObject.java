package com.odelan.qwork.data.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LuckyMan on 2/2/2018.
 */

public class LegsObject {
    private List<StepsObject> steps;

    private DistanceObject distance;

    private DurationObject duration;

    public LegsObject(DurationObject duration, DistanceObject distance, List<StepsObject> steps) {
        this.duration = duration;
        this.distance = distance;
        this.steps = steps;
    }

    public LegsObject(JSONObject jsonObject) {
        try {

            distance = new DistanceObject(jsonObject.getJSONObject("distance"));
            duration = new DurationObject(jsonObject.getJSONObject("duration"));
            JSONArray jsonSteps = jsonObject.getJSONArray("steps");
            steps = new ArrayList<>();
            for (int i = 0; i < jsonSteps.length(); i++) {
                steps.add(new StepsObject(jsonSteps.getJSONObject(i)));
            }
        }catch (Exception e){

        }
    }

    public List<StepsObject> getSteps() {
        return steps;
    }

    public DistanceObject getDistance() {
        return distance;
    }

    public DurationObject getDuration() {
        return duration;
    }
}
