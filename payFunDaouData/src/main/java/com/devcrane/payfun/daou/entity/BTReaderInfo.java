package com.devcrane.payfun.daou.entity;

/**
 * Created by Administrator on 10/6/2016.
 */
public class BTReaderInfo {
    String name ="";
    String address="";

    public BTReaderInfo(String name, String address) {
        this.name = name;
        this.address = address;
    }
    public BTReaderInfo(){
        name = address="";
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "BTReaderInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
