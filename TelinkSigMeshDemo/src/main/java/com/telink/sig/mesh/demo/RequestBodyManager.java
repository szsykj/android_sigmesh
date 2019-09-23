package com.telink.sig.mesh.demo;

import android.util.Log;

import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 封装请求传参
 */
public class RequestBodyManager {
    private static final String TAG = "RequestBodyManager";

    /**
     * 得到邮箱验证码
     *
     * @return
     */
    public static RequestBody getAuthValue(String deviceAddress) {
        try {
            JSONObject body = new JSONObject();
            body.put("deviceAddress", deviceAddress);
            return getRequestJsonBody(null, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 构造通用参数
     *
     * @return
     */
    private static RequestBody getRequestJsonBody(String token, Object body) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hA", 10000);
        jsonObject.put("hB", System.currentTimeMillis());
        jsonObject.put("hE", 3);
        jsonObject.put("hH", "0.0.1");
        jsonObject.put("hF", 0);
        jsonObject.put("hG", body);
        try {
            jsonObject.put("hI", "cn" );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (token != null) {
            jsonObject.put("hC", token);
        }
        return getBody(jsonObject.toString());
    }

    /**
     * string转RequestBody
     *
     * @param str json字符串
     * @return
     */
    private static RequestBody getBody(String str) {
        Log.e(TAG, "getRequestGsonBody() called with: str = [" + str + "]");
        return RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), str);
    }







}
