package com.worktogether.subwayticket;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        historyNoTicket = (TextView)findViewById(R.id.history_noticket);
        historyAll = (TextView)findViewById(R.id.history_all);

        // 默认显示"未取票"碎片
        noTicketFragment = new HistoryNoTicketFragment();
        FragmentManager noTicketFragmentManager = getFragmentManager();
        FragmentTransaction  noTicketTransaction = noTicketFragmentManager.beginTransaction();
        noTicketTransaction.add(R.id.ticketFrameLayout,noTicketFragment);
        noTicketTransaction.commit();

        historyNoTicket.setOnClickListener(this);
        historyAll.setOnClickListener(this);
    }

    public void onClick(View view){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch(view.getId()){
            case R.id.history_noticket:
                if(noTicketFragment == null){
                    noTicketFragment = new HistoryNoTicketFragment();
                }
                fragmentTransaction.replace(R.id.ticketFrameLayout,noTicketFragment);
                break;
            case R.id.history_all:
                if(allFragment == null){
                    allFragment = new HistoryAllFragment();
                }
                fragmentTransaction.replace(R.id.ticketFrameLayout,allFragment);
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
    }
}
