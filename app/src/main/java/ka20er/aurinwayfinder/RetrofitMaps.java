package ka20er.aurinwayfinder;

import ka20er.aurinwayfinder.Direction.Direction;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitMaps {
    @GET("api/directions/json?key=AIzaSyDK0FdeLHWjGPATQ4jyAt1Gv1n8G1Xm4UA")
    // No waypoints are specified. The route only consists of one leg.
    // Refer to https://developers.google.com/maps/documentation/directions/intro#TravelModes
    Call<Direction> getDistance(@Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode);
}
