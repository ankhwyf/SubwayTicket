package com.worktogether.subwayticket;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.worktogether.subwayticket.util.CountDownTimerUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;

import static android.Manifest.permission_group.SMS;

/**
 * Created by stone-chn on 12/18/16.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //正则表达式:验证密码(6~20位字母、字符、数字)
    public static final String REGEX_PASSWORD = "^[\\p{Alnum}|\\p{Punct}}]{6,20}$";

    //正则表达式:验证手机号
    public static final String REGEX_MOBILE = "^[1][345789][0-9]{9}$";

    private static  final int REGISTER_YES=0x11;
    private static  final int REGISTER_NO=0x10;

    private LinearLayout mLayoutLogin;
    private EditText etTel;
    private EditText etPwd;
    private EditText etPwdConfirm;
    private EditText etIdentifyCode;

    private Button btnGetIdentifyCode;
    private Button btnRegister;


    private String strTel;
    private String strPwd;
    private String strPwdConfirm;
    private String strIdentifyCode;

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REGISTER_YES:
                    publicDialog("警告",R.drawable.warning,"该手机号码已被注册!");
                    break;
                case REGISTER_NO:
                    //70s倒计时
                    CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(btnGetIdentifyCode, 70000, 1000);
                    mCountDownTimerUtils.start();
                    SMSSDK.getVerificationCode("86", strTel);
                    break;
                default:
                    int event = msg.arg1;
                    int result = msg.arg2;
                    Object data = msg.obj;
                    //回调完成
                    if(result==SMSSDK.RESULT_COMPLETE){
                        //验证码验证成功
                        if(event==SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            BmobUser bu = new BmobUser();
                            strTel = etTel.getText().toString().trim();
                            strPwd = etPwd.getText().toString().trim();
                            //对密码进行加密存储
                            strPwd=encryption(strPwd);

                            bu.setUsername(strTel);
                            bu.setPassword(strPwd);
                            bu.setMobilePhoneNumber(strTel);
                            bu.signUp(new SaveListener<BmobUser>() {
                                @Override
                                public void done(BmobUser s, BmobException e) {
                                    if (e == null) {
                                        toast("注册成功！^_^");
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    } else {
                                        Log.d("bmob", "失败" + e.getMessage() + "," + e.getErrorCode());
                                    }
                                }
                            });
                        }
                        //验证码获取成功
                        else if(event==SMSSDK.EVENT_GET_VERIFICATION_CODE) {

                        }
                        //返回支持发送验证码的国家列表
                        else if(event==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {

                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initSDK();
        // init components
        findviews();
        // set action listen event
        addListener();
    }

    private void findviews() {
        mLayoutLogin=(LinearLayout)findViewById(R.id.layout_login);
        etTel = (EditText) findViewById(R.id.register_tel);
        etPwd = (EditText) findViewById(R.id.register_pwd);
        etPwdConfirm = (EditText) findViewById(R.id.register_pwd_confirm);
        etIdentifyCode = (EditText) findViewById(R.id.register_confirm);

        btnGetIdentifyCode = (Button) findViewById(R.id.register_get_identify_code);
        btnRegister = (Button) findViewById(R.id.register_btn);
    }

    private void addListener() {
        btnGetIdentifyCode.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        mLayoutLogin.setOnClickListener(this);
    }


    public String encryption(String plaintext) {

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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //获取验证码btn
            case R.id.register_get_identify_code:
                confirmIdentifyCode();
                break;
            //注册btn
            case R.id.register_btn:
                if (confirmInvalid()) {
                    strTel = etTel.getText().toString().trim();
                    strIdentifyCode=etIdentifyCode.getText().toString().trim();
                    SMSSDK.submitVerificationCode("86", strTel,strIdentifyCode );
                }
                break;

            case R.id.layout_login:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                break;
        }
    }

    /**
     * 校验密码
     *
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    /**
     * 校验输入是否合法
     *
     * @param
     * @return 校验通过返回true，否则返回false
     */
    public Boolean confirmInvalid() {
        boolean flag = false;
        strTel = etTel.getText().toString().trim();
        strPwd = etPwd.getText().toString().trim();
        strPwdConfirm = etPwdConfirm.getText().toString().trim();
        strIdentifyCode = etIdentifyCode.getText().toString().trim();
        if (strTel.equals("")) {
            toast("请输入手机号码");
        } else if (!isMobile(strTel)) {
            toast("请输入正确的手机号码");
        } else if (strPwd.equals("")) {
            toast("请输入密码");
        } else if (!isPassword(strPwd)) {
            toast("请输入合法的密码");
        } else if (strPwdConfirm.equals("")) {
            toast("请输入确认密码");
        } else if (!strPwdConfirm.equals(strPwd)) {
            toast("两次密码输入不一致");
        } else if (strIdentifyCode.equals("")) {
            toast("请输入验证码");
        } else if (strIdentifyCode.length() != 4) {
            toast("请输入4位验证码");
        } else {
            flag = true;
        }
        return flag;
    }

    /**
     * 校验手机号码
     */
    public void confirmIdentifyCode() {
        strTel = etTel.getText().toString().trim();
        if (strTel.equals("")) {
            toast("请输入手机号码");
        } else if (!isMobile(strTel)) {
            toast("请输入正确的手机号码");
        } else {
            BmobQuery<BmobUser> query = new BmobQuery<>();
            //查询mobilePhoneNumber= “strTel”的数据
            query.addWhereEqualTo("mobilePhoneNumber", strTel);
            query.findObjects(new FindListener<BmobUser>() {
                @Override
                public void done(List<BmobUser> list, BmobException e) {
                    if (e == null) {
                        if (list!=null&&list.size() > 0) {
                            handler.sendEmptyMessage(REGISTER_YES);
                        } else {
                            handler.sendEmptyMessage(REGISTER_NO);
                        }
                    } else {
                        Log.d("bmob", "失败 from confirmIdentifyCode：" + e.getMessage() + ", " + e.getErrorCode());
                    }
                }
            });
        }
    }


    private void toast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 初始化短信SDK
     */
    private void initSDK() {
        try {

            SMSSDK.initSDK(this, "1a351ccc52300", "9a4f1337cb6517d927ea5d4aa26d6177");

            EventHandler eventHandler = new EventHandler() {
                public void afterEvent(int event, int result, Object data) {
                    Message msg = new Message();
                    msg.arg1 = event;
                    msg.arg2 = result;
                    msg.obj = data;
                    handler.sendMessage(msg);
                }
            };
            SMSSDK.registerEventHandler(eventHandler); // 注册短信回调

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 销毁回调监听接口
     */
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }

    private void publicDialog(String title,int iconID,String message) {

        AlertDialog.Builder  publicDialog = new AlertDialog.Builder(RegisterActivity.this);

        publicDialog.setTitle(title);
        publicDialog.setIcon(iconID);
        publicDialog.setMessage(message);
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
        publicDialog.show();
    }
}
