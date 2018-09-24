package com.ulan.az.usluga.service;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.FilterListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.Shared;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements FilterListener {

    MapView mapView;

    ArrayList<Service> serviceArrayList;
    ClientApiListener listener;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getController().setCenter(new GeoPoint(42.8629, 74.6059));



        listener = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {
                if (isOk) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        serviceArrayList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            final Service service = new Service();
                            service.setAddress(object.getString("address"));
                            service.setExperience(object.getDouble("experience"));
                            service.setGeoPoint(new GeoPoint(object.getDouble("lat"), object.getDouble("lng")));
                            service.setImage(object.getString("image"));
                            if (object.has("description"))
                                service.setDescription(object.getString("description"));
                            service.setCategory(object.getJSONObject("sub_category").getString("sub_category"));
                            User user = new User();
                            JSONObject jsonUser = object.getJSONObject("user");
                            user.setAge(jsonUser.getString("age"));
                            user.setImage(jsonUser.getString("image"));
                            user.setName(jsonUser.getString("name"));
                            user.setPhone(jsonUser.getString("phone"));
                            service.setUser(user);
                            serviceArrayList.add(service);
                            final int finalI = i;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    addMarker(service.getGeoPoint(), finalI);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        ClientApi.requestGet(URLS.services + "&sub_category=" + Shared.category_id, listener);


        return view;
    }

    public void addMarker(GeoPoint geoPoint, int index) {

        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(geoPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        ServiceInfoWindow serviceInfoWindow = new ServiceInfoWindow(R.layout.info_service, mapView, index);
        startMarker.setInfoWindow(serviceInfoWindow);
        //startMarker.showInfoWindow();
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_location));
        mapView.getOverlays().add(startMarker);
        mapView.invalidate();
    }

    @Override
    public void onFilter(int id) {
        ClientApi.requestGet(URLS.services + "&sub_category=" + Shared.category_id, listener);
    }


    public class ServiceInfoWindow extends InfoWindow {

        int index;

        public ServiceInfoWindow(int layoutResId, MapView mapView, int index) {
            super(layoutResId, mapView);
            this.index = index;
        }

        @Override
        public void onOpen(Object item) {

            TextView name = mView.findViewById(R.id.name);
            TextView phone = mView.findViewById(R.id.phone);
            TextView btn = mView.findViewById(R.id.btn);
            TextView experience = mView.findViewById(R.id.experience);
            ImageView clear = mView.findViewById(R.id.clear);

            final Service service = serviceArrayList.get(index);
            name.setText(service.getUser().getName());
            phone.setText(service.getUser().getPhone());
            experience.setText(service.getExperience() + "");

            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    close();
                }
            });
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), ServiceMoreInfoActivity.class).putExtra("service", service));

                }
            });


        }

        @Override
        public void onClose() {

        }
    }

}
