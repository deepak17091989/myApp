package com.example.myapp;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.myapp.util.VolleySingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Deepak Kumar on 10/3/2015.
 */
public class MapActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
    ArrayList<LatLng> latLngsArray=new ArrayList<>();
    Timer timer = new Timer();
    private GoogleMap mMap;
    LatLng loc;
    StringBuilder stringCoOrdinates=new StringBuilder();
    private static int flag=0;
    private final int TIME_INTERVAL = 20000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maplayout);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mMap.setOnMyLocationChangeListener(myLocationChangeListener);

            }
        }, 0, TIME_INTERVAL);

    }
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            loc = new LatLng(location.getLatitude(), location.getLongitude());
            if(mMap != null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                if(flag==0)
                 stringCoOrdinates.append(location.getLatitude()+","+location.getLongitude());
                else {
                    stringCoOrdinates.append("|" + location.getLatitude() + "," + location.getLongitude());
                    for(int i=0;i<latLngsArray.size();i++)
                    {
                        mMap.clear();
                        Marker mMarker = mMap.addMarker(new MarkerOptions().position(latLngsArray.get(i)));
                    }
                    flag = 1;
                }

            }
        }
    };
    private void getSmoothCurve(){
        RequestQueue queue=new VolleySingleton().getInstance().getRequestQueue();

        JsonObjectRequest hitRequest=new JsonObjectRequest(Request.Method.GET, "https://roads.googleapis.com/v1/snapToRoads?path="+stringCoOrdinates+"&interpolate=true&key=AIzaSyCWPOS0eeoGnii9-PJBs_OBxRDEaFuAY8w", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                        JSONArray snappedPoints=response.getJSONArray("snappedPoints");
                        JSONObject snappedPointsObj=snappedPoints.getJSONObject(0);
                        JSONObject locationObj=snappedPointsObj.getJSONObject("location");
                        Double latitude= Double.parseDouble(locationObj.getString("latitude"));
                        Double longitude=Double.parseDouble(locationObj.getString("longitude"));
                        LatLng latLang=new LatLng(latitude,longitude);
                        latLngsArray.add(latLang);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(hitRequest);
    }
    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    public void startLOCUpdates(){

    }
}
