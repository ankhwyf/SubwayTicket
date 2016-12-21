package com.worktogether.subwayticket.bean;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import cn.bmob.v3.BmobObject;

/**
 * Created by Ankhwyf on 16/12/17.
 */

public class SubwayLine extends BmobObject {

    private String station_name;
    private Double distance;
    //对应数据库中的表名
    public SubwayLine(String tableName){
        this.setTableName(tableName);
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
