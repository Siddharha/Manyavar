package com.wbct.etab.bean;

/**
 * Created by Siddhartha Maji on 1/7/2016.
 */
public class AccessoriItem {
    private String acrProductCode;
    private String acrPrice;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    private String imgUrl;

    public String getAcrProductCode() {
        return acrProductCode;
    }

    public void setAcrProductCode(String acrProductCode) {
        this.acrProductCode = acrProductCode;
    }

    public String getAcrPrice() {
        return acrPrice;
    }

    public void setAcrPrice(String acrPrice) {
        this.acrPrice = acrPrice;
    }
}
