package com.wbct.etab.bean;

/**
 * Created by Siddhartha Maji on 12/31/2015.
 */
public class FilterRangeItem {

    public String getText_range() {
        return text_range;
    }

    public void setText_range(String text_range) {
        this.text_range = text_range;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    private String text_range;
    private Boolean checked;
}
