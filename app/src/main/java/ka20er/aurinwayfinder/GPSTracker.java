package ka20er.aurinwayfinder;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class GPSTracker extends Activity {
    public static double retLong;
    public static double retLat;
    private final Context mContext;
    private static final long MIN_DISTANCE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3 * 1;
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public void getLocation() {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        final LocationListener locationListener = new LocationListener() {
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

            @Override
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub
            }
        };

        // Check Build Version. If below version 23, GPS sensor can be accessed directly.
        // Otherwise, the application needs to check whether user permission has been granted.
        // Since the minSDKVersion of this app is 23 in the build.gradle file, the first part of this
        // code will always be falsified. I have inserted it here just for completeness and clarity.
        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_FOR_UPDATES, locationListener);
        } else{
            // Check permission
            if (ContextCompat.checkSelfPermission(mContext, MainActivity.mPermission) != PackageManager.PERMISSION_GRANTED) {
                // Ask for permission
                ActivityCompat.requestPermissions(this, new String[]{MainActivity.mPermission}, MainActivity.REQUEST_CODE_PERMISSION);
            } else {
                // We have permission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_FOR_UPDATES, locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    retLat = lastKnownLocation.getLatitude();
                    retLong = lastKnownLocation.getLongitude();
                }
            };
        }
    }
}