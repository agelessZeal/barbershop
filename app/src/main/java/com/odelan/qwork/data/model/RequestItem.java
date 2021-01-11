package com.odelan.qwork.data.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by MJC_COM on 2018-01-12.
 */

@JsonObject
public class RequestItem extends BaseModel {
    @JsonField(name = "requestID")
    public String requestID;

    @JsonField(name = "state")
    public String state;

    @JsonField(name = "userName")
    public String userName;

    @JsonField(name = "userPhoto")
    public String userPhoto;
}

