package com.odelan.qwork.data.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by Administrator on 7/12/2017.
 */

@JsonObject
public class ImageModel extends BaseModel {

    @JsonField(name = "uid")
    public String uid;

    @JsonField(name = "user_id")
    public String user_id;

    @JsonField(name = "image_url")
    public String image_url;

    @JsonField(name = "created_on")
    public String created_on;
}
