package com.worktogether.subwayticket;

import android.app.Application;
import android.util.Log;

import com.worktogether.subwayticket.bean.SubwayLine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.smssdk.SMSSDK;

/**
 * Created by Ankhwyf on 16/12/17.
 */

public class MyApplication extends Application {
    //设置静态表名
    private static final String LINEONE = "subway_station_1";
    private static final String LINEFOUR = "subway_station_4";
    //设置静态字段名
    private static final String STATIONNAME = "station_name";
    private static final String DISTANCE = "distance";
    private static final double INF = 999999;

    //一号线
    public static List<SubwayLine> mSubwayLineOneList = new ArrayList<>();
    //四号线
    public static List<SubwayLine> mSubwayLineFourList = new ArrayList<>();
    //地铁线路ArrayList
    public static List<String> mLineList = new ArrayList<>();
    //站点ArrayList
    public static List<String> mStationListOne = new ArrayList<>();
    public static List<String> mStationListFour = new ArrayList<>();
    //用于存储1，4号线的站点距离
    public static List<List<Double>> mStationDistance = new ArrayList<>();
    //用于存储1，4号线的站点35
    public static List<String> mAllStations = new ArrayList<>();

    private int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        //默认初始化
        Bmob.initialize(this, "9ea43370e3823c7bc07ba1f9e851088a");
        //  查询地铁的两张表 初始化List
        queryLine(LINEONE, mSubwayLineOneList, mStationListOne);
        queryLine(LINEFOUR, mSubwayLineFourList, mStationListFour);

        //地铁ArrayList
        if (mLineList.size() == 0) {
            mLineList.add("地铁一号线");
            mLineList.add("地铁四号线");
        }
    }

    /**
     * 地铁1号线
     * 查询结果是JSONArray ，需自行解析
     * [{"updatedAt":"2016-12-16 11:26:04",
     * "station_name":"湘湖",
     * "distance":0,
     * "objectId":"Vo8a777K",
     * "createdAt":"2016-12-16 11:25:27"},
     **/
    //传入表名 和 用于存储地铁具体站点信息的List
    public void queryLine(final String lineStr, final List<SubwayLine> mSubwayLineList, final List<String> mStationList) {
        BmobQuery query = new BmobQuery(lineStr);
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if (e == null) {
                    //遍历
                    count++;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            //解析JSONArray数组信息
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String stationName = obj.getString(STATIONNAME);
                            Double distance = obj.getDouble(DISTANCE);

                            SubwayLine stationInfo = new SubwayLine(lineStr);
                            stationInfo.setDistance(distance);
                            stationInfo.setStation_name(stationName);

                            mSubwayLineList.add(stationInfo);
                            mStationList.add(stationName);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }//结束loop

                    /*
                    *  查询完毕，计算距离
                    *  因为目前只有两条地铁信息，因此count数到2即开始计算距离
                    * */
                    if (count == 2) {
                        //计算距离
                        CalDistance();
                    }
                } else {
                    Log.d("bmob", "查询失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    /*
    *  初始化 mStationDistance 站点距离
    * */
    public void initDistance(int n) {
        for (int i = 0; i < n; i++) {
            List<Double> temp = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (i == j) temp.add(0.0);
                else temp.add(INF);
            }
            mStationDistance.add(temp);
        }
    }
    /*
      *  Floyd ：计算并存储各站点之间的最短距离
      * */
    public void Floyd(int n) {
        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (mStationDistance.get(i).get(j) > mStationDistance.get(i).get(k) + mStationDistance.get(k).get(j)) {
                        double a = mStationDistance.get(i).get(k) + mStationDistance.get(k).get(j);
                        mStationDistance.get(i).set(j, a);
                    }
    }

   /*
   *  mStationListOne mStationListFour
   * 根据拿到的距离信息，将距离存储至mStationDistance
   * */
    public void CalDistance() {

        int mSizeOne = mStationListOne.size(); //28
        int mSizeFour = mStationListFour.size();//10
        //初始化mAllStations
        for (int i = 0; i < mSizeOne; i++) {
            mAllStations.add(mStationListOne.get(i));
        }
        for (int i = 0; i < mSizeFour; i++) {
            if (mAllStations.contains(mStationListFour.get(i)) == false) {
                mAllStations.add(mStationListFour.get(i));
            }
        }

        int mAllSize = mAllStations.size();        //35
        double dis = 0.0;
        int i = 0;

        //初始化mStationDistance
        initDistance(mAllSize);

        //利用1号线信息给mStationDistance赋值
        for (i = 0; i < mSizeOne - 1; i++) {
            dis = mSubwayLineOneList.get(i + 1).getDistance();
            mStationDistance.get(i).set(i + 1, dis);
            mStationDistance.get(i + 1).set(i, dis);
        }
        //近江-->城星路
        dis = mSubwayLineFourList.get(1).getDistance();
        mStationDistance.get(5).set(mSizeOne, dis);
        mStationDistance.get(mSizeOne).set(5, dis);

        //利用4号线信息给mStationDistance赋值
        for (i = mSizeOne; i < mAllSize - 1; i++) {
            dis = mSubwayLineFourList.get((i + 1) % mSizeOne + 1).getDistance();
            mStationDistance.get(i).set(i + 1, dis);
            mStationDistance.get(i + 1).set(i, dis);
        }
        //i=34
        //新风-->火车东站
        dis = mSubwayLineFourList.get((i + 1) % mSizeOne + 1).getDistance();
        mStationDistance.get(i).set(15, dis);
        mStationDistance.get(15).set(i, dis);

        //计算
        Floyd(mAllSize);
    }

  /*
   *  输入站名
   * 搜索获取在mAllStations里面站名相应的id
   * */
    public static int getIndexOfStation(String stationName) {
        int index = 0;
        for (int i = 0; i < mAllStations.size(); i++) {
            if (mAllStations.get(i).equals(stationName)) {
                index = i;
                break;
            }
        }
        return index;
    }

}
