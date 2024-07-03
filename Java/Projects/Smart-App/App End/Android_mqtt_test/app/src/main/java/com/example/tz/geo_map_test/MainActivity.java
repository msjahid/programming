package com.example.tz.geo_map_test;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.example.tz.geo_map_test.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mGoogleMap;
    Marker marker;
    LocationRequest locationRequest;
    GoogleApiClient mGoogleApiClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    EditText text;
    String clientId;
    final static String host = "tcp://192.168.0.1:1883";
    String lat;
    String lng;
    final int random = new Random().nextInt(100);
    String bus = "Bus_no :"+random;
	public String testtopic = "Group12-SSD/processedGPSdata";
    TextView Distance;
    TextView Duration;
    TextView Start_add;
    TextView Destination_add;
    ArrayList<LatLng> MarkerPoints;
    Switch traffic;
    private BleDevice bleDevice;
    public DeviceAdapter mDeviceAdapter;
    private ProgressDialog progressDialog;
    String blemac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
            Toast.makeText(this, "Loading", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);
            initMap();

        } else {
            Toast.makeText(this, "Install Google Play Service!!!", Toast.LENGTH_LONG).show();
        }

        MarkerPoints = new ArrayList<>();
        ToggleButton toggle = (ToggleButton) findViewById(R.id.trafficButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    mGoogleMap.setTrafficEnabled(true);
                } else {
                    // The toggle is disabled
                    mGoogleMap.setTrafficEnabled(false);
                }
            }
        });
        //kitahoinaken();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startScan();
        //kitahoinaken();
        Toast.makeText(this, "logged on", Toast.LENGTH_SHORT).show();
    }

    private boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable,0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cant Connect to Play Service", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            askForLocationPermissions();
        } else {
            //do your work
        }

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_json);
        googleMap.setMapStyle(style);


        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                // Already two locations
                if (MarkerPoints.size() > 1) {
                    MarkerPoints.clear();
                    mGoogleMap.clear();
                }

                // Adding new item to the ArrayList
                MarkerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (MarkerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (MarkerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }


                // Add new marker to the Google Map Android API V2
                mGoogleMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (MarkerPoints.size() >= 2) {
                    LatLng origin = MarkerPoints.get(0);
                    LatLng dest = MarkerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getUrl(origin, dest);
                    Log.d("onMapClick", url.toString());
                    MainActivity.FetchUrl FetchUrl = new FetchUrl();

                    // Start downloading json data from Google Directions API
                    FetchUrl.execute(url);
                    //move map camera
                    //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                    //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                }

            }
        });
    }

    private void askForLocationPermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Location permessions needed")
                    .setMessage("you need to allow this permission!")
                    .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
//                                        //Do nothing
                        }
                    })
                    .show();

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);

            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(80000);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(this, "Perfect!!!", Toast.LENGTH_LONG).show();
        } else {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            lat = Double.toString(location.getLatitude());
            lng = Double.toString(location.getLongitude());
            //CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
            //mGoogleMap.animateCamera(update);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            setMarker(ll);
        }

       // LocationProcessing();
        //rssiCheck();
        startScan();
        //kitahoinaken();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 10000);
    }


    private void setMarker(LatLng ll) {

        if (marker != null) {
            marker.remove();
        }
        MarkerOptions options = new MarkerOptions().title(String.valueOf("Your Location")).icon(BitmapDescriptorFactory.fromResource(R.drawable.perlocation)).position(ll);
        marker = mGoogleMap.addMarker(options);
        marker.showInfoWindow();


    }

//
//    public void rssiCheck()
//    {
//        Intent myIntent = getIntent();
//        String str = myIntent.getStringExtra("bleID");
//        int tasker = myIntent.getIntExtra("int",-1);
//        int Rssiread = myIntent.getIntExtra("RssiValue", -1);
//        Log.d("downloadUrl", String.valueOf(Rssiread));
//        int data = Integer.valueOf(Rssiread);
//        if (data > -100)
//        {
//            LocationProcessing();
//            Toast.makeText(this, "Location Sending", Toast.LENGTH_SHORT).show();
//
//        }
//        else
//        {
//            NoLocationProcessing();
//            Toast.makeText(this, "Location Not Sending", Toast.LENGTH_SHORT).show();
//        }
//
//    }

