package com.worktogether.subwayticket.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
/**
 * Created by Ankhwyf on 16/12/17.
 */

public class SharedPreferencesUtils {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static final String FILE_PREFERENCES = "carInspection";

    /**

     * 存入数据

     * @param context

     * @param key

     * @param value

     */
    public static void save(Context context, String key, Object value) {
        if (null == sharedPreferences) {
            sharedPreferences = context.getSharedPreferences(FILE_PREFERENCES, Context.MODE_PRIVATE);
        }
        if (null == editor) {
            editor = sharedPreferences.edit();
        }

        // 判断value的类型，并存入value


        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        }  else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }

        editor.commit();
        editor = null;
        sharedPreferences = null;
    }

    /**

     * 从sharedpreferences里面获取数据

     * @param context

     * @param key

     * @param valueType

     * @return

     */
    public static Object get(Context context, String key, int valueType) {
        Object result = null;
        sharedPreferences = context.getSharedPreferences(FILE_PREFERENCES, Activity.MODE_WORLD_READABLE);
        // 判断要获取的数据的类型


        switch (valueType) {
            case Constants.TYPE_INTEGER:
                result = sharedPreferences.getInt(key, -1);
                break;
            case Constants.TYPE_STRING:
                result = sharedPreferences.getString(key, "");
                break;
            case Constants.TYPE_FLOAT:
                result = sharedPreferences.getFloat(key, -1);
                break;
            case Constants.TYPE_LONG:
                result = sharedPreferences.getLong(key, -1);
                break;
            case Constants.TYPE_BOOLEAN:
                result = sharedPreferences.getBoolean(key, false);
                break;
        }
        sharedPreferences = null;
        return result;
    }
}
