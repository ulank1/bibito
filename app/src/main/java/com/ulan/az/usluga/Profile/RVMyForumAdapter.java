package com.ulan.az.usluga.Profile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.forum.Forum;
import com.ulan.az.usluga.forum.ForumInfoActivity;
import com.ulan.az.usluga.helpers.DataHelper;

import java.util.ArrayList;


public class RVMyForumAdapter extends RecyclerView.Adapter<RVMyForumAdapter.PersonViewHolder> {
    Context context;
    Forum vse;



    public class PersonViewHolder extends RecyclerView.ViewHolder {

       TextView category;
        TextView countText;
       ImageView imageView,redactor;
       DataHelper dataHelper;
       RecyclerView mRecyclerView;
       LinearLayout line2;
        ClientApiListener listener;

        PersonViewHolder(final View itemView) {
            super(itemView);


            imageView = (ImageView) itemView.findViewById(R.id.delete);
            category = (TextView) itemView.findViewById(R.id.category);
            countText = (TextView) itemView.findViewById(R.id.count);
            line2 = (LinearLayout) itemView.findViewById(R.id.line2);

            dataHelper = new DataHelper(context);

            redactor = (ImageView) itemView.findViewById(R.id.redactor);

            redactor.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,RedactorForumActivity.class);
                    intent.putExtra("service",listVse.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ForumInfoActivity.class);
                    intent.putExtra("forum", listVse.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });


            category.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(getAdapterPosition(),listVse.get(getAdapterPosition()).getId(),context,RVMyForumAdapter.this);
                }
            });
            listener = new ClientApiListener() {
                @Override
                public void onApiResponse(String id, String json, boolean isOk) {
                    countText.setText(id);
                }
            };
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.rv);

            LinearLayoutManager llm = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(llm);
            mRecyclerView.setHasFixedSize(true);




        }


    }

    public static ArrayList<Forum> listVse;
    ArrayList<ArrayList<User>> users;

    public RVMyForumAdapter(Context context, ArrayList<Forum> listVse, ArrayList<ArrayList<User>> users) {
        this.listVse = listVse;
        this.context = context;
        this.users = users;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = null;
        try {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my_forum, viewGroup, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        vse = listVse.get(i);
        Log.e("users",users.get(i).size()+"");
        personViewHolder.category.setText(vse.getTitle());
        if (vse.getCount()>0) {
            personViewHolder.countText.setText(vse.getCount()+"");
            RVMyForumSecondAdapter adapter = new RVMyForumSecondAdapter(context, users.get(i), vse.getId(), i, this, vse.getCount(),personViewHolder.listener);
            personViewHolder.mRecyclerView.setAdapter(adapter);
        }else {
            personViewHolder.line2.setVisibility(View.GONE);
        }

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

    public static void delete(int position, int id, Context context, RVMyForumAdapter rvMyOrderAdapter){
        ClientApi.requestDeleteForum(id,context);
        DataHelper dataHelper = new DataHelper(context);
        dataHelper.delete(id);
        listVse.remove(position);
        rvMyOrderAdapter.notifyDataSetChanged();
    }



}