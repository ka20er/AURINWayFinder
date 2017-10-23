package ka20er.aurinwayfinder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ka20er.aurinwayfinder.Direction.Direction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ka20er.aurinwayfinder.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private double retLong;
    private double retLat;
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    LocationManager locationManager;
    LocationListener locationListener;
    Button btnDriving;
    Button btnWalk;

    Map<String, String> mMarkers = new HashMap<>();
    LatLng AURIN_Marker;
    LatLng origin;
    LatLng dest;
    TextView ShowDistance;
    Polyline line;

    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final long MIN_DISTANCE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3 *1;
    private static final int DEFAULT_ZOOM_LEVEL = 17;

    private static int distance;
    private static String boxNELongitudeVal;
    private static String boxNELatitudeVal;
    private static String boxSWLongitudeVal;
    private static String boxSWLatitudeVal;
    private static LatLngBounds initialBoundary = null;
    private static LatLngBounds updatedBoundary = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setBuildingsEnabled(true);

        // Enabled all getstures setting
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setAllGesturesEnabled(true);
        mUiSettings.setZoomControlsEnabled(true);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location updateLocation) {
        // Clear the map
                mMap.clear();

        // Add a marker
                retLat = updateLocation.getLatitude();
                retLong = updateLocation.getLongitude();
                LatLng newPosition = new LatLng(retLat,retLong);
                mMap.addMarker(new MarkerOptions().position(newPosition).title("MY LOCATION").icon
                        (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                populateAURINData();
                updatedBoundary = initializeBounds(retLat,retLong);
                drawBounds (updatedBoundary, Color.RED);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }
        };

        // Check Build Version
        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES, MIN_DISTANCE_FOR_UPDATES,locationListener);
        } else {
        // Check permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // Ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_PERMISSION);
            } else {
        // We have permission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES, MIN_DISTANCE_FOR_UPDATES, locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (locationManager != null) {
                    retLat = lastKnownLocation.getLatitude();
                    retLong = lastKnownLocation.getLongitude();
                    origin = new LatLng(retLat, retLong);

                    mMap.addMarker(new MarkerOptions().position(origin).title("MY LOCATION").icon
                            (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    populateAURINData();
                    Toast.makeText(this,"Click on the marker to set destination", Toast.LENGTH_SHORT).show();

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(origin)
                            .zoom(DEFAULT_ZOOM_LEVEL)
                            .bearing(0)                 // Sets the orientation of the camera to North
                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    initialBoundary = initializeBounds(retLat,retLong);
                    drawBounds (initialBoundary, Color.BLUE);
                } else {
                    Toast.makeText(this,"NULL Location. Location sensor is inactive", Toast.LENGTH_SHORT).show();
                }
            }
        }

        ShowDistance = (TextView) findViewById(R.id.show_distance_time);
        ShowDistance.setText("");

        // Setting marker event click listener for the map
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                String id = mMarkers.get(marker.getId());
                Double markerLat  = marker.getPosition().latitude;
                Double markerLong  = marker.getPosition().longitude;
                dest = new LatLng(markerLat, markerLong);
                btnDriving.setEnabled(true);
                btnWalk.setEnabled(true);
                return true;
            }
        });

        btnDriving = (Button) findViewById(R.id.btnDriving);
        btnDriving.setEnabled(false);
        btnDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                build_retrofit_and_get_response("driving");
            }
        });

        btnWalk = (Button) findViewById(R.id.btnWalk);
        btnWalk.setEnabled(false);
        btnWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                build_retrofit_and_get_response("walking");
            }
        });
    }

    /**
     * Passing back BBox coordinates via the BackIntent.
     */
    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent();
        backIntent.putExtra("boxNELongitude", boxNELongitudeVal);
        backIntent.putExtra("boxNELatitude", boxNELatitudeVal);
        backIntent.putExtra("boxSWLongitude", boxSWLongitudeVal);
        backIntent.putExtra("boxSWLatitude", boxSWLatitudeVal);
        setResult(RESULT_OK, backIntent);
        finish();
    }

    /**
     * Initializes Bounding Box calculation.
     */
    public static LatLngBounds initializeBounds (Double latitudeVal, Double longitudeVal) {
        distance = MainActivity.coverage;
        List<LatLng> squarePositions = new ArrayList<>();

        // A List of LatLng defines current location. Two defines a square
        squarePositions.add(new LatLng(latitudeVal,longitudeVal));
        squarePositions.add(new LatLng(latitudeVal,longitudeVal));

        // Create a LatLngBounds.Builder and include my current position
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng position : squarePositions) {
            builder.include(position);
        }

        // Calculate the bounds of the initial positions
        LatLngBounds boundary = builder.build();

        // Increase the boundary by a specified distance
        // The bounds increases in the directions of NorthEast and SouthWest (45 and 225 degrees respectively)
        LatLng targetNorthEast = SphericalUtil.computeOffset(boundary.northeast, distance, 45);
        LatLng targetSouthWest = SphericalUtil.computeOffset(boundary.southwest, distance, 225);

        // Stored the BoundingBox coordinates
        boxNELongitudeVal = String.format("%.6f",targetNorthEast.longitude);
        boxNELatitudeVal = String.format("%.6f",targetNorthEast.latitude);
        boxSWLongitudeVal = String.format("%.6f",targetSouthWest.longitude);
        boxSWLatitudeVal = String.format("%.6f",targetSouthWest.latitude);

        // Add the new positions to the bounds
        builder.include(targetNorthEast);
        builder.include(targetSouthWest);

        // Calculate the bounds of the final positions
        LatLngBounds updateBoundary = builder.build();

        return updateBoundary;
    }

    /**
     * Draw the Bounding Box.
     */
    private void drawBounds (LatLngBounds bounds, int color) {
        PolygonOptions polygonOptions =  new PolygonOptions()
            .add(new LatLng(bounds.northeast.latitude, bounds.northeast.longitude))
            .add(new LatLng(bounds.southwest.latitude, bounds.northeast.longitude))
            .add(new LatLng(bounds.southwest.latitude, bounds.southwest.longitude))
            .add(new LatLng(bounds.northeast.latitude, bounds.southwest.longitude))
            .strokeColor(color);

        mMap.addPolygon(polygonOptions);
    }

    /**
     * If AURIN data is already available, populate it in the map.
     */
    private void populateAURINData() {
        if (MainActivity.listButton.isEnabled()) {
            for (int i = 0; i < DataRetrievalService.listRelevantLocation.size(); i++) {
                double destLat = DataRetrievalService.listRelevantLocation.get(i).getSavedLatitude();
                double destLong = DataRetrievalService.listRelevantLocation.get(i).getSavedLongitude();
                String label = DataRetrievalService.listRelevantLocation.get(i).getSavedTradeName();
                AURIN_Marker = new LatLng(destLat, destLong);
                MarkerOptions mo = new MarkerOptions().position(AURIN_Marker).title(label).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                Marker mkr = mMap.addMarker(mo);
                mMarkers.put(mkr.getId(),mo.getTitle());
            }
        }
    }

    /**
     * Get permission and perform system location update.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_FOR_UPDATES, locationListener);
            } else {
                Toast.makeText(this, "Permission is Denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /*
    * Retrieved direction by walking or driving from Google Direction API.
    */
    private void build_retrofit_and_get_response(String type) {
        String url = "https://maps.googleapis.com/maps/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);

        // Asynchronous Call in Retrofit 2.0
        // Request to Google Direction API is made via a background thread
        Call<Direction> call = service.getDistance("metric", retLat + "," + retLong,
                dest.latitude + "," + dest.longitude, type);

        // Put it into queue data structure
        call.enqueue(new Callback<Direction>() {
            @Override
        // This is done in the Main Thread
            public void onResponse(Call<Direction> call, Response<Direction> response) {
                // Whether the response is able to be parsed or not to Data Access Object, onResponse
                // will always be called. Further, there is also a possibility of problem in the
                // Google API response(404 error). Hence, try-catch block is necessary,
                try {
                    if (line != null) {
                        line.remove();
                    }
                    String distance = response.body().getRoutes().get(0).getLegs().get(0).getDistance().getText();
                    ShowDistance.setText("Distance:" + distance);

                    String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                    List<LatLng> list = decodePoly(encodedString);
                // Puts bread-crumb red markers
                    line = mMap.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(20)
                            .color(Color.RED)
                            .geodesic(true)
                    );
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Direction> call, Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });
    }

    /*
    * Decode the polyline encoding to get the route representation in coordinates
    * Refer to https://developers.google.com/maps/documentation/utilities/polylinealgorithm
    * Refer to https://ascii.cl/ for ASCII Codes Table
    * Refer to https://stackoverflow.com/questions/14463230/or-each-binary-with-0x20
    */
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shiftLat = 0;
            int resultLat = 0;

            // Hexa-decimal to Binary
            // 0x20 is 100000
            // 0x1f is 11111
            // & is bitwise AND operator
            // | is bitwise inclusive OR operator
            do {
            // Get ASCII numeric representation of the symbol
            // First convert ASCII to code number representation.
            // Then minus 63 to get decimal representation.
                b = encoded.charAt(index++) - 63;
            // Refer to step 8 to 6 on Google documentation
                resultLat |= (b & 0x1f) << shiftLat;
                shiftLat += 5;
            } while (b >= 0x20);

            // If resultLat bitwise AND with 1 != 0 then it is a negative value else it is positive
            // then unary bitwise complement operator (invert the bit pattern) after right shift one bit
            // else right shift one bit
            int dlat = ((resultLat & 1) != 0 ? ~(resultLat >> 1) : (resultLat >> 1));
            lat += dlat;

            int shiftLong = 0;
            int resultLong = 0;

            do {
                b = encoded.charAt(index++) - 63;
                resultLong |= (b & 0x1f) << shiftLong;
                shiftLong += 5;
            } while (b >= 0x20);

            int dlng = ((resultLong & 1) != 0 ? ~(resultLong >> 1) : (resultLong >> 1));
            lng += dlng;

            // Divide by 1e5 to get coordinates
            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}
