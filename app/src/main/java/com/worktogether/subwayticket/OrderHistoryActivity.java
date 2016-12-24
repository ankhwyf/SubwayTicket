package com.worktogether.subwayticket;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
    private TextView historyTitleBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // "未取票"Tab/"全部"Tab/"返回"按钮
        historyNoTicket = (TextView) findViewById(R.id.history_noticket);
        historyAll = (TextView) findViewById(R.id.history_all);
        historyTitleBack = (TextView) findViewById(R.id.history_title_back);

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
}
