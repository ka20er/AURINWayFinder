package ka20er.aurinwayfinder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static ka20er.aurinwayfinder.GPSTracker.retLat;
import static ka20er.aurinwayfinder.GPSTracker.retLong;
import static ka20er.aurinwayfinder.R.id.radioGrp;

public class MainActivity extends AppCompatActivity {
    Button mapButton;
    Button coordButton;
    Button dataButton;
    Button resetButton;
    ImageButton imageButton_About;
    static Button listButton;
    TextView tvLong;
    TextView tvLat;
    static TextView tvAddr;
    EditText etBBox;
    RadioGroup radioGroup;
    ContextReceiver contextBR = new ContextReceiver();
    GPSTracker gps;
    private static Location location;
    private static String retAddress;
    public static final int REQUEST_CODE_PERMISSION = 1;
    public static final String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final int RETRIEVED_CODE = 1;
    public static int coverage;
    private static final int DEFAULT_DISTANCE = 200;
    private static final String GPROTOCOL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    private static String BBoxVal;
    private static String selectedBtnID = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordButton = (Button) findViewById(R.id.coordBtn);
        mapButton = (Button) findViewById(R.id.mapBtn);
        dataButton = (Button) findViewById(R.id.dataBtn);
        listButton = (Button) findViewById(R.id.listBtn);
        resetButton = (Button) findViewById(R.id.resetBtn);
        imageButton_About = (ImageButton) findViewById(R.id.imageBtn);

        tvLong = (TextView) findViewById(R.id.tvLong);
        tvLat = (TextView) findViewById(R.id.tvLat);
        tvAddr = (TextView) findViewById(R.id.tvRvGeo);
        etBBox = (EditText) findViewById(R.id.etBBox);
        radioGroup = (RadioGroup) findViewById(radioGrp);

        // Buttons are disabled by default
        disableButtons();

        // Prompt user if GPS is disabled
        if (checkStatusGPS() == false)
            showGPSDisabledAlertToUser();

        // Check data retrieval status
        IntentFilter filter = new IntentFilter();
        filter.addAction("LiquorLicense_DATA_RETRIEVED");
        filter.addAction("PublicToilet_DATA_RETRIEVED");
        filter.addAction("Medicare_DATA_RETRIEVED");
        filter.addAction("Centrelink_DATA_RETRIEVED");
        filter.addAction("School_DATA_RETRIEVED");
        filter.addAction("RecreationFacility_DATA_RETRIEVED");
        filter.addAction("Bus_DATA_RETRIEVED");
        filter.addAction("Tram_DATA_RETRIEVED");
        filter.addAction("Train_DATA_RETRIEVED");
        filter.addAction("Nothing_DATA_RETRIEVED");
        registerReceiver(contextBR,filter);

        coordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gps = new GPSTracker(MainActivity.this);
                double latitude = retLat;
                double longitude = retLong;

                tvLat.setText(String.format("%.6f", latitude));
                tvLong.setText(String.format("%.6f", longitude));

                if ((latitude != 0.0) && (longitude != 0.0) && (checkStatusGPS() == true)) {
                    mapButton.setEnabled(true);
                }

                location = new Location("");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                Log.i("LOCATION", String.valueOf(location));

