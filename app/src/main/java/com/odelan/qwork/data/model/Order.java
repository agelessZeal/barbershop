package com.odelan.qwork.data.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by Administrator on 7/12/2017.
 */

@JsonObject
public class Order extends BaseModel {

    @JsonField(name = "order_id")
    public String order_id;

    @JsonField(name = "customer_id")
    public String customer_id;

    @JsonField(name = "barber_id")
    public String barber_id;

    @JsonField(name = "start_time")
    public String start_time;

    @JsonField(name = "end_time")
    public String end_time;

    @JsonField(name = "rating")
    public String rating;

    @JsonField(name = "comment")
    public String comment;

    @JsonField(name = "reason")
    public String reason;

    @JsonField(name = "status")
    public String status;

    @JsonField(name = "created_on")
    public String created_on;
}
