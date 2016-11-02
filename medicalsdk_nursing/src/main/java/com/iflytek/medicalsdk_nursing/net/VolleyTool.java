package com.iflytek.medicalsdk_nursing.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.iflytek.android.framework.base.SingleRequestQueen;
import com.iflytek.android.framework.volley.AuthFailureError;
import com.iflytek.android.framework.volley.DefaultRetryPolicy;
import com.iflytek.android.framework.volley.Response;
import com.iflytek.android.framework.volley.RetryPolicy;
import com.iflytek.android.framework.volley.VolleyError;
import com.iflytek.android.framework.volley.toolbox.StringRequest;
import com.iflytek.medicalsdk_nursing.CommUtil;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;


/**
 * @Title: com.iflytek.medicalassistant.net.VolleyTool
 * @Copyright: IFlytek Co., Ltd. Copyright 16/3/31-下午2:57,  All rights reserved
 * @Description: TODO 网络请求工具;
 * @author: chenzhilei
 * @data: 16/3/31 下午1:57
 * @version: V1.0
 */
public abstract class VolleyTool implements Handler.Callback {

    private Context mContext;

    private Handler handler;

    private Message msg;


    private SoapResult soapResult;


    public VolleyTool(Context context) {
        this.mContext = context;
        handler = new Handler(this);
        soapResult = new SoapResult();
    }

    @Override
    public boolean handleMessage(Message msg) {
        soapResult = (SoapResult) msg.obj;
        if (soapResult.isFlag()) {
            try {
                getRequest(msg.what, soapResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dismiss();
        } else {
//            if (SysCode.ERROR_CODE.SYSTEM_ERROR.equals(soapResult.getErrorCode())){
//                soapResult.setErrorName(SysCode.ERROR_NAME.SYSTEM_ERROR);
//                BaseToast.showToastNotRepeat(mContext,SysCode.ERROR_NAME.SYSTEM_ERROR,SysCode.TOAST);
//            }
//            try {
//                onErrorRequest(soapResult);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            dismiss();
        }
        return false;
    }


    /**
     * 请求得到数据
     *
     * @param msgWhat
     * @param result
     */
    public abstract void getRequest(int msgWhat, SoapResult result) throws JSONException, Exception;

    /**
     * 网络未连接
     */
    public abstract void onNetUnConnected();

    /**
     * 返回异常
     */
    public abstract void onErrorRequest(SoapResult result) throws Exception;


    /**
     * 发送Json请求（IP自配--针对特殊服务情况）
     *
     * @param msgWhat
     * @param isShowDialog
     * @param paramsMap
     * @param requestMethod
     * @param method
     * @param serverIP
     */
    public void sendJsonRequest(final int msgWhat, final boolean isShowDialog, String paramsMap, int requestMethod, final String method, String serverIP) {
        final SoapResult soapResult = new SoapResult();
        //默认获取数据为true
        soapResult.setFlag(true);
        //判断网络连接状态
        if (!CommUtil.getInstance().isNetworkConnected(mContext)) {
            onNetUnConnected();
            return;
        }

        String requestUrl = serverIP + "/" + method;
        Log.d("Request", "请求地址:" + requestUrl);
        Log.d("Request", "请求参数:" + paramsMap);

//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestMethod, requestUrl, paramsMap, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                Log.d("Request","请求结果："+jsonObject.toString());
//
//                msg = Message.obtain();
//                msg.what = msgWhat;
//                msg.obj = soapResult;
//                handler.sendMessage(msg);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                volleyError.printStackTrace();
//                Log.e("ERROR", volleyError.getMessage() + volleyError.getStackTrace());
//
//
//                msg = Message.obtain();
//                msg.what = msgWhat;
//                msg.obj = soapResult;
//                handler.sendMessage(msg);
//            }
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Accept", "application/json");
//                headers.put("Content-Type", "application/json; charset=UTF-8");
//                return headers;
//            }
//        };

        StringRequest stringRequest = new StringRequest(requestMethod, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                String resultStr = s.replace("\\", "");
// 处理完成后赋值回去
                resultStr = resultStr.substring(1,resultStr.length() - 1);
//                s.replace("\\","");
                Log.d("Request", "请求结果：" + resultStr.toString());
                soapResult.setData(String.valueOf(resultStr));
                msg = Message.obtain();
                msg.what = msgWhat;
                msg.obj = soapResult;
                handler.sendMessage(msg);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Log.e("ERROR", volleyError.getMessage() + volleyError.getStackTrace());
                msg = Message.obtain();
                msg.what = msgWhat;
                msg.obj = soapResult;
                handler.sendMessage(msg);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        /**
         * 超时重新请求问题
         */
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, 0, -1f);
        stringRequest.setRetryPolicy(retryPolicy);
        stringRequest.setTag(mContext.getClass().getSimpleName());
        SingleRequestQueen.getInstance(mContext).getRequestQueue().add(stringRequest);
//
    }


    public void showProgressDialog() {
    }

    public void dismiss() {
    }


}
