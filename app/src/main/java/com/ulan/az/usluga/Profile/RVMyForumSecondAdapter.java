package com.ulan.az.usluga.Profile;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.Shared;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RVMyForumSecondAdapter extends RecyclerView.Adapter<RVMyForumSecondAdapter.PersonViewHolder> {
    Context context;
    User vse;


    public class PersonViewHolder extends RecyclerView.ViewHolder {

       TextView name,phone;
       ImageView check,cancel;


        PersonViewHolder(final View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.phone);
            check = (ImageView) itemView.findViewById(R.id.check);
            cancel = (ImageView) itemView.findViewById(R.id.cancel);

            itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                //context.startActivity(new Intent(context,OrderMoreInfoActivity.class).putExtra("service",listVse.get(getAdapterPosition())));

                }


            });

            check.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    count--;
                    if (count>0){
                        ClientApi.requestPutForum(count,listVse.get(getAdapterPosition()).getId(),context);
                        listener.onApiResponse(count+"",null,true);
                    }else {
                        RVMyForumAdapter.delete(position, id, context, rvMyOrderAdapter);
                    }
                    ClientApi.requestDeleteConfirmation(listVse.get(getAdapterPosition()).getId(), context);
                    send(getAdapterPosition());
                    listVse.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

            cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClientApi.requestDelete1(URLS.confirmation_delete,listVse.get(getAdapterPosition()).getId(),context);
                    listVse.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

        }

    }

    ArrayList<User> listVse;
    int id;
    int position;
    int count;
    RVMyForumAdapter rvMyOrderAdapter;
    ClientApiListener listener;

    public RVMyForumSecondAdapter(Context context, ArrayList<User> listVse, int id, int position, RVMyForumAdapter rvMyOrderAdapter,int count,ClientApiListener clientApiListener) {
        this.listVse = listVse;
        this.context = context;
        this.id = id;
        this.position = position;
        this.rvMyOrderAdapter = rvMyOrderAdapter;
        this.count = count;
        listener = clientApiListener;
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
        Log.e("vse",vse.getName());
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
            notification.put("title", "Форум");
            notification.put("body", "забронировано");
            notification.put("id", 1);

            jsonObject.put("notification", notification);
            jsonObject.put("data", notification);
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