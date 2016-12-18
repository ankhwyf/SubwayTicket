package com.worktogether.subwayticket;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.worktogether.subwayticket.bean.SubwayLine;
import com.worktogether.subwayticket.util.Constants;
import com.worktogether.subwayticket.util.SharedPreferencesUtils;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Boolean isLogin=false;
    private int mCount=1;
    private double money=0.0;
    //设置静态表名
    private static final String LINEONE="subway_station_1";
    private static final String LINEFOUR="subway_station_4";
    //设置静态字段名
    private static final String STATIONID="objectId";
    private static final String STATIONNAME="station_name";
    private static final String DISTANCE="distance";
    // 选择的类型
    private static final int TYPE_START= 0x01;
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
    //站点ArrayList
    private List<String> mStationListOne = new ArrayList<>();
    private List<String> mStationListFour= new ArrayList<>();
    //地铁线路ArrayList
    private List<String> mLineList = new ArrayList<>();
   // 选定的站点
    private String mSelectedStation;
    // 选定的地铁线路
    private int mDepartSelectedLine=0;
    private int mArriveSelectedLine=0;
    private int mDepartSelectedStation=0;
    private int mArriveSelectedStation=0;
    private int mCurSelectedLine=0;
    private int mCurSelectedStation=0;

    private int mSelectedType = TYPE_START;
    private List<SubwayLine> mSubwayLineOneList=new ArrayList<>() ;
    private List<SubwayLine> mSubwayLineFourList=new ArrayList<>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queryLine(LINEONE,mSubwayLineOneList,mStationListOne);
        queryLine(LINEFOUR,mSubwayLineFourList,mStationListFour);
        // 关联控件
        findViews();
        //监听事件
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
        mTvMoney=(TextView)findViewById(R.id.should_pay);
    }

    private void initListener() {
        //监听始终点是否已全部选择
        mTvSelectArrived.addTextChangedListener(mTextWatcher);
        mTvSelectDepart.addTextChangedListener(mTextWatcher);
        mLayoutExit.setOnClickListener(this);
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
     * 初始化滚轮内容
     */
    private void initWheelView(View view) {

        //ArrayList
        if (mLineList.size() == 0) {
            mLineList.add("地铁一号线");
            mLineList.add("地铁四号线");
        }
        //站点Wheel
        if (mWheelStations == null) {
            mWheelStations = (WheelView) view.findViewById(R.id.wv_stations);
            mWheelStations.setWheelAdapter(new ArrayWheelAdapter(this));
            //设置滚轮主题
            mWheelStations.setSkin(WheelView.Skin.Holo);
            mWheelStations.setWheelData(mStationListOne);
            mWheelStations.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                @Override
                public void onItemSelected(int position, Object o) {
                    mSelectedStation = (String) o;
                    mCurSelectedStation=position;
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
                    mCurSelectedLine=position;
                    if(position==0){
                        mWheelStations.setWheelData(mStationListOne);
                    }
                    else {
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
                if(mCount<6){
                    mCount++;
                    mTvTicketCount.setText(String.valueOf(mCount));
                    mTvMoney.setText("￥"+(money*mCount));
                }
                break;
            //减少票数
            case R.id.sub_btn:
                if(mCount!=1){
                    mCount--;
                    mTvTicketCount.setText(String.valueOf(mCount));
                    mTvMoney.setText("￥"+(money*mCount));
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
                mPopupWindow.dismiss();
                //出发站点
                if (mSelectedType == TYPE_START) {
                    mTvSelectDepart.setText( mSelectedStation);
                    mTvSelectDepart.setTextColor(Color.BLACK);
                    // 选定出发站点的地铁线路和站点名称
                    mDepartSelectedLine=mCurSelectedLine;
                    mDepartSelectedStation=mCurSelectedStation;
                }
                //到达站点
                else {
                    mTvSelectArrived.setText(mSelectedStation);
                    mTvSelectArrived.setTextColor(Color.BLACK);
                    // 选定到达站点的地铁线路和站点名称
                    mArriveSelectedLine=mCurSelectedLine;
                    mArriveSelectedStation=mCurSelectedStation;
                }

                break;
            case R.id.btn_cancel:
                mPopupWindow.dismiss();
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
            }
            else if(arrived.equals(depart)){
                mBtnPay.setEnabled(false);
                Toast.makeText(getApplicationContext(),"站点不能相同！",Toast.LENGTH_SHORT).show();
            }
            else {
                mBtnPay.setEnabled(true);
                money=calTicketPrice(mDepartSelectedLine,mArriveSelectedLine,mDepartSelectedStation,mArriveSelectedStation);
                mTvMoney.setText("￥"+(money*mCount));
                int color = ContextCompat.getColor(MainActivity.this, R.color.colorLightRed);
                mTvMoney.setTextColor(color);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    /**
     * 地铁1号线
     * 查询结果是JSONArray ，需自行解析
     * [{"updatedAt":"2016-12-16 11:26:04",
     *   "station_name":"湘湖",
     *   "distance":0,
     *   "objectId":"Vo8a777K",
     *   "createdAt":"2016-12-16 11:25:27"},
     * **/
    //传入表名 和 用于存储地铁具体站点信息的List
    public void queryLine(final String lineStr, final List<SubwayLine> mSubwayLineList, final List<String> mStationList){
        BmobQuery query=new BmobQuery(lineStr);
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if(e==null){
                    //遍历
                    for(int i=0;i<jsonArray.length();i++){
                        try {
                            JSONObject obj=jsonArray.getJSONObject(i);
                            String id=obj.getString(STATIONID);
                            String stationName=obj.getString(STATIONNAME);
                            Double distance=obj.getDouble(DISTANCE);

                            SubwayLine stationInfo=new  SubwayLine(lineStr);
                            stationInfo.setDistance(distance);
                            stationInfo.setStation_name(stationName);
                            mSubwayLineList.add(stationInfo);
                            mStationList.add(stationName);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    Log.d("bmob","查询失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    //计算票价
    public double calTicketPrice(int departLine,int arriveLine,int departStation,int arriveStation){
        double price=0.0,distance=0.0;
        //情况一: 选择同一条线路
        if(departLine==arriveLine){
            Log.d("bmob","departStation："+mDepartSelectedStation);
            Log.d("bmob","arriveStation："+mArriveSelectedStation);
            //交换起点和终点
            if(arriveStation<departStation){
                swap(departStation,arriveStation);
            }
            //若两个站点为这三个站点中（近江，火车东站，彭埠）的任意两个的情况
            //则选取四号线的票价
            //一号线的两个站点
            if(arriveLine==0){
                String departName=mSubwayLineOneList.get(departStation).getStation_name();
                String arriveName=mSubwayLineOneList.get(arriveStation).getStation_name();
                if(departName.equals("近江")||departName.equals("火车东站")){
                    if(arriveName.equals("火车东站")||arriveName.equals("彭埠")){
                        arriveLine=1;
                    }
                }
            }
            switch (arriveLine){
                //地铁一号线
                case 0:
                    for(int i=arriveStation;i>departStation;i--) {
                        distance += mSubwayLineOneList.get(i).getDistance();
                        Log.d("bmob","i："+i+"，" + "distance："+distance);
                    }
                    break;
               //地铁四号线
                case 1:
                    for(int i=arriveStation;i>=departStation;i--) {
                        distance+=mSubwayLineFourList.get(i).getDistance();
                    }
                    break;
            }
        }
        //情况二: 选择不同线路的站点
        else {
            //若两个站点为这三个站点中（近江，火车东站，彭埠）的任意两个的情况
            //则选取四号线的票价
            String departName=mTvSelectDepart.getText().toString();
            String arriveName= mTvSelectArrived.getText().toString();

            /*
            * 站点之间跨度大：包含近江和火车东站
            * **/
        }
        price=calculatePrice(distance);
        return price;
    }

    public void swap(Integer a,Integer b){
        Integer temp;
        temp=a;
        a=b;
        b=temp;
    }

    public double calculatePrice(double distance){
        int temp=(int)(distance/ 1000 + 0.5);
        double price=0.0;
        if(temp<=4) price=2.0;
        else if(temp<=8) price=3.0;
        else if(temp<=12) price=4.0;
        else if(temp<=18) price=5.0;
        else if(temp<=24) price=6.0;
        else if(temp<=32) price=7.0;
        else if(temp<=40) price=8.0;
        return price;
    }
}
