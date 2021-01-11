package com.odelan.qwork.data.model;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by Administrator on 11/21/2016.
 */

@JsonObject
public class User extends BaseModel {

    @JsonField(name = "id")
    public String userid;

    @JsonField(name = "one_id")
    public String one_id;

    @JsonField(name = "email")
    public String email;

    @JsonField(name = "username")
    public String username;

    @JsonField(name = "password")
    public String password;

    @JsonField(name = "user_type")
    public String user_type;

    @JsonField(name = "fb_id")
    public String fb_id;

    @JsonField(name = "tw_id")
    public String tw_id;

    @JsonField(name = "g_id")
    public String g_id;

    @JsonField(name = "photo")
    public String photo;

    @JsonField(name = "included_shop_id")
    public String included_shop_id;

    @JsonField(name = "online_status")
    public String online_status;

    @JsonField(name = "fb_profile_link")
    public String fb_profile_link;

    @JsonField(name = "instagram_profile_link")
    public String instagram_profile_link;

    @JsonField(name = "twt_profile_link")
    public String twt_profile_link;

    @JsonField(name = "address")
    public String address;

    @JsonField(name = "Belongs_to_barbershop")
    public String belongstoTV;

    @JsonField(name = "lati")
    public String lati;

    @JsonField(name = "lang")
    public String lang;

    @JsonField(name = "is_ana")
    public String is_ana;

//    @JsonField(name = "work_time_start")
//    public String work_time_start;
//
//    @JsonField(name = "work_time_end")
//    public String work_time_end;

    @JsonField(name = "mon_time_start")
    public String mon_time_start;

    @JsonField(name = "mon_time_end")
    public String mon_time_end;

    @JsonField(name = "tue_time_start")
    public String tue_time_start;

    @JsonField(name = "tue_time_end")
    public String tue_time_end;

    @JsonField(name = "wed_time_start")
    public String wed_time_start;

    @JsonField(name = "wed_time_end")
    public String wed_time_end;

    @JsonField(name = "thr_time_start")
    public String thr_time_start;

    @JsonField(name = "thr_time_end")
    public String thr_time_end;

    @JsonField(name = "fri_time_start")
    public String fri_time_start;

    @JsonField(name = "fri_time_end")
    public String fri_time_end;

    @JsonField(name = "sat_time_start")
    public String sat_time_start;

    @JsonField(name = "sat_time_end")
    public String sat_time_end;

    @JsonField(name = "sun_time_start")
    public String sun_time_start;

    @JsonField(name = "sun_time_end")
    public String sun_time_end;

    @JsonField(name = "close_state")
    public String close_state;

    @JsonField(name = "description")
    public String desc;

    @JsonField(name = "birthday")
    public String birthday;

    @JsonField(name = "gender")
    public String gender;

    @JsonField(name = "phone")
    public String phone;

    @JsonField(name = "barbershop_name")
    public String barbershop_name;

    @JsonField(name = "barbershop_id")
    public String barbershop_id;

    @JsonField(name = "rating")
    public String rating;

    @JsonField(name = "count5")
    public String count5;

    @JsonField(name = "count4")
    public String count4;

    @JsonField(name = "count3")
    public String count3;

    @JsonField(name = "count2")
    public String count2;

    @JsonField(name = "count1")
    public String count1;

    @JsonField(name = "cur_count")
    public String cur_count;

    @JsonField(name = "totalCustomerCount")
    public String totalCustomerCount;

    @JsonField(name = "faivorit_barber_id")
    public String faivorit_barber_id = "";

    @JsonField(name = "total_turn_time")
    public String total_turn_time = "";

    public String toJson() {
        try {
            return LoganSquare.serialize(this);
        } catch (Exception ex) {

        }
        return "";
    }
}