//    public void kitahoinaken()
//    {
//        Timer timer = new Timer();
//        Toast.makeText(this, "haaahaaahaaa", Toast.LENGTH_SHORT).show();
//        timer.scheduleAtFixedRate(new TimerTask() {
//
//            @Override
//            public void run() {
//                startScan();
//
//            }
//
//        },20000,60000);
//    }

    private void startScan() {
        mDeviceAdapter = new DeviceAdapter(this);
        BleManager.getInstance().init(getApplication());
        BleManager bleManager = BleManager.getInstance()
                .enableLog(true)
                .setMaxConnectCount(7)
                .setOperateTimeout(500000);

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                mDeviceAdapter.clearScanDevice();
                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();

                if(bleDevice.getMac().contains("00:18:e4:40:00:06"))
                {
                    int value = bleDevice.getRssi();
                    blemac = "AC:23:3F:A0:04:5B";
                    Toast.makeText(MainActivity.this, "S1 Found", Toast.LENGTH_SHORT).show();
//                    Intent myIntent = new Intent(ScanActivity.this, MainActivity.class);
//                    myIntent.putExtra("bleID","AC:23:3F:A0:04:5B");
//                    myIntent.putExtra("RssiValue", value);
//                    myIntent.putExtra("int",92);
//                    ScanActivity.this.startActivity(myIntent);
                    LocationProcessing();
                }

                else if (bleDevice.getMac().contains("88:4A:EA:84:03:DB"))
                {
                    Toast.makeText(MainActivity.this, "HC Found", Toast.LENGTH_SHORT).show();
                    //Intent myIntent = new Intent(ScanActivity.this, MainActivity.class);
                    //myIntent.putExtra("bleID","88:4A:EA:84:03:DB");
                    //myIntent.putExtra("int",92);
                    //ScanActivity.this.startActivity(myIntent);
                    blemac = "88:4A:EA:84:03:DB";
                    LocationProcessing();

                }
                else if(bleDevice.getMac().contains("58:A8:39:00:85:A3"))
                {
                    Toast.makeText(MainActivity.this, "Edison Found", Toast.LENGTH_SHORT).show();
//                    Intent myIntent = new Intent(ScanActivity.this, MainActivity.class);
//                    myIntent.putExtra("bleID","58:A8:39:00:85:A3");
//                    myIntent.putExtra("int",92);
//                    ScanActivity.this.startActivity(myIntent);
                    blemac = "58:A8:39:00:85:A3";
                }

                else if(bleDevice.getMac().contains("00:10:18:01:9D:AF"))
                {
                    Toast.makeText(MainActivity.this, "Wicked found and shanti", Toast.LENGTH_SHORT).show();
//                    Intent myIntent = new Intent(ScanActivity.this, MainActivity.class);
//                    myIntent.putExtra("bleID","58:A8:39:00:85:A3");
//                    myIntent.putExtra("int",92);
//                    ScanActivity.this.startActivity(myIntent);
                    blemac= "00:10:18:01:9D:AF";
                    LocationProcessing();
                }

                else
                {
                    NoLocationProcessing();

                }


            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

            }
        });
    }

    public void LocationProcessing() {


        clientId = MqttClient.generateClientId();

      final MqttAndroidClient client = new MqttAndroidClient(this.getApplicationContext(), host,
                clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //Toast.makeText(MainActivity.this, "Client Connected", Toast.LENGTH_SHORT).show();
                    try {
                        client.subscribe(testtopic, 0);
                       // final BleActivity bleActivity = new BleActivity();

                        Timer timer = new Timer();
                        //timer.scheduleAtFixedRate(new TimerTask() {
                            //@Override
                            //public void run() {
                                //Intent myIntent = getIntent();
                                //String str = myIntent.getStringExtra("bleID");
                                //int tasker = myIntent.getIntExtra("int",-1);
                                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                                //if (tasker == 92) {
                                    String data = ("{" +
                                                   "\"BusID\" " + " : " + random + ", " +
                                                   "\"UUID\" " + " : " + "\"" + blemac + "\"" + ", " +
                                                   " \"Latitude\" " + " : " + lat + ",  " +
                                                   " \"Longitude\" " + " : " + lng + ",  " +
                                                   " \"TimeStamp\" " + " : " + "\""+date.toString()+"\"" +

                                                   "}"
                                    );
                                    byte[] encodeddata = new byte[0];
                                    try {
                                        encodeddata = data.getBytes("UTF-8");
                                        MqttMessage gpsdata = new MqttMessage(encodeddata);
//                                        Toast.makeText(MainActivity.this, "Location sending", Toast.LENGTH_SHORT).show();
                                        client.publish("Group-12-SSD/gpsLocationData", gpsdata);
                                        //Toast.makeText(MainActivity.this, "Location Sending", Toast.LENGTH_SHORT).show();
                                    } catch (UnsupportedEncodingException | MqttException e) {
                                        e.printStackTrace();
                                    }
                                //}
                            //}
                        //},10000 , 50000);

                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                    //Toast.makeText(MainActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems

                    Toast.makeText(MainActivity.this, "Client failed to Connect", Toast.LENGTH_SHORT).show();
                }

            });
        }
        catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String testtopic, MqttMessage message) throws Exception {

                String locationdata = message.toString();
                mGoogleMap.clear();
                JSONArray data = new JSONArray(locationdata);
                for (int i = 0; i < data.length(); i++) {

                    // Create a marker for each bus in the JSON data.
                    JSONObject jsonObj = data.getJSONObject(i);
                    Double Longitude = jsonObj.getDouble("Longitude");
                    Double Latitude = jsonObj.getDouble("Latitude");
                    mGoogleMap.addMarker(new MarkerOptions()
                            .title(jsonObj.getString("BusID"))
                            .snippet(jsonObj.getString("UUID"))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.busloc1))
                            .position(new LatLng(
                                    Latitude,
                                    Longitude
                            ))
                    );
                };
                //Toast.makeText(MainActivity.this, "BusLocations Updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }


    public void NoLocationProcessing() {



        clientId = MqttClient.generateClientId();

        final MqttAndroidClient client = new MqttAndroidClient(this.getApplicationContext(), host,
                clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //Toast.makeText(MainActivity.this, "Client Connected", Toast.LENGTH_SHORT).show();
                    try {
                        client.subscribe(testtopic, 0);
                        // final BleActivity bleActivity = new BleActivity();

                        //Intent myIntent = getIntent();
                        //String str = myIntent.getStringExtra("bleID");
                        //int tasker = myIntent.getIntExtra("int",-1);
                        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                        //if (tasker == 92) {
                        String data = ("{" +
                                "\"BusID\" " + " : " + random + ", " +
                                "\"UUID\" " + " : " + "\"" + "CC:DD" + "\"" + ", " +
                                " \"Latitude\" " + " : " + lat + ",  " +
                                " \"Longitude\" " + " : " + lng + ",  " +
                                " \"TimeStamp\" " + " : " + "\""+date.toString()+"\"" +

                                "}"
                        );
                        byte[] encodeddata = new byte[0];
                        try {
                            encodeddata = data.getBytes("UTF-8");
                            //MqttMessage gpsdata = new MqttMessage(encodeddata);
                            Toast.makeText(MainActivity.this, "Location not sending", Toast.LENGTH_SHORT).show();
                            //client.publish("Group-12-SSD/gpsLocationData", gpsdata);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        //}
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                    //Toast.makeText(MainActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems

                    Toast.makeText(MainActivity.this, "Client failed to Connect", Toast.LENGTH_SHORT).show();
                }

            });
        }
        catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String testtopic, MqttMessage message) throws Exception {

                String locationdata = message.toString();
                mGoogleMap.clear();
                JSONArray data = new JSONArray(locationdata);
                for (int i = 0; i < data.length(); i++) {

                    // Create a marker for each bus in the JSON data.
                    JSONObject jsonObj = data.getJSONObject(i);
                    Double Longitude = jsonObj.getDouble("Longitude");
                    Double Latitude = jsonObj.getDouble("Latitude");
                    mGoogleMap.addMarker(new MarkerOptions()
                            .title(jsonObj.getString("BusID"))
                            .snippet(jsonObj.getString("UUID"))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.busloc1))
                            .position(new LatLng(
                                    Latitude,
                                    Longitude
                            ))
                    );
                };
                //Toast.makeText(MainActivity.this, "BusLocations Updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }
