package com.worktogether.subwayticket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class MainActivity extends AppCompatActivity {

    Boolean isLogin=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //默认初始化
        Bmob.initialize(this,"9ea43370e3823c7bc07ba1f9e851088a");
        //自v3.4.7版本开始，设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间（s）

        queryData();

        /**
         * 添加数据使用BmobObject对象的save方法，就可以将当前对象的内容保存到Bmob服务端。
         **/
        OrderHistory curOrder=new OrderHistory();

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




    public void useCodeTemplate(){
        BmobQuery<BmobObject> query=new BmobQuery<>();
        BmobQuery<BmobObject> innerQuery=new BmobQuery<>();
        query.addWhereMatchesQuery("","",innerQuery).findObjects(new FindListener<BmobObject>() {
            @Override
            public void done(List<BmobObject> list, BmobException e) {
                if(e==null) {

                } else {
                    Log.e("",e.toString());
                }
            }
        });
    }

}
