package com.example.partb.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Util {

    /**
     * extended asynctask class to keep reference
     *
     * @param <D>
     * @param <A>
     * @param <B>
     * @param <C>
     */
    public static abstract class AsyncTask<D, A, B, C> extends android.os.AsyncTask<A, B, C> {
        protected D ref = null;

        public AsyncTask (D ref) {
            this.ref = ref;
        }
    }

    /**
     * do http post
     *
     * @param object
     */
    public static void httpPost (HttpPostObject object) {
        new AsyncTask<HttpPostObject, String, String, HashMap<String, Object>>(object) {

            @Override
            protected void onPostExecute (HashMap<String, Object> result) {
                HttpPostObject object = ref;
                if (object == null) {
                    return;
                }
                if (object.getResposeCallback() == null) {
                    return;
                }
                Log.e("httppost", "result : " + result);
                object.getResposeCallback().onHttpResponse(object, (String) result.get("result"), 1);
            }

            @Override
            protected HashMap<String, Object> doInBackground (String... arg0) {
                HttpPostObject object = ref;
                HashMap<String, Object> result = new HashMap<String, Object>();
                Log.e("postaction","do in background");
                if (object != null) {
                    // url
                    Log.e("postaction","object not null");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(object.getURL());
                    final HttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams,300000);
                    try {
                        //getPostDataString(object);
                        // params

                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                        if (object.getParams() != null) {
                            for (Entry<String, String> pairs : object.getParams().entrySet()) {
                                Log.e("doInBackground","doInBackground:" + pairs.getKey() + " : [" + pairs.getValue() + "]");
                                if (pairs.getValue() != null) {
                                    nameValuePairs.add(new BasicNameValuePair(pairs.getKey(), pairs.getValue()));
                                }
                            }
                        }

                        Log.e("postaction","url : " + object.getURL());
                        UrlEncodedFormEntity form = new UrlEncodedFormEntity(nameValuePairs);
                        form.setContentEncoding(HTTP.UTF_8);
                        form.setContentType("application/x-www-form-urlencoded");
                        httppost.setEntity(form);
                        HttpResponse response = httpclient.execute(httppost);
                        Log.e("postaction","data call back : ");
                        HttpEntity entity = response.getEntity();
                        result.put("result", EntityUtils.toString(entity, HTTP.UTF_8));
                        result.put("status", 1);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("error",e.getLocalizedMessage());
                    }
                }else{
                    Log.e("postaction","object null");
                }
                return result;
            }
        }.execute();
    }

    private static String getPostDataString(HttpPostObject object) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            if (object.getParams() != null) {
                for (Entry<String, String> pairs : object.getParams().entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        result.append("&");
                    }
                    Log.e("doInBackground","doInBackground:" + pairs.getKey() + " : [" + pairs.getValue() + "]");
                    result.append(URLEncoder.encode(pairs.getKey(), "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(pairs.getValue(), "UTF-8"));
                }
            }
            return result.toString();
        }catch(Exception e){
            return "";
        }
    }

    /**
     * check if device has internet connection
     *
     * @return
     */
    public static boolean hasConnection (Activity context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * set cached string
     *
     * @param key
     * @param value
     */
    public static void setString (Context context,String key, String value){
        if (value == null) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences("data",Context.MODE_MULTI_PROCESS);//context.getPreferences(Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    /**
     * get cache string
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString (Context context,String key, String defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences("data",Context.MODE_MULTI_PROCESS);
        return preferences.getString(key, defaultValue);
    }

    public static void removeString (Context context,String key){
        SharedPreferences preferences = context.getSharedPreferences("data",Context.MODE_MULTI_PROCESS);//context.getPreferences(Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(key);
        edit.commit();
    }
}
