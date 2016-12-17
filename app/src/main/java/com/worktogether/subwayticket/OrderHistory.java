package com.worktogether.subwayticket;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.socketio.callback.StringCallback;

/**
 * Created by Ankhwyf on 16/12/17.
 */

public class OrderHistory extends BmobObject {
    //购买者id
    private String user_id;
    //票的状态：0表示未取票 1表示已取票 2表示已退票
    private Integer ticket_status;
    //单票价格
    private Float ticket_price;
    //票数
    private Integer ticket_count;
    //地铁几号线
    private Integer subway_line_id;
    //始发站ID
    private String depart_place_id;
    //到达站ID
    private String arrive_place_id;

    //对应数据库中的表名
    public OrderHistory(){
        this.setTableName("order_history");
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Integer getTicket_status() {
        return ticket_status;
    }

    public void setTicket_status(Integer ticket_status) {
        this.ticket_status = ticket_status;
    }

    public Float getTicket_price() {
        return ticket_price;
    }

    public void setTicket_price(Float ticket_price) {
        this.ticket_price = ticket_price;
    }

    public Integer getTicket_count() {
        return ticket_count;
    }

    public void setTicket_count(Integer ticket_count) {
        this.ticket_count = ticket_count;
    }

    public Integer getSubway_line_id() {
        return subway_line_id;
    }

    public void setSubway_line_id(Integer subway_line_id) {
        this.subway_line_id = subway_line_id;
    }

    public String getDepart_place_id() {
        return depart_place_id;
    }

    public void setDepart_place_id(String depart_place_id) {
        this.depart_place_id = depart_place_id;
    }

    public String getArrive_place_id() {
        return arrive_place_id;
    }

    public void setArrive_place_id(String arrive_place_id) {
        this.arrive_place_id = arrive_place_id;
    }
}
