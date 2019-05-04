package com.example.partb;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.partb.model.dataAdapter;
import com.example.partb.util.HttpPostObject;
import com.example.partb.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataActivity extends AppCompatActivity {
    private RecyclerView recycleview;
    private Button logout;
    private dataAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("data","onCreate");
        Log.e("data2",Util.getString(this.getApplicationContext(),"usertoken",null));
        setContentView(R.layout.activity_data);
        logout = (Button) findViewById(R.id.btn_logout);
        recycleview = (RecyclerView) findViewById(R.id.recycleview);

        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.e("logout","1");
                try {
                    Util.removeString(DataActivity.this.getApplicationContext(), "usertoken");
                    Util.removeString(DataActivity.this.getApplicationContext(), "userid");
                    Intent intent = new Intent(DataActivity.this, MainActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    Log.e("logout error",e.getLocalizedMessage());
                }
            }
        });

        getData();
    }

    private void checkData(){
        try {
            String cacheData = Util.getString(DataActivity.this, "listdata", null);
            if (cacheData != null) {
                processData(cacheData);
            }
        }catch (Exception e){
            Log.e("checkdata error",e.getLocalizedMessage());
        }
    }

    private void getData(){
        if(Util.hasConnection(DataActivity.this)){
            HttpPostObject.HttpResponseCallback callback =
                    new HttpPostObject.HttpResponseCallback() {
                        @Override
                        public void onHttpResponse(HttpPostObject postObject, String result, int status) {
                            if (result != null) {
                                try {

                                    JSONObject response = new JSONObject(result);
                                    Log.e("data return",response.toString());
                                    Util.setString(DataActivity.this.getApplicationContext(),"listdata",response.toString());
                                    processData(response.toString());
                                    //catch
                                } catch (JSONException e) {
                                    Log.e("post", "Error getdata " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                    };

            String url = DataActivity.this.getString(R.string.listing_api) + "?id=" + Util.getString(this.getApplicationContext(),"userid",null) + "&token=" + Util.getString(this.getApplicationContext(),"usertoken",null);
            HttpPostObject post = new HttpPostObject(url, callback, "");
            Util.httpPost(post);
        } else{
            Toast.makeText(DataActivity.this,"Please open network",Toast.LENGTH_LONG).show();
        }
    }

    private void processData(String listdata){
        try {
            JSONObject data = new JSONObject(listdata);
            Log.e("process data",data.toString());
            JSONObject stat = data.getJSONObject("status");
            if(stat.getString("code").equals("200")) {
                Log.e("listing","start");
                JSONArray listing = data.getJSONArray("listing");
                ArrayList<String> list = new ArrayList<String>();
                if (listing != null) {
                    for (int i=0;i<listing.length();i++){
                        list.add(listing.getString(i));
                        Log.e("list etc",listing.getString(i).toString());
                    }
                }
                Log.e("arraylist",list.toString());
                //ArrayList aaa = new ArrayList();
                adapter = new dataAdapter(DataActivity.this,list);
                recycleview.setAdapter(adapter);

                recycleview.setLayoutManager(new LinearLayoutManager(DataActivity.this,LinearLayoutManager.VERTICAL,false));
                Log.e("listing",listing.toString());
            }else{
                Toast.makeText(DataActivity.this,stat.getString("message"),Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Log.e("listing error",e.getLocalizedMessage());
        }
    }
}
