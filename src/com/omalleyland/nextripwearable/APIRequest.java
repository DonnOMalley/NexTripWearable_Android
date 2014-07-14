package com.omalleyland.nextripwearable;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by donn on 7/6/14.
 */
public class APIRequest extends AsyncTask<String, Void, String> {

    private Context ctx;

    public APIRequest() {}

    public APIRequest(Context ctx) {
        this.ctx = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String XMLResult = "";
        String WebAddress;

        //Execute HTTP Request against API and return XML result
        WebAddress = ctx.getString(R.string.API_WEB_ADDRESS);
        Log.d("NexTripWearable", WebAddress + params[0]);
        HttpGet httpGet = new HttpGet(WebAddress + params[0]);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();
            XMLResult = EntityUtils.toString(httpEntity);
            Log.d("NexTripWearable", XMLResult);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return XMLResult;
    }
}
