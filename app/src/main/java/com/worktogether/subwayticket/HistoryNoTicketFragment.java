package com.worktogether.subwayticket;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.worktogether.subwayticket.bean.OrderHistory;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static cn.bmob.v3.Bmob.getApplicationContext;
import static com.worktogether.subwayticket.OrderHistoryActivity.historyNoTicketList;


public class HistoryNoTicketFragment extends Fragment {

//    private List<OrderHistory> historyNoTicketList = new ArrayList<OrderHistory>();
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //把布局加载进来了，通过返回的view就可以查到这个布局里面的id
        //这里一般不会处理问题，只是用来加载view
        view = inflater.inflate(R.layout.activity_history_noticket, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        historyNoTicketList.clear();

//        // 初始化"未取票"碎片ListView
//        initHistoryNoTicketsListView();

//        // 从Bmob中查询并初始化"未取票"碎片ListView
//        queryHistoryNoTicket();
        setAdapterUntaken();
    }

public void setAdapterUntaken(){
    HistoryNoTicketFragmentAdapter adapter = new HistoryNoTicketFragmentAdapter(getActivity(), R.layout.activity_noticket_item, historyNoTicketList);
//        Log.d("adapter", String.valueOf(adapter));
    ListView listView = (ListView) view.findViewById(R.id.history_notickets_listview);
//        Log.d("listView", String.valueOf(listView));
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            OrderHistory orderHistory = historyNoTicketList.get(position);
            Bundle mBundle = new Bundle();
            mBundle.putString("departStation", orderHistory.getDepart_station_name());
            mBundle.putString("arriveStation", orderHistory.getArrive_station_name());
            mBundle.putDouble("totalMoney", orderHistory.getTicket_price() * orderHistory.getTicket_count());
            mBundle.putInt("ticketCount", orderHistory.getTicket_count());
            //exp: 2016-12-21 20:51:18
//                mBundle.putString("orderCreatedTime", orderHistory.getCreatedAt());
            mBundle.putString("orderCreatedTime", "2016-12-21 20:51:18");
            mBundle.putString("orderID", orderHistory.getObjectId());

            Intent intent = new Intent(getActivity(), PayDetailActivity.class);
            intent.putExtras(mBundle);
            startActivity(intent);
        }
    });
}

    public class HistoryNoTicketFragmentAdapter extends ArrayAdapter<OrderHistory> {

        private int resourceId;

        public HistoryNoTicketFragmentAdapter(Context context, int textViewResourceId, List<OrderHistory> objects) {
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
            String ticketPrice = changeTicketPrice(orderHistory.getTicket_price(), orderHistory.getTicket_count());
            // 订单生成时间
            String ticketCreatedTime = orderHistory.getCreatedAt();

            viewHolder.departToArrive.setText(departToArrive);
            viewHolder.ticketStatus.setText(ticketStatus);
            viewHolder.ticketNum.setText(ticketNum);
            viewHolder.ticketPrice.setText(ticketPrice);
            viewHolder.ticketCreatedTime.setText("2016-12-24 00:00:00");

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


}
