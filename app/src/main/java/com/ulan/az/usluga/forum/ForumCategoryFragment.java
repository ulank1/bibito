package com.ulan.az.usluga.forum;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ulan.az.usluga.Category.Category;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.FilterListener;
import com.ulan.az.usluga.Main2Activity;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.Searchlistener;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.service.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForumCategoryFragment extends Fragment implements FilterListener, Searchlistener {

    int tag;
    RecyclerView mRecyclerView;

    ArrayList<Category> serviceArrayList;
    RVForumCategoryAdapter adapter;

    public ForumCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
      /*  DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                llm.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
*/
        ClientApiListener listener = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {
                //Log.e("SSSS", "SSSS");
                if (isOk) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        serviceArrayList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Category category = new Category();
                            category.setCategory(object.getString("category"));
                            category.setId(object.getInt("id"));
                            serviceArrayList.add(category);
                        }
                        //Log.e("size", serviceArrayList.size() + "");
                        Shared.forumCategories  =serviceArrayList;
                        adapter = new RVForumCategoryAdapter(getActivity(), serviceArrayList, 1);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                // things to do on the main thread

                                mRecyclerView.setAdapter(adapter);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

            }
        };

        ClientApi.requestGet(URLS.forum_category, listener);

        Main2Activity activity = (Main2Activity) getActivity();

        activity.setForumApiListener(this);

        return view;
    }

    @Override
    public void onFilter(int id) {
/*
            adapter = new RVForumCategoryAdapter(getActivity(), categories, 1);

            mRecyclerView.setAdapter(adapter);*/
    }

    @Override
    public void onSearch(ArrayList<Service> services) {
        Log.e("DDSS",Shared.forumCategories_search.size()+"");
        if (Shared.forumCategories_search.size()>-1) {
            adapter = new RVForumCategoryAdapter(getActivity(), Shared.forumCategories_search, 1);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // things to do on the main thread

                    mRecyclerView.setAdapter(adapter);
                }
            });
        }
    }
}
