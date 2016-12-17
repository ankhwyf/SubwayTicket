package com.worktogether.subwayticket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.worktogether.subwayticket.bean.OrderHistory;
import com.worktogether.subwayticket.util.Constants;
import com.worktogether.subwayticket.util.SharedPreferencesUtils;

import org.json.JSONArray;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class MainActivity extends AppCompatActivity {

    Boolean isLogin=false;
    Integer count=1;

    @BindView(R.id.pay_btn)
    Button mBtnPay;

    @BindView(R.id.select_arrived)
    TextView mTvSelectArrived;

    @BindView(R.id.select_depart)
    TextView mTvSelectDepart;

    @BindView(R.id.ticket_count)
    TextView mTvTicketCount;

    private MyTextWatcher mTextWatcher = new MyTextWatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //使BindView可用
        ButterKnife.bind(this);
        //监听始终点是否已选择
        initListener();

        //默认初始化
        Bmob.initialize(this,"9ea43370e3823c7bc07ba1f9e851088a");

    }

    @OnClick({R.id.layout_exit,R.id.select_depart,R.id.select_arrived,R.id.add_btn,R.id.sub_btn,R.id.pay_btn,R.id.order_list})
    void onClick(View v) {
        switch (v.getId()) {
            //退出登录
            case R.id.layout_exit:
                //将当前状态存储至SharedPreferences，登录状态为未登录（false）
                SharedPreferencesUtils.save(this, Constants.KEY_LOGIN_STATUS, false);
                //启动登录界面的活动
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            //购票记录
            case R.id.order_list:
                //获取SharedPreferences中的登录状态 存储至isLogin
                isLogin = (Boolean) SharedPreferencesUtils.get(this, Constants.KEY_LOGIN_STATUS, Constants.TYPE_BOOLEAN);
                //若为非登录状态，则启动登录界面的活动
                if (isLogin == false) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                //否则跳至历史订单记录界面
                else
                    startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
                break;
            //出发站
            case R.id.select_depart:


                break;
            //到达站
            case R.id.select_arrived:

                break;
            //增加票数
            case R.id.add_btn:
                if(count<6){
                    count++;
                    mTvTicketCount.setText(String.valueOf(count));
                }
                break;
            //减少票数
            case R.id.sub_btn:
                if(count!=1){
                    count--;
                    mTvTicketCount.setText(String.valueOf(count));
                }

                break;
            //支付按钮
            case R.id.pay_btn:
                //获取SharedPreferences中的登录状态 存储至isLogin
                isLogin = (Boolean) SharedPreferencesUtils.get(this, Constants.KEY_LOGIN_STATUS, Constants.TYPE_BOOLEAN);
                //若为非登录状态，则启动登录界面的活动
                if (isLogin == false) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                //否则跳至支付详情界面
                else {
                    startActivity(new Intent(MainActivity.this, PayDetailActivity.class));
                }
                break;
        }

    }

    private void initListener() {
        mTvSelectArrived.addTextChangedListener(mTextWatcher);
        mTvSelectDepart.addTextChangedListener(mTextWatcher);
    }

    private class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String arrived = mTvSelectArrived.getText().toString().trim();
            String depart = mTvSelectDepart.getText().toString().trim();
            if (TextUtils.isEmpty(arrived) || TextUtils.isEmpty(depart)) {
                mBtnPay.setEnabled(false);
            } else {
                mBtnPay.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    /**
     * 查询结果是JSONArray ，需自行解析
     * **/
    public void queryData(){
        BmobQuery query=new BmobQuery("subway_station_1");
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if(e==null){
                    Log.d("bmob","查询成功："+jsonArray.toString());
                } else {
                    Log.d("bmob","查询失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

}
