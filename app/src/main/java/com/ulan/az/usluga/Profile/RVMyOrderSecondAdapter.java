package com.ulan.az.usluga.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.order.OrderMoreInfoActivity;
import com.ulan.az.usluga.service.RVServiceAdapter;
import com.ulan.az.usluga.service.Service;
import com.ulan.az.usluga.service.ServiceMoreInfoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RVMyOrderSecondAdapter extends RecyclerView.Adapter<RVMyOrderSecondAdapter.PersonViewHolder> {
    Context context;
    User vse;


    public class PersonViewHolder extends RecyclerView.ViewHolder {

       TextView name,phone;
       ImageView check,cancel;
       ProgressBar progressBar;


        PersonViewHolder(final View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.phone);
            check = (ImageView) itemView.findViewById(R.id.check);
            cancel = (ImageView) itemView.findViewById(R.id.cancel);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);

            itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                   ClientApiListener listener = new ClientApiListener() {
                        @Override
                        public void onApiResponse(String id, String json, boolean isOk) {
                            Log.e("OOO",json);
                            if (isOk) {
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    JSONArray jsonArray = jsonObject.getJSONArray("objects");
                                        boolean bool = true;
                                        JSONObject object = jsonArray.getJSONObject(0);
                                        Service service = new Service();
                                        service.setAddress(object.getString("address"));
                                        if (!object.isNull("experience"))
                                            service.setExperience(object.getDouble("experience"));
                                        if (!object.isNull("lat"))
                                            service.setGeoPoint(new GeoPoint(object.getDouble("lat"), object.getDouble("lng")));
                                        else service.setGeoPoint(new GeoPoint(0, 0));
                                        service.setImage(object.getString("image"));
                                        if (object.has("description"))
                                            service.setDescription(object.getString("description"));
                                        service.setCategory(object.getJSONObject("sub_category").getJSONObject("category").getString("category")+" -> "+object.getJSONObject("sub_category").getString("sub_category"));
                                        service.setId(object.getInt("id"));
                                        User user = new User();
                                        JSONObject jsonUser = object.getJSONObject("user");
                                        user.setAge(jsonUser.getString("age"));
                                        user.setImage(jsonUser.getString("image"));
                                        user.setName(jsonUser.getString("name"));
                                        user.setPhone(jsonUser.getString("phone"));
                                        user.setId(jsonUser.getInt("id"));
                                        user.setDeviceId(jsonUser.getString("device_id"));

                                        service.setUser(user);
                                    context.startActivity(new Intent(context,ServiceMoreInfoActivity.class).putExtra("service",service));


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    Log.e("OOOii",listVse.get(getAdapterPosition()).getId()+"");
                    ClientApi.requestGet(URLS.services+"&user="+listVse.get(getAdapterPosition()).getId(),listener);



                }


            });

            check.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                 //   RVMyOrderAdapter.delete(position,id,context,rvMyOrderAdapter);
                    ClientApi.requesPutStatus(URLS.confirm_delete+listVse.get(getAdapterPosition()).getId_confirm()+"/",0);
                    send(getAdapterPosition());
                    listVse.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

            cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClientApi.requesPutStatus(URLS.confirm_delete+listVse.get(getAdapterPosition()).getId_confirm()+"/",2);
                    listVse.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

        }

    }

    ArrayList<User> listVse;
    int id;
    int position;
    RVMyOrderAdapter rvMyOrderAdapter;

    public RVMyOrderSecondAdapter(Context context, ArrayList<User> listVse,int id,int position,RVMyOrderAdapter rvMyOrderAdapter) {
        this.listVse = listVse;
        this.context = context;
        this.id = id;
        this.position = position;
        this.rvMyOrderAdapter = rvMyOrderAdapter;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = null;
        try {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my_order_second, viewGroup, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        vse = listVse.get(i);
        personViewHolder.name.setText(vse.getName());
        personViewHolder.phone.setText(vse.getPhone());
    }

    @Override
    public int getItemCount() {
        return listVse.size();
    }


    public String getJson(int position) {
        JSONObject jsonObject = new JSONObject();
        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            notification.put("title", "Задачи");
            notification.put("body", "принял предложение");
            notification.put("id", 1);
            data.put("title", "Задачи");
            data.put("body", "принял предложение");
            data.put("id", 1);
            jsonObject.put("notification", notification);
            jsonObject.put("data", data);
            jsonObject.put("to", listVse.get(position).getDeviceId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public void send(int position) {

        OkHttpClient client = new OkHttpClient();


        RequestBody body = RequestBody.create(JSON, getJson(position));
        Request request = new Request.Builder()
                .header("Authorization", Shared.key)
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Log.e("Error", e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                //Log.e("RESPONSE_Finish", json);


            }
        });
    }

}