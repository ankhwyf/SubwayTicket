package com.worktogether.subwayticket;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.worktogether.subwayticket.bean.OrderHistory;

import java.util.ArrayList;
import java.util.List;



public class HistoryNoTicketFragment extends Fragment {

    private List<OrderHistory> historyNoTicketList = new ArrayList<OrderHistory>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_history_noticket,container,false);

        // 初始化"未取票"碎片ListView
        initHistoryNoTicketsListView();

        OrderHistoryAdapter adapter = new OrderHistoryAdapter(getActivity(),R.layout.activity_noticket_item,historyNoTicketList);
        ListView listView = (ListView) getActivity().findViewById(R.id.history_notickets_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderHistory orderHistory = historyNoTicketList.get(position);
                Bundle mBundle=new Bundle();
                mBundle.putString("departStation",orderHistory.getDepart_station_name());
                mBundle.putString("arriveStation",orderHistory.getArrive_station_name());
                mBundle.putDouble("totalMoney",orderHistory.getTicket_price()*orderHistory.getTicket_count());
                mBundle.putInt("ticketCount",orderHistory.getTicket_count());
                //exp: 2016-12-21 20:51:18
                mBundle.putString("orderCreatedTime",orderHistory.getCreatedAt());
                mBundle.putString("orderID",orderHistory.getObjectId());

                Intent intent=new Intent(getActivity(),PayDetailActivity.class);
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });

        return view;
    }

    public void initHistoryNoTicketsListView() {
        OrderHistory order1 = new OrderHistory();
        order1.setDepart_station_name("火车东站");
        order1.setArrive_station_name("打铁关");
        order1.setTicket_status(0);
        order1.setTicket_count(1);
        order1.setTicket_price(2.00);
        historyNoTicketList.add(order1);

        OrderHistory order2 = new OrderHistory();
        order2.setDepart_station_name("江陵路");
        order2.setArrive_station_name("西湖文化广场");
        order2.setTicket_status(1);
        order2.setTicket_count(2);
        order2.setTicket_price(8.00);
        historyNoTicketList.add(order2);

        OrderHistory order3 = new OrderHistory();
        order3.setDepart_station_name("景芳");
        order3.setArrive_station_name("龙翔桥");
        order3.setTicket_status(2);
        order3.setTicket_count(3);
        order3.setTicket_price(12.00);
        historyNoTicketList.add(order3);
    }





}
