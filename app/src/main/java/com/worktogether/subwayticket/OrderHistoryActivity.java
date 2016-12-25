package com.worktogether.subwayticket;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private BmobUser mCurUser;
    private final static String HISTORY = "order_history";
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

        mCurUser = BmobUser.getCurrentUser();

        queryHistoryAll();

        findViews();

        initListener();

    }

    private void showNoTicketFragment(){
        // 默认显示"未取票"碎片
        noTicketFragment = new HistoryNoTicketFragment();
        FragmentManager noTicketFragmentManager = getFragmentManager();
        FragmentTransaction noTicketTransaction = noTicketFragmentManager.beginTransaction();
        noTicketTransaction.add(R.id.ticketFrameLayout, noTicketFragment);
        noTicketTransaction.commit();
    }
    private void findViews() {
// "未取票"Tab/"全部"Tab/"返回"按钮
        historyNoTicket = (TextView) findViewById(R.id.history_noticket);
        historyAll = (TextView) findViewById(R.id.history_all);
        historyTitleBack = (LinearLayout) findViewById(R.id.history_title_back);
    }

    private void initListener() {

        // "未取票"Tab的事件监听
        historyNoTicket.setOnClickListener(this);
        // "全部"Tab的事件监听
        historyAll.setOnClickListener(this);
        // "返回"按钮
        historyTitleBack.setOnClickListener(this);
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
    private void queryHistoryAll() {

        BmobQuery allQuery = new BmobQuery(HISTORY);
        // 查询user_phone为当前用户的手机号的数据
        // 当前用户的手机号

        String user_phone = mCurUser.getMobilePhoneNumber();
//        Log.i("bmob from all ", user_phone);

        allQuery.addWhereEqualTo("user_phone", user_phone);
        allQuery.setLimit(60);
        //Log.i("bmob from all ", String.valueOf(allQuery));
        // 执行查询方法
        allQuery.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String depart_station_name = obj.getString("depart_station_name");
                            String arrive_station_name = obj.getString("arrive_station_name");
                            int ticket_status = obj.getInt("ticket_status");
                            double ticket_price = obj.getDouble("ticket_price");
                            int ticket_count = obj.getInt("ticket_count");
                            String objectId=obj.getString("objectId");
                            String createdAt=obj.getString("createdAt");

                            OrderHistory orderHistory = new OrderHistory();
                            orderHistory.setDepart_station_name(depart_station_name);
                            orderHistory.setArrive_station_name(arrive_station_name);
                            orderHistory.setTicket_status(ticket_status);
                            orderHistory.setTicket_price(ticket_price);
                            orderHistory.setTicket_count(ticket_count);
                            orderHistory.setObjectId(objectId);
                            orderHistory.setCreatedAt(createdAt);

                            historyAllTicketList.add(orderHistory);

                            if (ticket_status == 0) {
                                historyNoTicketList.add(orderHistory);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    showNoTicketFragment();
                } else {
                    Log.i("bmob", "失败：from all " + e.toString());
                }
            }
        });
    }

}
