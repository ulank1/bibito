package com.ulan.az.usluga.order;


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
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.service.Service;

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
public class MapOrderFragment extends Fragment {

    MapView mapView;

    ArrayList<Service> serviceArrayList;

    public MapOrderFragment() {
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

      /*  String json = "{\"meta\": {\"limit\": 0, \"offset\": 0, \"total_count\": 1}, \"objects\": [{\"address\": \"dsfsdf\", \"category\": {\"category\": \"dsfs\", \"id\": 1, \"resource_uri\": \"/api/v1/category/1/\"}, \"experience\": 5.0, \"id\": 2, \"image\": \"/media/images/5.8.0_yd5LyuC.PNG\", \"lat\": 42.8675, \"lng\": 74.5876, \"resource_uri\": \"/api/v1/service/2/\", \"user\": {\"age\": \"233...5555\", \"created_at\": \"2018-07-28T11:15:40.985000\", \"id\": 1, \"image\": \"/media/images/5.8.0.PNG\", \"name\": \"sdfsdfsdf\", \"phone\": \"+996701991855\", \"resource_uri\": \"/api/v1/users/1/\", \"updated_at\": \"2018-07-28T11:15:40.985000\"}}]}";
        try {
            JSONObject jsonObject = new JSONObject(json);
            serviceArrayList = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("objects");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Service service = new Service();
                service.setAddress(object.getString("address"));
                service.setGeoPoint(new GeoPoint(object.getDouble("lat"), object.getDouble("lng")));
                service.setImage(object.getString("image"));
                service.setCategory(object.getJSONObject("category").getString("category"));
                User user = new User();
                JSONObject jsonUser = object.getJSONObject("user");
                user.setAge(jsonUser.getString("age"));
                user.setImage(jsonUser.getString("image"));
                user.setName(jsonUser.getString("name"));
                user.setPhone(jsonUser.getString("phone"));
                service.setUser(user);
                serviceArrayList.add(service);

                addMarker(service.getGeoPoint(),i);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/

        ClientApiListener listener = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json,boolean isOk) {
                if (isOk) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        serviceArrayList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            final Service service = new Service();
                            service.setAddress(object.getString("address"));
                            service.setGeoPoint(new GeoPoint(object.getDouble("lat"), object.getDouble("lng")));
                            service.setImage(object.getString("image"));
                            if (object.has("description"))
                            service.setDescription(object.getString("description"));
                            service.setId(object.getInt("id"));
                            service.setCategory(object.getJSONObject("sub_category").getString("sub_category"));
                            User user = new User();
                            JSONObject jsonUser = object.getJSONObject("user");
                            user.setAge(jsonUser.getString("age"));
                            user.setImage(jsonUser.getString("image"));
                            user.setName(jsonUser.getString("name"));
                            user.setPhone(jsonUser.getString("phone"));
                            user.setDeviceId(jsonUser.getString("device_id"));

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
         ClientApi.requestGet(URLS.order+"&status=1&sub_category="+ Shared.category_id,listener);


    return view;
    }

    public void addMarker(GeoPoint geoPoint,int index) {

        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(geoPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        ServiceInfoWindow serviceInfoWindow = new ServiceInfoWindow(R.layout.info_order,mapView,index);
        startMarker.setInfoWindow(serviceInfoWindow);
        //startMarker.showInfoWindow();
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_location));
        mapView.getOverlays().add(startMarker);
        mapView.invalidate();
    }


    public class ServiceInfoWindow extends InfoWindow{

        int index;

        public ServiceInfoWindow(int layoutResId, MapView mapView,int index) {
            super(layoutResId, mapView);
            this.index = index;
        }

        @Override
        public void onOpen(Object item) {

            TextView name =mView.findViewById(R.id.name);
            TextView phone =mView.findViewById(R.id.phone);
            TextView btn =mView.findViewById(R.id.btn);
            ImageView clear = mView.findViewById(R.id.clear);

            final Service service = serviceArrayList.get(index);
            name.setText(service.getUser().getName());
            phone.setText(service.getUser().getPhone());

            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    close();
                }
            });

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   startActivity(new Intent(getActivity(),OrderMoreInfoActivity.class).putExtra("service",service));

                }
            });

        }

        @Override
        public void onClose() {

        }
    }

}
