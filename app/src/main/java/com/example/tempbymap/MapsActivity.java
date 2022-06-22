package com.example.tempbymap;

import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.tempbymap.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONObject;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private FusedLocationProviderClient client;
    private SupportMapFragment mapFragment;
    private ConnectivityManager manager;
    private NetworkInfo networkInfo;
    private Geocoder geocoder;
    private double selectedLat, selectedLng;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        client = LocationServices.getFusedLocationProviderClient(this);


        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(17.966472, 79.488017);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Warangal"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                selectedLat = latLng.latitude;
                selectedLng = latLng.longitude;
                String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + selectedLat + "&lon=" + selectedLng + "&appid=0a3ef48eb27734ca0632f59111180451&units=metric";
                RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String responseText = response.toString();
                                try
                                {
                                    JSONObject json_LL = response.getJSONObject("main");
                                    String temp = json_LL.getString("temp");
                                    Toast.makeText(MapsActivity.this, "Temperature: " + temp, Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Rest Response", error.toString());
                            }
                        }
                );
                requestQueue.add(objectRequest);
//                Toast.makeText(this,  url, Toast.LENGTH_SHORT).show();
            }
        });
    }
//    private void GetAddress(double mlat, double mlng)
//    {
//        geocoder = new Geocoder(this, Locale.getDefault());
//    }
}