package com.worktogether.subwayticket;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.worktogether.subwayticket.util.Constants;
import com.worktogether.subwayticket.util.SharedPreferencesUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;

import static com.worktogether.subwayticket.RegisterActivity.encryption;
import static com.worktogether.subwayticket.RegisterActivity.isMobile;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static BmobUser mCurUser;

    private EditText et_tel;
    private EditText et_pwd;
    private Button btn_login;

    private TextView tv_findPwd;
    private TextView tv_register;

    private ImageView img_eye;

    private String str_tel;
    private String str_pwd;

    private boolean is_pwd_visiable;
    private boolean is_account_valid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init components
        findviews();

        // add action listen event
        addListener();
    }

    private void findviews() {

        et_tel = (EditText) findViewById(R.id.login_tel);
        et_pwd = (EditText) findViewById(R.id.login_pwd);

        img_eye = (ImageView) findViewById(R.id.img_eye);

        btn_login = (Button) findViewById(R.id.login_btn);

        tv_findPwd = (TextView) findViewById(R.id.findpwd_link);
        tv_register = (TextView) findViewById(R.id.register_link);
    }

    private void addListener() {

        btn_login.setOnClickListener(this);

        img_eye.setOnClickListener(this);

        tv_findPwd.setOnClickListener(this);
        tv_register.setOnClickListener(this);

    }

    private void testTelValid() {
        str_tel = et_tel.getText().toString();
        if (TextUtils.isEmpty(str_tel)) {
            toast("用户名不能为空！");
        } else if (!isMobile(str_tel)) {
            toast("请输入有效的手机号码！");
        } else {
            BmobQuery<BmobUser> query = new BmobQuery<>();
            query.addWhereEqualTo("username", str_tel);

            query.findObjects(new FindListener<BmobUser>() {
                @Override
                public void done(List<BmobUser> list, BmobException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            is_account_valid = true;
                        } else {
                            AlertDialog.Builder publicDialog = new AlertDialog.Builder(LoginActivity.this);
                            publicDialog.setTitle("提示");
                            publicDialog.setIcon(R.drawable.reminder);
                            SpannableString spannableString = new SpannableString("该手机号码尚未注册，去注册?");
                            int color = ContextCompat.getColor(LoginActivity.this, R.color.colorLightBlue);
                            ForegroundColorSpan span = new ForegroundColorSpan(color);
                            spannableString.setSpan(span, spannableString.length() - 4, spannableString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            publicDialog.setMessage(spannableString);
                            publicDialog.setCancelable(false);
                            publicDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //启动注册界面的活动
                                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                                }
                            });
                            publicDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            publicDialog.show();
                        }
                    } else {
                        Log.d("bmob", "查询失败" + e.getMessage() + ", " + e.getErrorCode());
                        toast("抱歉，请稍候重试！");
                    }
                }
            });
        }
    }

    private void testValid() {
        testTelValid();
        if (is_account_valid) {
            str_tel = et_tel.getText().toString();
            str_pwd = et_pwd.getText().toString();
            if (TextUtils.isEmpty(str_tel) || TextUtils.isEmpty(str_pwd)) {
                toast("用户名和密码不能为空！");
            } else {
                str_tel = str_tel.trim();
                str_pwd = str_pwd.trim();
                String hashpwd = encryption(str_pwd);
                BmobUser.loginByAccount(str_tel, hashpwd, new LogInListener<BmobUser>() {
                    @Override
                    public void done(BmobUser user, BmobException e) {
                        if (e != null) {
                            toast("抱歉，请稍后重试");
                            Log.d("bmob", "查询失败" + e.getMessage() + ", " + e.getErrorCode());
                        } else if (user != null) {
                            //将当前状态存储至SharedPreferences，登录状态为登录（true）
                            SharedPreferencesUtils.save(LoginActivity.this, Constants.KEY_LOGIN_STATUS, true);
                            toast("登录成功！");
                            //启动购票界面的活动
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            toast("用户名或密码错误");
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.login_btn: {
                testValid();
                break;
            }

            case R.id.img_eye: {
                //http://blog.csdn.net/wq___1994/article/details/51916667
                if (is_pwd_visiable) {
                    img_eye.setImageResource(R.drawable.eye);
                    et_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
                } else {
                    //visible
                    img_eye.setImageResource(R.drawable.noeye);
                    et_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                is_pwd_visiable = !is_pwd_visiable;
                break;
            }

            case R.id.findpwd_link: {
                toast("呀，找不到了呦！！！");
                break;
            }

            case R.id.register_link: {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    private void toast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
