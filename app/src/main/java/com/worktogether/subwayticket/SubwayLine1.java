package com.worktogether.subwayticket;

import cn.bmob.v3.BmobObject;

/**
 * Created by Ankhwyf on 16/12/17.
 */

public class SubwayLine1 extends BmobObject {

    private String station_name;
    private Float distance;
    //对应数据库中的表名
    public SubwayLine1(){
        this.setTableName("subway_station_1");
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }
}
