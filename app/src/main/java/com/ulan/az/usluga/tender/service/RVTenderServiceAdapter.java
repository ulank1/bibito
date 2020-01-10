package com.ulan.az.usluga.tender.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.Profile.RedactorOrderActivity;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.helpers.DataHelper;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.service.Service;

import java.util.ArrayList;

import okhttp3.MultipartBody;


public class RVTenderServiceAdapter extends RecyclerView.Adapter<RVTenderServiceAdapter.PersonViewHolder> {
    Activity context;
    Service vse;


    public class PersonViewHolder extends RecyclerView.ViewHolder {

        TextView category;
        ImageView imageView, redactor;
        DataHelper dataHelper;
        RecyclerView mRecyclerView;


        PersonViewHolder(final View itemView) {
            super(itemView);


            imageView = (ImageView) itemView.findViewById(R.id.delete);
            category = (TextView) itemView.findViewById(R.id.category);

            dataHelper = new DataHelper(context);

            redactor = (ImageView) itemView.findViewById(R.id.redactor);

            redactor.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, RedactorOrderActivity.class);
                    intent.putExtra("service", listVse.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });


            category.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(getAdapterPosition(), listVse.get(getAdapterPosition()).getId(), context, RVTenderServiceAdapter.this);
                }
            });

            itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    final ClientApiListener listener1 = new ClientApiListener() {
                        @Override
                        public void onApiResponse(String id, final String json, final boolean isOk) {
                            Log.e("DDDsadad", json);
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isOk && json.isEmpty()) {
                                        Toast.makeText(context, "Вы предложили свою задачу", Toast.LENGTH_LONG).show();
                                        context.finish();

                                    }
                                }
                            });

                        }
                    };

                    final MultipartBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("user", "/api/v1/users/" + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, context)) + "/")
                            .addFormDataPart("last", String.valueOf(listVse.get(getAdapterPosition()).getId()))
                            .addFormDataPart("order", "/api/v1/order/" + String.valueOf(service_id + "/")).build();

                    ClientApi.requestPost(URLS.confirm_delete, req, listener1);

                }


            });


        }


    }

    public static ArrayList<Service> listVse;
    private int service_id;

    public RVTenderServiceAdapter(Activity context, ArrayList<Service> listVse, int service_id) {
        this.listVse = listVse;
        this.context = context;
        this.service_id = service_id;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = null;
        try {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my_order, viewGroup, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        vse = listVse.get(i);
        personViewHolder.category.setText(vse.getCategory());
        personViewHolder.redactor.setVisibility(View.GONE);
        personViewHolder.imageView.setVisibility(View.GONE);
       /* if (vse.is_favorite){
            Glide.with(context).load(R.drawable.ic_action_star_active).into(personViewHolder.imageView);
        }else {
            Glide.with(context).load(R.drawable.ic_action_star_none_active).into(personViewHolder.imageView);
        }*/

    }


    @Override
    public int getItemCount() {
        return listVse.size();
    }

    public static void delete(int position, int id, Context context, RVTenderServiceAdapter rvMyOrderAdapter) {
        ClientApi.requestDelete(id, context);
        DataHelper dataHelper = new DataHelper(context);
        dataHelper.delete(id);
        listVse.remove(position);
        rvMyOrderAdapter.notifyDataSetChanged();
    }

}