package com.ulan.az.usluga.Profile;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.service.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TenderOrderFragment extends Fragment {

    RecyclerView mRecyclerView;
    ProgressBar progressBar;
    ClientApiListener listener;
    ArrayList<Service> serviceArrayList;
    RVTenderServiceAdapter adapter;

    public TenderOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tender, container, false);

        mRecyclerView = view.findViewById(R.id.rv);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        progressBar = view.findViewById(R.id.progressbar);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
        listener = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {
                if (isOk) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        serviceArrayList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object1 = jsonArray.getJSONObject(i);
                            JSONObject object = object1.getJSONObject("order");
                            Service service = new Service();
                            service.setStatus(object1.getInt("status"));
                            service.setAddress(object.getString("address"));
                            if (object.has("description"))
                                service.setDescription(object.getString("description"));
                            if (!object.isNull("lat"))
                                service.setGeoPoint(new GeoPoint(object.getDouble("lat"), object.getDouble("lng")));
                            else service.setGeoPoint(new GeoPoint(0, 0));

                            service.setId(object.getInt("id"));
                            service.setImage(object.getString("image"));
                            service.setCategory(object.getJSONObject("sub_category").getJSONObject("category").getString("category")+" -> "+object.getJSONObject("sub_category").getString("sub_category"));
                            Shared.category_id_order = object.getJSONObject("sub_category").getInt("id");

                            User user = new User();
                            JSONObject jsonUser = object.getJSONObject("user");
                            user.setAge(jsonUser.getString("age"));
                            user.setImage(jsonUser.getString("image"));
                            user.setName(jsonUser.getString("name"));
                            user.setPhone(jsonUser.getString("phone"));
                            user.setDeviceId(jsonUser.getString("device_id"));

                            service.setUser(user);

                            serviceArrayList.add(service);

                        }
                        Shared.orderCategories = serviceArrayList;
                        adapter = new RVTenderServiceAdapter(getContext(), serviceArrayList);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                // things to do on the main thread
                                progressBar.setVisibility(View.GONE);
                                mRecyclerView.setAdapter(adapter);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        progressBar.setVisibility(View.VISIBLE);

            ClientApi.requestGet(URLS.confirm + "&user="+E.getAppPreferencesINT(E.APP_PREFERENCES_ID,getActivity()), listener);


        return view;
    }

}
