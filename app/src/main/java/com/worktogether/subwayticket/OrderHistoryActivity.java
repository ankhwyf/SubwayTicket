package com.worktogether.subwayticket;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worktogether.subwayticket.bean.OrderHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;


public class OrderHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    // "未取票"Tab
    private TextView historyNoTicket;
    // "全部"Tab
    private TextView historyAll;
    // "未取票"碎片
    private HistoryNoTicketFragment noTicketFragment;
    // "全部"碎片
    private HistoryAllFragment allFragment;
    // "返回"按钮
    private LinearLayout historyTitleBack;

    public static List<OrderHistory> historyAllTicketList = new ArrayList<OrderHistory>();
    public static List<OrderHistory> historyNoTicketList = new ArrayList<OrderHistory>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // "未取票"Tab/"全部"Tab/"返回"按钮
        historyNoTicket = (TextView) findViewById(R.id.history_noticket);
        historyAll = (TextView) findViewById(R.id.history_all);
        historyTitleBack = (LinearLayout) findViewById(R.id.history_title_back);

        // 默认显示"未取票"碎片
        noTicketFragment = new HistoryNoTicketFragment();
        FragmentManager noTicketFragmentManager = getFragmentManager();
        FragmentTransaction noTicketTransaction = noTicketFragmentManager.beginTransaction();
        noTicketTransaction.add(R.id.ticketFrameLayout, noTicketFragment);
        noTicketTransaction.commit();

        // "未取票"Tab的事件监听
        historyNoTicket.setOnClickListener(this);
        // "全部"Tab的事件监听
        historyAll.setOnClickListener(this);
        // "返回"按钮
        historyTitleBack.setOnClickListener(this);

        // 从Bmob中查询并初始化historyAllTicketList/historyNoTicketList
        queryHistoryAll();

        // 调用"全部"碎片的自定义适配器
        HistoryNoTicketFragment.setNoTicketHistoryAdapter();
    }

    public void onClick(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (view.getId()) {
            // "未取票"Tab
            case R.id.history_noticket:
                if (noTicketFragment == null) {
                    noTicketFragment = new HistoryNoTicketFragment();
                }
                fragmentTransaction.replace(R.id.ticketFrameLayout, noTicketFragment);
                break;
            // "全部"Tab
            case R.id.history_all:
                if (allFragment == null) {
                    allFragment = new HistoryAllFragment();
                }
                fragmentTransaction.replace(R.id.ticketFrameLayout, allFragment);
                break;
            // "返回"按钮
            case R.id.history_title_back:
                startActivity(new Intent(OrderHistoryActivity.this, MainActivity.class));
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
    }


    // 查询当前登录用户的所有历史记录
    public void queryHistoryAll() {

        historyAllTicketList.clear();
        historyNoTicketList.clear();

        BmobQuery allQuery = new BmobQuery("order_history");

        // 查询user_phone为当前用户的手机号的数据
        // 当前用户的手机号
        BmobUser mCurUser = BmobUser.getCurrentUser();
        String user_phone = mCurUser.getMobilePhoneNumber();

        allQuery.addWhereEqualTo("user_phone", user_phone);
        allQuery.setLimit(60);

        // 执行查询方法
        allQuery.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Log.i("jsonArray11111111111", String.valueOf(jsonArray.length()));
                        try {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Integer ticket_status = obj.getInt("ticket_status");
                            Double ticket_price = obj.getDouble("ticket_price");
                            Integer ticket_count = obj.getInt("ticket_count");
                            String depart_station_name = obj.getString("depart_station_name");
                            String arrive_station_name = obj.getString("arrive_station_name");

                            OrderHistory order = new OrderHistory();
                            order.setDepart_station_name(depart_station_name);
                            order.setArrive_station_name(arrive_station_name);
                            order.setTicket_status(ticket_status);
                            order.setTicket_price(ticket_price);
                            order.setTicket_count(ticket_count);

                            historyAllTicketList.add(order);

                            if (ticket_status == 0) {
                                historyNoTicketList.add(order);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                } else {
                    Log.i("bmob", "失败：from all " + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

    }

}
