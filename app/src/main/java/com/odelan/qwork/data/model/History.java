package com.odelan.qwork.data.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by Administrator on 7/12/2017.
 */

@JsonObject
public class History extends BaseModel {

    @JsonField(name = "order")
    public Order order;

    @JsonField(name = "barber")
    public User barber;

    @JsonField(name = "barbershop")
    public User barbershop;

    @JsonField(name = "customer")
    public User customer;
}