//    public void datapublish() {
//
//        clientId = MqttClient.generateClientId();
//        final MqttAndroidClient client = new MqttAndroidClient(this.getApplicationContext(), host,
//                clientId);
//        try {
//            IMqttToken token = client.connect();
//            token.setActionCallback(new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    // We are connected
//                    Timer timer = new Timer();
//                    timer.scheduleAtFixedRate(new TimerTask() {
//                        @Override
//                        public void run() {
//                            String data = ("{" + "\"BusID\" " + " : " + random + ", " + "\"UUID\" " + " : " + "\"" +PREF_UNIQUE_ID + "\"" + ", " + " \"Latitude\" " + " : " + lat + ",  " + " \"Longitude\" " + " : " + lng + "}");
//                            byte[] encodeddata = new byte[0];
//                            try {
//                                encodeddata = data.getBytes("UTF-8");
//                                MqttMessage gpsdata = new MqttMessage(encodeddata);
//                                client.publish("Group-12-SSD/gpsLocationData", gpsdata);
//                            }
//                            catch (UnsupportedEncodingException | MqttException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    },10000 , 100000);
//                }
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    // Something went wrong e.g. connection timeout or firewall problems
//                    Toast.makeText(MainActivity.this, "Client failed to Connect, Data not Sent", Toast.LENGTH_SHORT).show();
//                }
//            });
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            MainActivity.ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;

        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            ArrayList<String> address;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";
            String Start_address = "";
            String Destination_address = "";

            //distance = response.getJSONArray("routes").getJSONObject(routeIndex).getJSONArray("legs").getJSONObject(index).getJSONObject("distance").getString("text");
            //duration = response.getJSONArray("routes").getJSONObject(routeIndex).getJSONArray("legs").getJSONObject(index).getJSONObject("duration").getString("text");
            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                address = new ArrayList<String>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");

                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }
                    else if (j == 2) { // Get duration from the list
                        Start_address = (String) point.get("start_address");
                        continue;
                    }
                    else if (j == 3) { // Get duration from the list
                        Destination_address = (String) point.get("end_address");
                        continue;
                    }


                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.YELLOW);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

//            Distance.setText("Distance Between Locations : "+distance);
//            Duration.setText(("Time to reach : "+ "  " + duration));
//            Start_add.setText("Starting Address : " + " " + Start_address);
//            Destination_add.setText("Destination Address :" + " " + Destination_address);

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mGoogleMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    public void onDirectionbtn(View view) {

        Intent myIntent2 = new Intent(MainActivity.this, DirectionsActivity.class);
        MainActivity.this.startActivity(myIntent2);

    }

}
