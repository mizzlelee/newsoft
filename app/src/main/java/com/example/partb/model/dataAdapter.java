package com.example.partb.model;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.partb.MainActivity;
import com.example.partb.R;
import com.example.partb.util.HttpPostObject;
import com.example.partb.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class dataAdapter extends RecyclerView.Adapter<dataAdapter.MyViewHodler> {

    private final Context context;
    private ArrayList<String> data;

    public dataAdapter(Context context, ArrayList data){
        this.context = context;
        this.data = data;
    }
    @NonNull
    @Override
    public MyViewHodler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = View.inflate(context, R.layout.item_recycler,null);
        return new MyViewHodler(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHodler myViewHodler, int i) {
        try {
            JSONObject dat = new JSONObject(data.get(i));
            Log.e("bind data",dat.getString("list_name"));
            myViewHodler.name.setText(dat.getString("list_name"));
            myViewHodler.distance.setText(dat.getString("distance"));
        }catch (Exception e){
            Log.e("binf error",e.getLocalizedMessage());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHodler extends ViewHolder{
        private TextView name,distance;
        public MyViewHodler(View itemView){
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.text_name);
            distance = (TextView) itemView.findViewById(R.id.text_distance);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"Data== " + data.get(getLayoutPosition()),Toast.LENGTH_LONG).show();
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        JSONObject dat = new JSONObject(data.get(getLayoutPosition()));
                        dialog(dat.getString("id"), dat.getString("list_name"), dat.getString("distance"));
                        return true;
                    }catch (Exception e){
                        Log.e("longclick",e.getLocalizedMessage());
                        return false;
                    }
                }
            });
        }
    }

    private void dialog(final String userid, String name, final String distance){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("OLD DATA== id: " + userid + " name: " + name + "\n" + " cause time issue will pre-set the update data" +"\n" + "UPDATE DATA== id: " + userid + " name: zzzzz")
                .setTitle("Update data");

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                updateData(userid,"zzzzz",distance);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateData(String id,String name,String distance){
            HttpPostObject.HttpResponseCallback callback =
                    new HttpPostObject.HttpResponseCallback() {
                        @Override
                        public void onHttpResponse(HttpPostObject postObject, String result, int status) {
                            if (result != null) {
                                try {
                                    JSONObject response = new JSONObject(result);
                                    Log.e("update return",response.toString());
                                    Toast.makeText(context,"API RETURN DATA== " + response.toString(),Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    Log.e("post", "Error getdata " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                    };

            HttpPostObject post = new HttpPostObject(context.getString(R.string.listing_update_api), callback, "");
            post.putParam("id",Util.getString(context,"userid",null));
            post.putParam("token", Util.getString(context,"usertoken",null));
            post.putParam("listing_id", id);
            post.putParam("listing_name", name);
            post.putParam("distance", distance);
            Util.httpPost(post);
    }

}