                Toast.makeText(getApplicationContext(), "Requesting ... Reverse Geocode Lookup", Toast.LENGTH_SHORT).show();
                updateLocationInfo task = new updateLocationInfo();
                task.execute();
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etBBox.getText().toString().equalsIgnoreCase("")) {
                    coverage = DEFAULT_DISTANCE;
                } else {
                    coverage = Integer.parseInt(String.valueOf(etBBox.getText()));
                }
                etBBox.setEnabled(false);
                resetButton.setEnabled(true);
                Intent mapIntent = new Intent(MainActivity.this, MapsActivity.class);
                startActivityForResult(mapIntent, RETRIEVED_CODE);
            }
        });

        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sentBBoxIntent = new Intent(MainActivity.this, DataRetrievalService.class);
                Log.i("BBoxVal ", BBoxVal);
                sentBBoxIntent.putExtra(DataRetrievalService.MESSAGE, BBoxVal);
                sentBBoxIntent.putExtra(DataRetrievalService.OPTION, selectedBtnID);
                startService(sentBBoxIntent);
            }
        });

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listingIntent = new Intent(MainActivity.this, ListingActivity.class);
                startActivity(listingIntent);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtons();
                etBBox.setEnabled(true);
                mapButton.setEnabled(true);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedID) {
                listButton.setEnabled(false);
                // find which radio button is selected
                if (checkedID == R.id.radio1Btn) {
                    selectedBtnID = "0";
                } else if (checkedID == R.id.radio2Btn) {
                    selectedBtnID = "1";
                } else if (checkedID == R.id.radio3Btn) {
                    selectedBtnID = "2";
                } else if (checkedID == R.id.radio4Btn) {
                    selectedBtnID = "3";
                } else if (checkedID == R.id.radio5Btn) {
                    selectedBtnID = "4";
                } else if (checkedID == R.id.radio6Btn) {
                    selectedBtnID = "5";
                } else if (checkedID == R.id.radio7Btn) {
                    selectedBtnID = "6";
                } else if (checkedID == R.id.radio8Btn) {
                    selectedBtnID = "7";
                } else if (checkedID == R.id.radio9Btn) {
                    selectedBtnID = "8";
                }
            }
        });

        imageButton_About.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutUsIntent = new Intent(MainActivity.this,AboutUsActivity.class);
                startActivity(aboutUsIntent);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(contextBR);
    }

    /**
     * Retrieve the Bounding Box coordinates via the back button.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            super.onActivityResult(requestCode, resultCode, intent);

            if (requestCode == RETRIEVED_CODE && resultCode == RESULT_OK) {
                String boxSWLongitudeVal = intent.getStringExtra("boxSWLongitude");
                String boxSWLatitudeVal = intent.getStringExtra("boxSWLatitude");
                String boxNELongitudeVal = intent.getStringExtra("boxNELongitude");
                String boxNELatitudeVal = intent.getStringExtra("boxNELatitude");

                BBoxVal = boxSWLongitudeVal + "," + boxSWLatitudeVal + ", " +
                        boxNELongitudeVal + "," + boxNELatitudeVal;
                dataButton.setEnabled(true);
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Issue in Main Activity: " + ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void disableButtons() {
        mapButton.setEnabled(false);
        dataButton.setEnabled(false);
        listButton.setEnabled(false);
        resetButton.setEnabled(false);
    }

    /**
     * I only use GPS_PROVIDER because it is more accurate and can be performed with device that
     * is online via mobile network or Wi-Fi.
     */
    private boolean checkStatusGPS() {
        boolean statusGPS;

        // Check whether application has permission to access GPS sensor
        try{
            if (ContextCompat.checkSelfPermission(this,mPermission)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{mPermission},REQUEST_CODE_PERMISSION);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // Check whether GPS is turn ON or OFF
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        statusGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusGPS;
    }

    /**
     * Dialog box for warning user that GPS sensor is not activated.
     */
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Go Settings To Enable GPS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    /**
     * Receiving broadcast msg when LIQUOR_DATA_RETRIEVED is completed.
     */
    private class ContextReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("LiquorLicense_DATA_RETRIEVED")) {
                String msg = intent.getExtras().getString("dataStatus");
                Log.i("LIQUOR NOTICE:", msg);
            } else if (intent.getAction().equals("PublicToilet_DATA_RETRIEVED")) {
                String msg = intent.getExtras().getString("dataStatus");
                Log.i("PUBLIC TOILET NOTICE:", msg);
            } else if (intent.getAction().equals("Medicare_DATA_RETRIEVED")) {
                String msg = intent.getExtras().getString("dataStatus");
                Log.i("MEDICARE NOTICE:", msg);
            } else if (intent.getAction().equals("Centrelink_DATA_RETRIEVED")) {
                String msg = intent.getExtras().getString("dataStatus");
                Log.i("CENTRELINK NOTICE:", msg);
            } else if (intent.getAction().equals("School_DATA_RETRIEVED")) {
                String msg = intent.getExtras().getString("dataStatus");
                Log.i("SCHOOL NOTICE:", msg);
            } else if (intent.getAction().equals("RecreationFacility_DATA_RETRIEVED")) {
                String msg = intent.getExtras().getString("dataStatus");
                Log.i("RECREATION NOTICE:", msg);
            } else if (intent.getAction().equals("Bus_DATA_RETRIEVED")) {
                String msg = intent.getExtras().getString("dataStatus");
                Log.i("BUS NOTICE:", msg);
            } else if (intent.getAction().equals("Tram_DATA_RETRIEVED")) {
                String msg = intent.getExtras().getString("dataStatus");
                Log.i("TRAM NOTICE:", msg);
            } else if (intent.getAction().equals("Train_DATA_RETRIEVED")) {
                String msg = intent.getExtras().getString("dataStatus");
                Log.i("TRAIN NOTICE:", msg);
            } else if (intent.getAction().equals("Nothing_DATA_RETRIEVED")) {
                String msg = intent.getExtras().getString("dataStatus");
                Log.i("NO DATA NOTICE:", msg);
            }

            if (DataRetrievalService.listRelevantLocation.size() > 0) {
                listButton.setEnabled(true);
            } else {
                Toast.makeText(getApplicationContext(), "No Data available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    * GeoReverse from coordinate to location by running AsyncTask background service.
    * The result is passed to UI main thread after the background execution.
    * This is different to AURIN DataRetrievalService in which it is based on IntentService concept.
    */
    static private class updateLocationInfo extends AsyncTask<Void, Void, String> {
        HttpURLConnection connection;

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(GPROTOCOL + location.getLatitude() + "," + location.getLongitude()
                        + "&key=AIzaSyDK0FdeLHWjGPATQ4jyAt1Gv1n8G1Xm4UA");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                Log.i("CONNECTION: ", "DONE");

                StringBuilder lineBuilder = new StringBuilder();
                String line;

                line = reader.readLine();
                while (line != null) {
                    lineBuilder.append(line);
                    line = reader.readLine();
                }

                String dataJSON = lineBuilder.toString();
                Log.i("LINE BUILDER: ", "DONE");

                parseJSON(dataJSON);
                Log.i("DATA RETRIEVAL: ", "DONE");

            } catch (IOException e) {
                System.out.println("ERROR Logged");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            tvAddr.setText("Approximate Address: \n" + retAddress);
        }
    }

    private static void parseJSON(String dataJSON) {
        JSONObject readerJSON = null;

        try {
            readerJSON = new JSONObject(dataJSON);
            JSONArray featuresVal = readerJSON.getJSONArray("results");
            JSONObject firstLoc = featuresVal.getJSONObject(0);

            retAddress = firstLoc.getString("formatted_address");

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
