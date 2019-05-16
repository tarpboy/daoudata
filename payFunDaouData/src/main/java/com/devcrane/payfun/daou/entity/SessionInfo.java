package com.devcrane.payfun.daou.entity;

/**
 * Created by qts1 on 9/21/16.
 */
public class SessionInfo {
    String slipNo="";
    String seedIndx = "";

    public SessionInfo() {
        seedIndx = slipNo = "";
    }

    public SessionInfo(String slipNo, String seedIndx) {
        this.slipNo = slipNo;
        this.seedIndx = seedIndx;
    }

    public String getSlipNo() {
        return slipNo;
    }

    public void setSlipNo(String slipNo) {
        this.slipNo = slipNo;
    }

    public String getSeedIndx() {
        return seedIndx;
    }

    public void setSeedIndx(String seedIndx) {
        this.seedIndx = seedIndx;
    }

    @Override
    public String toString() {
        return "SessionInfo{" +
                "slipNo='" + slipNo + '\'' +
                ", seedIndx='" + seedIndx + '\'' +
                '}';
    }
}
