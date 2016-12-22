package com.worktogether.subwayticket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by stone-chn on 12/18/16.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_tel;
    private EditText et_pwd1;
    private EditText et_pwd2;
    private EditText et_confirm;

    private Button btn_get_confirm;
    private Button btn_register;

    private String str_tel;
    private String str_pwd1;
    private String str_pwd2;
    private String str_confirm;
    private String current_user_id;

    private boolean is_confirm;
    private boolean is_confirm_sent;
    private boolean is_invalid_tel;
    private boolean is_invalid_pwd;

    public AlertDialog.Builder publicDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // init components
        findviews();
        et_tel.setFocusable(true);

        // init publicDialog
        initPublicDialog();

        // set action listen event
        addListener();
    }

    private void findviews() {

        et_tel      = (EditText) findViewById(R.id.register_tel);
        et_pwd1     = (EditText) findViewById(R.id.register_pwd1);
        et_pwd2     = (EditText) findViewById(R.id.register_pwd2);
        et_confirm  = (EditText) findViewById(R.id.register_confirm);

        btn_get_confirm = (Button) findViewById(R.id.register_get_confirm_btn);
        btn_register    = (Button) findViewById(R.id.register_btn);
    }

    private void addListener() {

        btn_get_confirm.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        et_tel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    testTel();
                    if (is_invalid_tel) {
                        publicDialog.show();
                        et_tel.setText("");
                        et_tel.requestFocus();
                        is_invalid_tel = false;
                    }
                }
            }
        });
    }

    private void initPublicDialog() {

        publicDialog = new AlertDialog.Builder(RegisterActivity.this);

        publicDialog.setTitle("警告：");
        publicDialog.setCancelable(false);
        publicDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        publicDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    private void testTel () {

        str_tel = et_tel.getText().toString();
        if (TextUtils.isEmpty(str_tel)) {
            is_invalid_tel = true;
            publicDialog.setMessage("手机号码不能为空！");
        }
        else {
            str_tel = str_tel.trim();
            if (!str_tel.matches("1\\d{10}")) {
                is_invalid_tel = true;
                publicDialog.setMessage("请输入合法的手机号码！");
            }
            else {
                BmobQuery<BmobUser> query = new BmobQuery<>();
                query.addWhereEqualTo("username", str_tel);
                query.findObjects(new FindListener<BmobUser>() {
                    @Override
                    public void done(List<BmobUser> list, BmobException e) {
                        if (e == null) {
                            if (list.size() > 0) {
                                is_invalid_tel = true;
                                publicDialog.setMessage("该号码已注册！");
                            }
                        } else {
                            Log.d("Bmob", "查询失败");
                            is_invalid_tel = true;
                            publicDialog.setMessage("请稍后重试！");
                        }
                    }
                });
            }
        }
    }

    private void testPwd() {

        str_pwd1 = et_pwd1.getText().toString();
        str_pwd2 = et_pwd2.getText().toString();

        if (TextUtils.isEmpty(str_pwd1) || TextUtils.isEmpty(str_pwd2)) {
            is_invalid_pwd = true;
            publicDialog.setMessage("密码不能为空！");
        }
        else {
            str_pwd1 = str_pwd1.trim();
            str_pwd2 = str_pwd2.trim();

            if (!str_pwd1.equals(str_pwd2)) {
                is_invalid_pwd = true;
                publicDialog.setMessage("两次密码不一致！");
            }
        }
    }

    private void testConfirm() {

        str_confirm = et_confirm.getText().toString();
        if (TextUtils.isEmpty(str_confirm)) {
            publicDialog.setMessage("请填入验证码");
            publicDialog.show();
        }
        else {
            str_confirm = str_confirm.trim();
            BmobUser.signOrLoginByMobilePhone(str_tel, str_confirm, new LogInListener<BmobUser>() {

                @Override
                public void done(BmobUser user, BmobException e) {
                    if(user != null){
                        is_confirm = true;
                        Log.i("Bmob","用户验证成功");
                    }
                }
            });
        }

    }

    private String encryption(String plaintext) {

        String hashtext = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(plaintext.getBytes());
            byte[] digest = md5.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            hashtext = bigInt.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashtext;
    }

    private class StopConfirmBtn implements Runnable {

        private long seconds;
        public StopConfirmBtn(long seconds) {
            this.seconds = seconds;
        }

        @Override
        public void run() {
            btn_get_confirm.setClickable(false);
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 1000 * seconds){

            }
            btn_get_confirm.setClickable(true);
        }
    }

    @Override
    public void onClick(View v) {

        testTel();
        if (is_invalid_tel) {
            publicDialog.show();
            is_invalid_tel = false;
            return;
        }

        testPwd();
        if (is_invalid_pwd) {
            publicDialog.show();
            is_invalid_pwd = false;
            return;
        }

        switch (v.getId()) {
            case R.id.register_btn:
            {
                BmobSMS.requestSMSCode(str_tel, "SubwayTicket", new QueryListener<Integer>() {

                    @Override
                    public void done(Integer smsId, BmobException ex) {
                        if (ex == null) {//验证码发送成功
                            is_confirm_sent = true;
                            Log.i("Bmob", "短信id：" + smsId);//用于查询本次短信发送详情
                        }
                    }
                });

                if (is_confirm_sent) {
                    publicDialog.setTitle("提示：");
                    publicDialog.setMessage("验证码已发送");
                    publicDialog.show();
                    publicDialog.setMessage("警告：");
                    Thread stopConfirmBtnThd = new Thread(new StopConfirmBtn(1));
                    stopConfirmBtnThd.start();
                }
                break;
            }

            case R.id.register_get_confirm_btn:
            {
                testConfirm();
                if (is_confirm) {
                    BmobUser user = new BmobUser();
                    user.setUsername(str_tel);
                    String encrypText = encryption(str_pwd1);
                    if (encrypText == null) {
                        publicDialog.setMessage("出错啦，请稍候再试！");
                        publicDialog.show();
                        return;
                    } else {
                        user.setPassword(encrypText);
                    }
                    user.setMobilePhoneNumberVerified(true);
                    user.setMobilePhoneNumber(str_tel);

                    user.save(new SaveListener<String>() {

                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                current_user_id = objectId;
                                publicDialog.setTitle("提示：");
                                publicDialog.setMessage("注册成功！");
                                publicDialog.show();
                            } else {
                                publicDialog.setMessage("出错啦，请稍候再试！");
                                publicDialog.show();
                                Log.i("Bmob", "注册失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                }

                break;
            }
        }
    }
}
