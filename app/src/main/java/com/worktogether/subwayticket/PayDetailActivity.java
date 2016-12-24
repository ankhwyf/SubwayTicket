package com.worktogether.subwayticket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.worktogether.subwayticket.util.QRCodeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PayDetailActivity extends AppCompatActivity implements View.OnClickListener {

    // 标题栏中的返回键
    private TextView detailsTitleBack;
    // 出发站-到达站
    private TextView departToArrive;
    // 地铁票总金额
    private TextView ticketPrice;
    // 票数
    private TextView ticketNum;
    // 地铁票截止时间
    private TextView ticketDueDate;
    // 生成二维码的字符串
    private TextView QRCodeStr;
    // 二维码图片
    private ImageView ticketQRCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_detail);

        detailsTitleBack = (TextView) findViewById(R.id.details_title_back);
        departToArrive = (TextView) findViewById(R.id.depart_to_arrive);
        ticketPrice = (TextView) findViewById(R.id.ticket_price);
        ticketNum = (TextView) findViewById(R.id.ticket_num);
        ticketDueDate = (TextView) findViewById(R.id.ticket_duedate);
        QRCodeStr = (TextView) findViewById(R.id.QRCodeStr);
        ticketQRCode = (ImageView) findViewById(R.id.ticket_QRCode);

        // 定义Bundle，获取intent传递过来的数据（出发站/到达站/总金额/张数/订单生成时间/订单Id）
        Bundle mBundle = getIntent().getExtras();
        // 通过关键字取出各个数据
        String departStation = mBundle.getString("departStation");
        String arriveStation = mBundle.getString("arriveStation");
        double totalMoney = mBundle.getDouble("totalMoney");
        int ticketCount = mBundle.getInt("ticketCount");
        String orderCreatedTime = mBundle.getString("orderCreatedTime");
        String orderID = mBundle.getString("orderID");

        // 出发站-到达站
        String departToArriveStation = departStation + "-" + arriveStation;
        // 地铁票截止时间
        String dueDate = "";
        try {
            // 把String(orderCreatedTime)转化为Data(date)
            java.text.SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(orderCreatedTime);

            // 地铁票截止时间 = 订单生成时间 + 7天
            Calendar calender = Calendar.getInstance();
            calender.setTime(date);
            calender.add(Calendar.WEEK_OF_YEAR, 1);
            date = calender.getTime();

            // 把Data(date)转化为String(dueDate)
            dueDate = format.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 把数据添加到界面中的TextView中
        departToArrive.setText(departToArriveStation);
        ticketPrice.setText(Double.toString(totalMoney));
        ticketNum.setText(ticketCount + "");
        ticketDueDate.setText(dueDate);
        // 把出发站/到达站/总金额/张数/订单生成时间/订单Id全部用于生成二维码，但只把订单id显示在界面中
        QRCodeStr.setText(orderID);

        // 生成二维码
        try {
            // 调用方法createCode生成二维码
            Bitmap bitmap = QRCodeUtils.createCode(getApplicationContext(), orderID + departToArriveStation + Double.toString(totalMoney) + ticketCount + dueDate);
            // 将二维码在界面中展示
            ticketQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        detailsTitleBack.setOnClickListener(this);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.details_title_back:
//                //启动主界面(购票界面)的活动
//                startActivity(new Intent(PayDetailActivity.this, MainActivity.class));
                //启动历史记录界面的活动
                startActivity(new Intent(PayDetailActivity.this, OrderHistoryActivity.class));
                break;
            default:
                break;
        }
    }

}
