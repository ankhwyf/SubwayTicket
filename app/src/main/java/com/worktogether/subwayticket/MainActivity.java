package com.worktogether.subwayticket;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.worktogether.subwayticket.bean.OrderHistory;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static com.worktogether.subwayticket.MyApplication.getIndexOfStation;
import static com.worktogether.subwayticket.MyApplication.mLineList;
import static com.worktogether.subwayticket.MyApplication.mStationDistance;
import static com.worktogether.subwayticket.MyApplication.mStationListFour;
import static com.worktogether.subwayticket.MyApplication.mStationListOne;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //当前用户
    private BmobUser mCurUser;
    //票的张数
    private int mCount = 1;
    //票的单价
    public double money = 0.0;

    // 选择的类型
    private static final int TYPE_START = 0x01;
    private static final int TYPE_END = 0x02;

    //支付按钮
    private Button mBtnPay;
    //选择出发站点
    private TextView mTvSelectArrived;
    //选择到达站点
    private TextView mTvSelectDepart;
    //票的张数
    private TextView mTvTicketCount;
    //退出登录
    private LinearLayout mLayoutExit;
    //购票记录
    private TextView mTvOrderList;
    //票数加加++
    private TextView mTvAdd;
    //票数减减--
    private TextView mTvSub;
    private TextView mTvMoney;
    //站点滚轮
    private WheelView mWheelStations;
    //地铁线路滚轮
    private WheelView mWheelLines;
    //popupWindow 弹出框确定按钮
    private Button mBtnConfirm;
    //popupWindow 弹出框取消按钮
    private Button mBtnCancel;
    //监听起点终点是否已选择
    private MyTextWatcher mTextWatcher = new MyTextWatcher();
    //popupWindow 弹出框
    private PopupWindow mPopupWindow = null;
    private PopupWindow mPopupConfirmPayWindow = null;

    // 选定的站点
    private String mSelectedStation;
    private String mDepartName;
    private String mArriveName;
    // 选定的地铁线路
    private int mCurSelectedLine = 0;
    private int mCurSelectedStation = 0;

    private int mSelectedType = TYPE_START;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCurUser = BmobUser.getCurrentUser();
        // 关联控件
        findViews();
        //监听事件
        initListener();
    }

    @Override
    protected void onResume(){
        super.onResume();
        //获取当前用户
        mCurUser = BmobUser.getCurrentUser();
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
        mTvMoney = (TextView) findViewById(R.id.should_pay);
    }

    private void initListener() {
        //监听始终点是否已全部选择
        mTvSelectArrived.addTextChangedListener(mTextWatcher);
        mTvSelectDepart.addTextChangedListener(mTextWatcher);
        mLayoutExit.setOnClickListener(this);
        mBtnPay.setOnClickListener(this);
        mTvOrderList.setOnClickListener(this);
        mTvAdd.setOnClickListener(this);
        mTvSub.setOnClickListener(this);
        mTvSelectArrived.setOnClickListener(this);
        mTvSelectDepart.setOnClickListener(this);
    }

    /**
     * 显示选择站点的popup windows
     */
    private void showPopupWindow() {
        //若为空，则将mPopupWindow实例化
        if (mPopupWindow == null) {
            //显示popup_select_stations_layout布局
            View popupView = LayoutInflater.from(this).inflate(R.layout.popup_select_stations_layout, null);
            mPopupWindow = new PopupWindow(popupView,
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setTouchable(true);
            // 点击空白区域，窗口消失
            mPopupWindow.setOutsideTouchable(true);

            mBtnConfirm = (Button) popupView.findViewById(R.id.btn_confirm);
            mBtnCancel = (Button) popupView.findViewById(R.id.btn_cancel);
            // 初始化WheelView
            initWheelView(popupView);

            mBtnConfirm.setOnClickListener(this);
            mBtnCancel.setOnClickListener(this);
        }

        mPopupWindow.showAtLocation(MainActivity.this.findViewById(R.id.activity_main), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 显示支付详情界面popupWindow
     */
    private void showPopupConfirmPayWindow() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_confirm_pay_detail, null);
        mPopupConfirmPayWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        mPopupConfirmPayWindow.setTouchable(true);
        // 点击空白区域，窗口消失
        mPopupConfirmPayWindow.setOutsideTouchable(true);

        //findById
        Button mBtnConfirmPay = (Button) popupView.findViewById(R.id.confirm_pay_btn);
        TextView mTvConfirmCount = (TextView) popupView.findViewById(R.id.confirm_ticket_count);
        TextView mTvConfirmMoneyAmount = (TextView) popupView.findViewById(R.id.confirm_money_amount);
        TextView mTvConfirmPhone = (TextView) popupView.findViewById(R.id.confirm_phone_number);
        ImageView mIvCancelPay = (ImageView) popupView.findViewById(R.id.cancel_pay);

        //隐藏手机号中间4位
       String mUserPhone=mCurUser.getMobilePhoneNumber();
        mUserPhone=mUserPhone.substring(0,3)+"****"+mUserPhone.substring(7,11);

        //监听事件
        mBtnConfirmPay.setOnClickListener(this);
        mIvCancelPay.setOnClickListener(this);

        //显示相关确认信息
        mTvConfirmCount.setText("杭州地铁单程票" + mCount + " 张");
        mTvConfirmMoneyAmount.setText(money * mCount + "元");
        mTvConfirmPhone.setText(mUserPhone);

        //mPopupConfirmPayWindow显示的位置
        mPopupConfirmPayWindow.showAtLocation(MainActivity.this.findViewById(R.id.activity_main), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 初始化滚轮内容
     */
    private void initWheelView(View view) {
//        //站点Wheel
        if (mWheelStations == null) {
            mWheelStations = (WheelView) view.findViewById(R.id.wv_stations);
            mWheelStations.setWheelAdapter(new ArrayWheelAdapter(this));
            //设置滚轮主题
            mWheelStations.setSkin(WheelView.Skin.Holo);
            //载入一号线地铁站点数据ArrayList
            mWheelStations.setWheelData(mStationListOne);

            mWheelStations.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                @Override
                public void onItemSelected(int position, Object o) {
                    mSelectedStation = (String) o;
                }
            });
        }

        //地铁线路Wheel
        if (mWheelLines == null) {
            mWheelLines = (WheelView) view.findViewById(R.id.wv_lines);
            mWheelLines.setWheelAdapter(new ArrayWheelAdapter(this));
            //设置滚轮主题
            mWheelLines.setSkin(WheelView.Skin.Holo);
            //载入地铁线路数据ArrayList
            mWheelLines.setWheelData(mLineList);

            mWheelLines.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                @Override
                public void onItemSelected(int position, Object o) {
                    mCurSelectedLine = position;

                    int stationSelection = mWheelStations.getCurrentPosition();
                    //为了解决 选择地铁4号线第一个站点显示"湘湖"的问题
                    //选择的是一号线
                    if (position == 0) {
                        //若当前所选择的站点数大于一号线的站点数，则将当前的站点数赋值为一号线最后一个站
                        if (stationSelection > mStationListOne.size() - 1) {
                            stationSelection = mStationListOne.size() - 1;
                        }
                        mSelectedStation = mStationListOne.get(stationSelection);
                        mWheelStations.setWheelData(mStationListOne);
                    }
                    //选择的是四号线
                    else {
                        //若当前所选择的站点数大于四号线的站点数，则将当前的站点数赋值为四号线最后一个站
                        if (stationSelection > mStationListFour.size() - 1) {
                            stationSelection = mStationListFour.size() - 1;
                        }
                        mSelectedStation = mStationListFour.get(stationSelection);
                        mWheelStations.setWheelData(mStationListFour);
                    }
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //退出登录
            case R.id.layout_exit:
                //若为登录状态，则跳出弹出框询问是否真的要退出，以免用户重复登录
                if(mCurUser!=null){
                    AlertDialog.Builder publicDialog = new AlertDialog.Builder(MainActivity.this);
                    publicDialog.setTitle("提示");
                    publicDialog.setIcon(R.drawable.reminder);
                    publicDialog.setMessage("确定"+mCurUser.getMobilePhoneNumber()+"退出登录？");
                    publicDialog.setCancelable(false);
                    publicDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //清除缓存用户对象
                            BmobUser.logOut();
                            //启动登录界面的活动
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    });
                    publicDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    publicDialog.show();
                } else {
                    //直接跳转至登录界面
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                break;
            //购票记录
            case R.id.order_list:
//                //若为非登录状态，则启动登录界面的活动
                if (mCurUser==null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    //否则跳至历史订单记录界面
                    startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
                }
                break;
            //出发站
            case R.id.select_depart:
                mSelectedType = TYPE_START;
                showPopupWindow();
                break;
            //到达站
            case R.id.select_arrived:
                mSelectedType = TYPE_END;
                showPopupWindow();
                break;
            //增加票数
            case R.id.add_btn:
                if (mCount < 6) {
                    mCount++;
                    mTvTicketCount.setText(String.valueOf(mCount));
                    mTvMoney.setText("￥" + (money * mCount));
                }
                break;
            //减少票数
            case R.id.sub_btn:
                if (mCount != 1) {
                    mCount--;
                    mTvTicketCount.setText(String.valueOf(mCount));
                    mTvMoney.setText("￥" + (money * mCount));
                }
                break;
            //支付按钮
            case R.id.pay_btn:
                //若为非登录状态，则启动登录界面的活动
                if (mCurUser==null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                //否则跳至支付详情界面
                else {
                    showPopupConfirmPayWindow();

                }
                break;
            case R.id.btn_confirm:
                mPopupWindow.dismiss();
                //出发站点
                if (mSelectedType == TYPE_START) {
                    // 选定出发站点的站点名称
                    mDepartName = mSelectedStation;
                    mTvSelectDepart.setText(mSelectedStation);
                    mTvSelectDepart.setTextColor(Color.BLACK);

                }
                //到达站点
                else {
                    // 选定到达站点的站点名称
                    mArriveName = mSelectedStation;
                    mTvSelectArrived.setText(mSelectedStation);
                    mTvSelectArrived.setTextColor(Color.BLACK);
                }
                break;
            case R.id.btn_cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.confirm_pay_btn:
                //出发站 到达站 总金额 张数 创建日期 订单ID 票的状态
                final OrderHistory mCurOrder = new OrderHistory();
                mCurOrder.setUser_phone(mCurUser.getMobilePhoneNumber());
                mCurOrder.setDepart_station_name(mDepartName);
                mCurOrder.setArrive_station_name(mArriveName);
                mCurOrder.setTicket_price(money * mCount);
                mCurOrder.setTicket_count(mCount);
                mCurOrder.setTicket_status(0);//0 表示未取票
                mCurOrder.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
//                            Toast.makeText(getApplicationContext(),"创建成功！"+s,Toast.LENGTH_SHORT).show();
                            Bundle mBundle = new Bundle();
                            mBundle.putString("departStation", mDepartName);
                            mBundle.putString("arriveStation", mArriveName);
                            mBundle.putDouble("totalMoney", money * mCount);
                            mBundle.putInt("ticketCount", mCount);
                            //exp: 2016-12-21 20:51:18
                            mBundle.putString("orderCreatedTime", mCurOrder.getCreatedAt());
                            mBundle.putString("orderID",mCurOrder.getObjectId());
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, PayDetailActivity.class);
                            intent.putExtras(mBundle);
                            mPopupConfirmPayWindow.dismiss();
                            startActivity(intent);
                        } else {
                            Log.d("bmob", "失败" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
                break;
            case R.id.cancel_pay:
                mPopupConfirmPayWindow.dismiss();
                break;
        }
    }

    /*
   * 用于判断两个站点是否选择完毕
   * 若是，则"立即支付"按钮可点击
   * 否则，mBtnPay.setEnabled(false);
   * */
    private class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String arrived = mTvSelectArrived.getText().toString().trim();
            String depart = mTvSelectDepart.getText().toString().trim();
            //判断是否已选择
            if (TextUtils.isEmpty(arrived) || TextUtils.isEmpty(depart)) {
                mBtnPay.setEnabled(false);
            }
            //判断选择的站点是否相同
            else if (arrived.equals(depart)) {
                mBtnPay.setEnabled(false);
                Toast.makeText(getApplicationContext(), "站点不能相同！", Toast.LENGTH_SHORT).show();
            }
            //正确选择
            //根据所选的站点，获取距离，计算票价，并显示
            else {
                mBtnPay.setEnabled(true);

                mDepartName = mTvSelectDepart.getText().toString();
                mArriveName = mTvSelectArrived.getText().toString();
                int i = getIndexOfStation(mDepartName);
                int j = getIndexOfStation(mArriveName);
                money = calculatePrice(mStationDistance.get(i).get(j));
                mTvMoney.setText("￥" + (money * mCount));

                int color = ContextCompat.getColor(MainActivity.this, R.color.colorLightRed);
                mTvMoney.setTextColor(color);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    /*
   *  输入距离（m）
   *  根据杭州地铁票价计算规则计算票价
   *  http://www.19lou.com/forum-1601-thread-14901351213759101-1-1.html
   *  杭州市物价局向社会公布：杭州地铁1号线票价2元起步
   *  2元可乘4公里，
   *  4-12公里，每加一元乘4公里；
   *  4到12公里每1元可乘4公里；
   * 12到24公里每1元可乘6公里；
   * 24公里以上每1元可乘8公里。
   * */
    public double calculatePrice(double distance) {
        //将距离转换成km为单位的数字
        double temp = distance / 1000;
        double price = 0.0;
        if (temp <= 4) price = 2.0;
        else if (temp <= 8) price = 3.0;
        else if (temp <= 12) price = 4.0;
        else if (temp <= 18) price = 5.0;
        else if (temp <= 24) price = 6.0;
        else if (temp <= 32) price = 7.0;
        else if (temp <= 40) price = 8.0;
        return price;
    }

}
