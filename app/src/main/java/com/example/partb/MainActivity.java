package com.example.partb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.partb.util.HttpPostObject;
import com.example.partb.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkData();
        setContentView(R.layout.activity_main);

        this.findViewById(R.id.loginbutton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText username = (EditText) findViewById(R.id.username);
                EditText password = (EditText) findViewById(R.id.password);

                if(username!=null && password!= null){
                    String username2 = username.getText().toString();
                    String password2 = password.getText().toString();
                    callAPI(username2,password2);
                }else{
                    Toast.makeText(MainActivity.this,"Please fill in username and password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void callAPI(String username,String password){
        if(Util.hasConnection(MainActivity.this)){
            HttpPostObject.HttpResponseCallback callback =
                    new HttpPostObject.HttpResponseCallback() {
                        @Override
                        public void onHttpResponse(HttpPostObject postObject, String result, int status) {
                            if (result != null) {
                                try {

                                    JSONObject response = new JSONObject(result);
                                    resultHandler(response);
                                    //catch
                                } catch (JSONException e) {
                                    Log.e("post", "Error getdata " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                    };

            HttpPostObject post = new HttpPostObject(MainActivity.this.getString(R.string.login_api), callback, "");
            post.putParam("email",username);
            post.putParam("password", password);
            Util.httpPost(post);
        } else{
            Toast.makeText(MainActivity.this,"Please open network",Toast.LENGTH_LONG).show();
        }
    }

    private void resultHandler(JSONObject result){
        try {
            Log.e("answer", result.toString());
            JSONObject stat = result.getJSONObject("status");
            if(stat.getString("code").equals("200")) {
                Log.e("answer2", stat.getString("code"));
                Log.e("answer3", result.getString("id"));
                Log.e("answer3", result.getString("token"));
                Util.setString(this.getApplicationContext(),"userid",result.getString("id"));
                Util.setString(this.getApplicationContext(),"usertoken",result.getString("token"));
                toDataProcess();
            }else{
                Log.e("answer2",stat.getString("code"));
                Toast.makeText(MainActivity.this,stat.getString("message"),Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Log.e("error result handler",e.getLocalizedMessage());
        }
    }

    private void toDataProcess(){
        Intent intent = new Intent(MainActivity.this, DataActivity.class);
        startActivity(intent);
    }

    public void checkData(){
        try {
            Log.e("checkData","1");
            String cacheid = Util.getString(this.getApplicationContext(), "userid", null);
            String cachetoken = Util.getString(this.getApplicationContext(), "usertoken", null);
            if (cacheid != null && cachetoken != null) {
                toDataProcess();
            }
        }catch (Exception e){
            Log.e("checkdata error",e.getLocalizedMessage());
        }
    }
}
