package com.odelan.qwork.widget.google_place_api.bean;

/**
 * Created by 석원 on 3/27/2015.
 */
public class AutoCompleteBean {
    String description;
    String reference;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public AutoCompleteBean(String description, String reference){
        this.description = description;
        this.reference = reference;
    }
}
