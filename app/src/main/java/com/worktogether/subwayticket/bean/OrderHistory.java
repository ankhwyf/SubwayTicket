package com.worktogether.subwayticket.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.socketio.callback.StringCallback;

/**
 * Created by Ankhwyf on 16/12/17.
 */

public class OrderHistory extends BmobObject {
    //购买者电话号码
    private String user_phone;
    //票的状态：0表示未取票 1表示已取票 2表示已退票
    private Integer ticket_status;
    //总价
    private Double ticket_price;
    //票数
    private Integer ticket_count;
    //始发站名
    private String depart_station_name;
    //到达站名
    private String arrive_station_name;
    //订单ID
    private String id;
    //创建时间
    private String createAt;


    //对应数据库中的表名
    public OrderHistory(){
        this.setTableName("order_history");
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public Integer getTicket_status() {
        return ticket_status;
    }

    public void setTicket_status(Integer ticket_status) {
        this.ticket_status = ticket_status;
    }

    public Double getTicket_price() {
        return ticket_price;
    }

    public void setTicket_price(Double ticket_price) {
        this.ticket_price = ticket_price;
    }

    public Integer getTicket_count() {
        return ticket_count;
    }

    public void setTicket_count(Integer ticket_count) {
        this.ticket_count = ticket_count;
    }

    public String getDepart_station_name() {
        return depart_station_name;
    }

    public void setDepart_station_name(String depart_station_name) {
        this.depart_station_name = depart_station_name;
    }

    public String getArrive_station_name() {
        return arrive_station_name;
    }

    public void setArrive_station_name(String arrive_station_name) {
        this.arrive_station_name = arrive_station_name;
    }
}
