package com.aampower.aampoweradmin.utils;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by waqar on 10/12/2017.
 */

public class MySingleTon {

    private static MySingleTon mInstance;
    private RequestQueue requestQueue;
    private static Context context;

    private MySingleTon(Context contextt) {
        context = contextt;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {

        if (requestQueue == null) {

            requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        }

        return requestQueue;
    }

    public static synchronized MySingleTon getInstance(Context context) {

        if (mInstance == null) {

            mInstance = new MySingleTon(context);

        }
        return mInstance;
    }

    public<T> void addRequestQue(Request<T> request){

        request.setShouldCache(true);// no caching url...
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,//time to wait for it in this case 20s
                        3,//tries in case of error
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
//        request.setRetryPolicy((new DefaultRetryPolicy(10000, 1, 1)));
        requestQueue.add(request);

    }

}
