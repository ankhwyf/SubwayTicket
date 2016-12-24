package com.worktogether.subwayticket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.worktogether.subwayticket.bean.OrderHistory;

import java.util.List;

public class OrderHistoryAdapter extends ArrayAdapter<OrderHistory> {

    private int resourceId;

    public OrderHistoryAdapter(Context context, int textViewResourceId, List<OrderHistory> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        OrderHistory orderHistory = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.departToArrive = (TextView) view.findViewById(R.id.noticket_depart_to_arrive);
            viewHolder.ticketStatus = (TextView) view.findViewById(R.id.noticket_status);
            viewHolder.ticketNum = (TextView) view.findViewById(R.id.noticket_num);
            viewHolder.ticketPrice = (TextView) view.findViewById(R.id.noticket_price);
            viewHolder.ticketCreatedTime = (TextView) view.findViewById(R.id.noticket_nowtime);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        // 出发站-到达站
        String departToArrive = orderHistory.getDepart_station_name() + "-" + orderHistory.getArrive_station_name();
        // 地铁票状态
        String ticketStatus = changeTicketStatus(orderHistory.getTicket_status());
        // 地铁票张数
        String ticketNum = changeTicketNum(orderHistory.getTicket_count());
        // 订单总金额
        String ticketPrice = changeTicketPrice(orderHistory.getTicket_price(),orderHistory.getTicket_count());
        // 订单生成时间
        String ticketCreatedTime = orderHistory.getCreatedAt();

        viewHolder.departToArrive.setText(departToArrive);
        viewHolder.ticketStatus.setText(ticketStatus);
        viewHolder.ticketNum.setText(ticketNum);
        viewHolder.ticketPrice.setText(ticketPrice);
        viewHolder.ticketCreatedTime.setText(ticketCreatedTime);

        return view;

    }

    class ViewHolder {
        TextView departToArrive;
        TextView ticketStatus;
        TextView ticketNum;
        TextView ticketPrice;
        TextView ticketCreatedTime;
    }

    // 把地铁票状态(0/1/2)改为("未取票""已取票""已退票")
    public String changeTicketStatus(int status) {
        String ticketStatus = "";
        switch (status) {
            case 0:
                ticketStatus = "未取票";
                break;
            case 1:
                ticketStatus = "已取票";
                break;
            case 2:
                ticketStatus = "已退票";
                break;
            default:
                break;
        }
        return ticketStatus;
    }

    // 把票数从(1)变为("1张")
    public String changeTicketNum(int num) {
        String ticketNum = num + "张";
        return ticketNum;
    }

    // 把单票价格(2.00)*票数+元("4.00元")
    public String changeTicketPrice(double price, int num) {
        String ticketPrice = price * num + "元";
        return ticketPrice;
    }
}
