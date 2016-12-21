package com.worktogether.subwayticket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PayDetailActivity extends AppCompatActivity {

    private TextView QRCodeNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_detail);

        QRCodeNum = (TextView) findViewById(R.id.QRCodeStr);
    }

    public void makeQRCode(View view){

    }
}
