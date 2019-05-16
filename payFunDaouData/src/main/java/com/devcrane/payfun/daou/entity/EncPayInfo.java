package com.devcrane.payfun.daou.entity;

/**
 * Created by Administrator on 8/31/2016.
 */
public class EncPayInfo {
    String passWord="";
    String emvData="";
    String signData="";

    public EncPayInfo(String passWord, String emvData, String signData) {
        this.passWord = passWord;
        this.emvData = emvData;
        this.signData = signData;
    }
    public EncPayInfo(){

    }
    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getEmvData() {
        return emvData;
    }

    public void setEmvData(String emvData) {
        this.emvData = emvData;
    }

    public String getSignData() {
        return signData;
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }

    @Override
    public String toString() {
        return "EncPayInfo{" +
                "passWord='" + passWord + '\'' +
                ", emvData='" + emvData + '\'' +
                ", signData='" + signData + '\'' +
                '}';
    }
}
