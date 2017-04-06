package com.iflytek.medicalsdk_nursing.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.iflytek.android.framework.base.SingleRequestQueen;
import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.android.framework.volley.AuthFailureError;
import com.iflytek.android.framework.volley.DefaultRetryPolicy;
import com.iflytek.android.framework.volley.Response;
import com.iflytek.android.framework.volley.RetryPolicy;
import com.iflytek.android.framework.volley.VolleyError;
import com.iflytek.android.framework.volley.toolbox.StringRequest;
import com.iflytek.medicalsdk_nursing.util.CommUtil;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
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
public abstract class ChatVolleyTool implements Handler.Callback {

    private Context mContext;

    private Handler handler;

    private Message msg;


    private SoapResult1 soapResult;


    public ChatVolleyTool(Context context) {
        this.mContext = context;
        handler = new Handler(this);
        soapResult = new SoapResult1();
    }

    @Override
    public boolean handleMessage(Message msg) {
        soapResult = (SoapResult1) msg.obj;
        if (soapResult.getFlag() == "true") {
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
            try {
                onErrorRequest(soapResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    public abstract void getRequest(int msgWhat, SoapResult1 result) throws JSONException, Exception;

    /**
     * 网络未连接
     */
    public abstract void onNetUnConnected();

    /**
     * 返回异常
     */
    public abstract void onErrorRequest(SoapResult1 result) throws Exception;

    /**
     * 发送Json请求（IP自配--针对特殊服务情况）
     *
     * @param msgWhat
     * @param isShowDialog
     * @param paramsMap
     * @param requestMethod
     * @param
     * @param serverIP
     */
    public void sendJsonRequest(final int msgWhat, final boolean isShowDialog, final String paramsMap, int requestMethod, String serverIP) {
        final SoapResult1 soapResult = new SoapResult1();
        //默认获取数据为true
        soapResult.setFlag("true");
        //判断网络连接状态
        if (!CommUtil.getInstance().isNetworkConnected(mContext)) {
            onNetUnConnected();
            return;
        }
        if (isShowDialog) {
//            if (lyProgressDialog == null) {
//                lyProgressDialog = new IProgressDialog(mContext, "加载中...", 30000);
//            }
//
//            lyProgressDialog.show();
//            if (mLoadingDialog == null) {
//                mLoadingDialog = new LoadingDialog(mContext, "加载中...", 30000) {
//                    @Override
//                    public void onBackPressed() {
//                        SingleRequestQueen.getInstance(mContext).quitRequest(mContext);
//                        return;
//                    }
//                };
//            }
//            mLoadingDialog.show();
        }
       // final String map = "{\"methodName\":\"creatRoom\",\"userId\":\"111331\",\"userName\":\"创建人\",\"targetId\":\"222\",\"targetName\":\"患者\"}";
        String requestUrl = serverIP;
        Log.d("Request", "请求地址:" + requestUrl);
        Log.d("Request", "请求参数:" + paramsMap);

        StringRequest stringRequest = new StringRequest(requestMethod, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Request", "请求方法：" + "\n请求结果：" + response);
                        try {
                            ResponseData1 rd ;
                            rd = new Gson().fromJson(response, ResponseData1.class);
                            if ("true".equals(rd.getFlag())) {
                                String data = "";
                                String flag = "";
                                if (StringUtils.isNotBlank(rd.getData())) {
                                    flag = "true";
                                    data = rd.getData();
                                } else {
                                    flag = "false";
                                    data = "";
                                }

                                if ("true".equals(flag)) {
                                    soapResult.setData(data);

                                } else {
                                    soapResult.setFlag("false");
                                    soapResult.setErrorCode(flag);
                                    soapResult.setResult(data);

                                }

                            } else {
                                soapResult.setFlag("false");
                                soapResult.setResult(rd.getResult());
                            }
                        } catch (Exception ex) {
                            soapResult.setFlag("false");
                            soapResult.setResult("服务端连接错误！");
                        }
                        msg = Message.obtain();
                        msg.what = msgWhat;
                        msg.obj = soapResult;
                        if (isShowDialog) {
                            msg.arg1 = 1;
                        } else {
                            msg.arg1 = 0;
                        }
                        handler.sendMessage(msg);
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Log.e("ERROR", volleyError.getMessage() + volleyError.getStackTrace());

//                if (volleyError instanceof NetworkError) {
//                    // 网络异常
//                    soapResult.setFlag(false);
//                    soapResult.setErrorCode(SysCode.ERROR_CODE.SOCKET_ERROR);
//                    soapResult.setErrorName(SysCode.ERROR_NAME.SOCKET_ERROR);
//                } else if (volleyError instanceof TimeoutError) {
//                    // 连接超时
//                    soapResult.setFlag(false);
//                    soapResult.setErrorCode(SysCode.ERROR_CODE.TIMEOUT_ERROR);
//                    soapResult.setErrorName(SysCode.ERROR_NAME.TIMEOUT_ERROR);
//                } else if (volleyError instanceof ServerError) {
//                    // 服务器异常
//                    soapResult.setFlag(false);
//                    soapResult.setErrorCode(SysCode.ERROR_CODE.CONNECT_ERROR);
//                    soapResult.setErrorName(SysCode.ERROR_NAME.CONNECT_ERROR);
//                } else {
//                    soapResult.setFlag(false);
//                    soapResult.setErrorCode(SysCode.ERROR_CODE.UNKNOWEN_ERROR);
//                    soapResult.setErrorName(SysCode.ERROR_NAME.UNKNOWEN_ERROR);
//                }
                if (isShowDialog) {
//                    lyProgressDialog.onError(soapResult.getErrorName());
//                    mLoadingDialog.onError(soapResult.getErrorName());
                }
                msg = Message.obtain();
                msg.what = msgWhat;
                msg.obj = soapResult;
                if (isShowDialog) {
                    msg.arg1 = 1;
                } else {
                    msg.arg1 = 0;
                }
                handler.sendMessage(msg);
            }
        }) {


            @Override
            public byte[] getBody() throws AuthFailureError {
                return getWrapBody(paramsMap);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Accept", "charset=UTF-8");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }

//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> map = new HashMap<>();
//                map.put("methodName","creatRoom");
//                map.put("userId","xfsu");
//                map.put("userName","苏笑风");
//                map.put("targetId","111");
//                map.put("targetName","患者");
//                return map;
//            }

        };

        /**
         * 超时重新请求问题
         */
        RetryPolicy retryPolicy = new DefaultRetryPolicy(20000, 0, -1f);
        stringRequest.setRetryPolicy(retryPolicy);
        stringRequest.setTag(mContext.getClass().getSimpleName());
        SingleRequestQueen.getInstance(mContext).getRequestQueue().add(stringRequest);

    }


    public void showProgressDialog() {
    }

    public void dismiss() {
    }

    private static byte[] getWrapBody(String reqJson) {
        // mLog.print(TAG, "req：" + reqJson);

        try {
            return null == reqJson ? null : reqJson.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }


}
