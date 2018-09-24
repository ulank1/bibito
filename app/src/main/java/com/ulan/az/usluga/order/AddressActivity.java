package com.ulan.az.usluga.order;

import android.content.Intent;
import android.location.Address;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ulan.az.usluga.R;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.IOException;

public class AddressActivity extends AppCompatActivity {
    MapView mapView;
    EditText address;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        address = findViewById(R.id.address);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getController().setCenter(new GeoPoint(42.8629, 74.6059));

        mapView.setMapListener(new DelayedMapListener(new MapListener() {
            public boolean onZoom(final ZoomEvent e) {


                return true;
            }

            public boolean onScroll(final ScrollEvent e) {
                MapView mapView = (MapView) findViewById(R.id.mapView);

                reverseGeocoding(mapView.getMapCenter().getLatitude(),mapView.getMapCenter().getLongitude());

                return true;
            }
        }, 1000));


    }

    public void reverseGeocoding(final double lat, final double lon) {
        progressBar.setVisibility(View.VISIBLE);
        address.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                GeocoderNominatim geocoder = new GeocoderNominatim("ulankarimovv@gmail.com");
                try {
                    if (geocoder.getFromLocation(lat, lon,1).size()>0) {
                        Address adress = geocoder.getFromLocation(lat, lon, 1).get(0);
                        ////////Log.e("Cityy", adress.toString());
                        String saddress = adress.toString();
                        String[] s = saddress.split(",");
                        for (int i = 0; i < s.length; i++) {
                            ////////Log.e("City" + i, s[i]);
                        }
                        int indexOfDisplayNAme = 20;
                        String addressFull;
                        String[] number;
                        for (int i=15;i<25;i++){
                            if (s[i].contains("display_name=")){
                                indexOfDisplayNAme=i;
                            }
                        }
                        number=s[indexOfDisplayNAme].split("=");
                        addressFull=number[1];
                        for (int i=indexOfDisplayNAme+1;i<27;i++){
                            if (s[i].trim().contains("Бишкек")){
                                break;
                            }else addressFull=addressFull+","+s[i];
                        }
                        final String finalAddressFull = addressFull;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                address.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                address.setText(finalAddressFull);
                            }
                        });


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // получим идентификатор выбранного пункта меню
        int id = item.getItemId();
        if (id==R.id.action_search) {
            if (progressBar.getVisibility() == View.GONE && !address.getText().toString().isEmpty()) {

                Intent intent = new Intent();
                intent.putExtra("address", address.getText().toString());
                intent.putExtra("lat", mapView.getMapCenter().getLatitude());
                intent.putExtra("lon", mapView.getMapCenter().getLongitude());
                setResult(RESULT_OK, intent);
                finish();

            } else {

                Toast.makeText(this, "Подождите!", Toast.LENGTH_SHORT).show();

            }
        }else {
            finish();
        }

        return true;
    }



}
