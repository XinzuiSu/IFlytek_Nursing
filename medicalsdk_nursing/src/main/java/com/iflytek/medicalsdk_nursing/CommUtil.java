package com.iflytek.medicalsdk_nursing;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iflytek.android.framework.toast.BaseToast;
import com.iflytek.android.framework.volley.DefaultRetryPolicy;
import com.iflytek.android.framework.volley.Request;
import com.iflytek.android.framework.volley.RequestQueue;
import com.iflytek.android.framework.volley.Response;
import com.iflytek.android.framework.volley.RetryPolicy;
import com.iflytek.android.framework.volley.VolleyError;
import com.iflytek.android.framework.volley.toolbox.StringRequest;
import com.iflytek.android.framework.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gangpeng on 15/11/12.
 */
public class CommUtil {
    private static float screenDensity;
    private static CommUtil commUtil;
    private static long lastClickTime;

    public static CommUtil getInstance() {

        if (commUtil == null) {
            commUtil = new CommUtil();
        }
        return commUtil;
    }

    /**
     * map 转 jsonString
     *
     * @param map
     * @return
     */
    public static String changeJson(Map<String, String> map) {
        JsonObject list = new JsonObject();
        if (map.size() > 0) {
            for (String s : map.keySet()) {
                list.addProperty(s, map.get(s));
            }
        }
        return list.toString();
    }

    /**
     * objMap 转 jsonString
     *
     * @param map
     * @return
     */
    public static String changeJsonByObj(Map<String, Object> map) {
        return new Gson().toJson(map);
    }





    /**
     * 用于测试接口
     */
    public static void testInterface(Context context, String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("testInterface", "response" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("testInterface", "error" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> dataMap = new HashMap<String, String>();
                dataMap.put("name", "");
                return dataMap;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        //可设置 超时时间
        RetryPolicy retryPolicy = new DefaultRetryPolicy(Integer.parseInt("10000"), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);
    }


    /**
     * 检测指定路径文件是否存在，不存在则创建
     *
     * @param filePath
     * @throws IOException
     */
    public static boolean checkFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            //如果 父文件夹 不存在，则创建
            if (!file.getParentFile().exists()) {
                if (file.getParentFile().mkdirs()) {
                }
            }
            try {
                if (file.createNewFile()) {
                }
            } catch (IOException e) {
                Log.i("file", "拍照文件创建失败");
                return false;
            }
        }
        return true;
    }

    /**
     * 判断网络是否连接  by aawang
     *
     * @return
     */
//    public boolean isNetworkConnected(Context mContext) {
//        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
//        if (mNetworkInfo != null) {
//            return mNetworkInfo.isAvailable();
//        }
//        BaseToast.showToastNotRepeat(mContext, "请先连接网络",
//                2000);
//        return false;
//    }

    /**
     * 判断网络是否连接  by aawang
     *
     * @return
     */
    public static boolean isNetworkConnected(final Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseToast.showToastNotRepeat(context.getApplicationContext(), "请先连接网络",
                        2000);
            }
        });
        return false;
    }



    /**
     * 获取手机状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 获取APP的versionCode
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 防止重复点击
     * @return 是否能点击
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 200) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


}
