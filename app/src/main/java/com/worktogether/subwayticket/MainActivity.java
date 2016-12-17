package com.worktogether.subwayticket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.worktogether.subwayticket.bean.OrderHistory;
import com.worktogether.subwayticket.util.Constants;
import com.worktogether.subwayticket.util.SharedPreferencesUtils;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import org.json.JSONArray;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Boolean isLogin=false;
    Integer count=1;

    private Button mBtnPay;
    private TextView mTvSelectArrived;
    private TextView mTvSelectDepart;
    private TextView mTvTicketCount;
    private LinearLayout mLayoutExit;
    private TextView mTvOrderList;
    private TextView mTvAdd;
    private TextView mTvSub;

    private WheelView mWheelView;
    private Button mBtnConfirm;
    private Button mBtnCancel;

    private MyTextWatcher mTextWatcher = new MyTextWatcher();

    private PopupWindow mPopupWindow = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 关联控件
        findViews();
        //监听始终点是否已选择
        initListener();

    }

    private void findViews() {
        mBtnPay = (Button) findViewById(R.id.pay_btn);
        mTvSelectArrived = (TextView) findViewById(R.id.select_arrived);
        mTvSelectDepart = (TextView) findViewById(R.id.select_depart);
        mTvTicketCount = (TextView) findViewById(R.id.ticket_count);
        mLayoutExit = (LinearLayout) findViewById(R.id.layout_exit);
        mTvOrderList = (TextView) findViewById(R.id.order_list);
        mTvAdd = (TextView) findViewById(R.id.add_btn);
        mTvSub = (TextView) findViewById(R.id.sub_btn);
    }

    private void initListener() {
        mTvSelectArrived.addTextChangedListener(mTextWatcher);
        mTvSelectDepart.addTextChangedListener(mTextWatcher);
        mLayoutExit.setOnClickListener(this);
        mTvOrderList.setOnClickListener(this);
        mTvAdd.setOnClickListener(this);
        mTvSub.setOnClickListener(this);
    }

    /**
     * 显示选择站点的popup windows
     */
    private void showPopupWindow() {
        if (mPopupWindow == null) {
            View popupView = LayoutInflater.from(this).inflate(R.layout.popup_select_stations_layout, null);
            PopupWindow window = new PopupWindow(popupView,
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setTouchable(true);
            // 点击空白区域，窗口消失
            mPopupWindow.setOutsideTouchable(true);

            mWheelView = (WheelView) popupView.findViewById(R.id.wv_stations);
            mBtnConfirm = (Button) popupView.findViewById(R.id.btn_confirm);
            mBtnCancel = (Button) popupView.findViewById(R.id.btn_cancel);

            // 初始化WheelView
            initWheelView();

            mBtnConfirm.setOnClickListener(this);
            mBtnCancel.setOnClickListener(this);
//            mWheelView.set
        }

    }

    private void initWheelView() {

        if (mWheelView != null) {
        }
    }

    @Override
    public void onClick(View v) {
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
            case R.id.btn_confirm:

                break;
            case R.id.btn_cancel:

                break;
        }
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
