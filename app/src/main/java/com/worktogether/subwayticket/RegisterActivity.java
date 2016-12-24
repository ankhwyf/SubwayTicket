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
    private EditText et_tel;
    private EditText et_pwd;
    private EditText et_pwd_confirm;
    private EditText et_identify_code;

    private Button btn_get_identify_code;
    private Button btn_register;


    private String str_tel;
    private String str_pwd;
    private String str_pwdConfirm;
    private String str_identifyCode;

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REGISTER_YES:
                    publicDialog("警告",R.drawable.warning,"该手机号码已被注册!");
                    break;
                case REGISTER_NO:
                    //70s倒计时
                    CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(btn_get_identify_code, 70000, 1000);
                    mCountDownTimerUtils.start();
                    SMSSDK.getVerificationCode("86", str_tel);
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
                            str_tel = et_tel.getText().toString().trim();
                            str_pwd = et_pwd.getText().toString().trim();
                            //对密码进行加密存储
                            str_pwd=encryption(str_pwd);

                            bu.setUsername(str_tel);
                            bu.setPassword(str_pwd);
                            bu.setMobilePhoneNumber(str_tel);
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
        et_tel = (EditText) findViewById(R.id.register_tel);
        et_pwd = (EditText) findViewById(R.id.register_pwd);
        et_pwd_confirm = (EditText) findViewById(R.id.register_pwd_confirm);
        et_identify_code = (EditText) findViewById(R.id.register_confirm);

        btn_get_identify_code = (Button) findViewById(R.id.register_get_identify_code);
        btn_register = (Button) findViewById(R.id.register_btn);
    }

    private void addListener() {
        btn_get_identify_code.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        mLayoutLogin.setOnClickListener(this);
    }


    public static String encryption(String plaintext) {

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
                    str_tel = et_tel.getText().toString().trim();
                    str_identifyCode=et_identify_code.getText().toString().trim();
                    SMSSDK.submitVerificationCode("86", str_tel,str_identifyCode );
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
        str_tel = et_tel.getText().toString().trim();
        str_pwd = et_pwd.getText().toString().trim();
        str_pwdConfirm = et_pwd_confirm.getText().toString().trim();
        str_identifyCode = et_identify_code.getText().toString().trim();
        if (str_tel.equals("")) {
            toast("请输入手机号码");
        } else if (!isMobile(str_tel)) {
            toast("请输入正确的手机号码");
        } else if (str_pwd.equals("")) {
            toast("请输入密码");
        } else if (!isPassword(str_pwd)) {
            toast("请输入合法的密码");
        } else if (str_pwdConfirm.equals("")) {
            toast("请输入确认密码");
        } else if (!str_pwdConfirm.equals(str_pwd)) {
            toast("两次密码输入不一致");
        } else if (str_identifyCode.equals("")) {
            toast("请输入验证码");
        } else if (str_identifyCode.length() != 4) {
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
        str_tel = et_tel.getText().toString().trim();
        if (str_tel.equals("")) {
            toast("请输入手机号码");
        } else if (!isMobile(str_tel)) {
            toast("请输入正确的手机号码");
        } else {
            BmobQuery<BmobUser> query = new BmobQuery<>();
            //查询mobilePhoneNumber= “strTel”的数据
            query.addWhereEqualTo("mobilePhoneNumber", str_tel);
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
                        toast("抱歉，请稍候重试！");
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

    public void publicDialog(String title, int iconID, String message) {

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
