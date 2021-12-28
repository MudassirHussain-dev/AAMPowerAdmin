package com.aampower.aampoweradmin.utils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {

    RequestQueue mRequestQueue;

    public HTTPRequest(RequestQueue requestQueue){
        this.mRequestQueue = requestQueue;
    }

    public void GET(String url, Map<String, String> params, Response.Listener<String> response_listener, Response.ErrorListener error_listener, String API_KEY, String stringRequestTag) {
        final Map<String, String> mParams = params;
        final String mAPI_KEY = API_KEY;
        final String mUrl = url;

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                mUrl,
                response_listener,
                error_listener
        ) {
            @Override
            protected Map<String, String> getParams() {
                return mParams;
            }

            @Override
            public String getUrl() {
                StringBuilder stringBuilder = new StringBuilder(mUrl);
                int i = 1;
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    String key;
                    String value;
                    try {
                        key = URLEncoder.encode(entry.getKey(), "UTF-8");
                        value = URLEncoder.encode(entry.getValue(), "UTF-8");
                        if (i == 1) {
                            stringBuilder.append("?" + key + "=" + value);
                        } else {
                            stringBuilder.append("&" + key + "=" + value);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    i++;

                }
                String url = stringBuilder.toString();

                return url;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                if (!(mAPI_KEY.equals(""))) {
                    headers.put("X-API-KEY", mAPI_KEY);
                }
                return headers;
            }
        };

        if (stringRequestTag != null) {
            stringRequest.setTag(stringRequestTag);
        }

        mRequestQueue.add(stringRequest);
    }

}
