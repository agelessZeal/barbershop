package com.odelan.qwork.data.model;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by LuckyMan on 2/3/2018.
 */

@JsonObject
public class ReviewNotify extends BaseModel{

    @JsonField(name = "noti_username")
    public String userName = "";

    @JsonField(name = "noti_photo")
    public String photo = "";

    @JsonField(name = "noti_orderid")
    public String orderId = "";

    public String toJson() {
        try {
            return LoganSquare.serialize(this);
        } catch (Exception ex) {

        }
        return "";
    }
}
