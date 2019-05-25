package com.martiply.android.activities.map;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.*;
import com.martiply.android.R;
import com.martiply.android.activities.BusProvider;
import com.martiply.android.activities.permission.Permission;
import com.martiply.android.util.Debugs;
import com.martiply.model.Store;

import java.lang.reflect.Type;

public  class LocationAwareActivity extends AppCompatActivity {
    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 34;
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 56;

    public static final float ZOOM_FAR = 13.0F;
    public static final float ZOOM_CLOSE = 16.0F;
//	protected LocationClient mLocationClient;
	protected Location mLastLocation;

    protected Type type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_COARSE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    buildGoogleApiClient();
//                } else {
//                    Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    buildGoogleApiClient();
//                } else {
//                    Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }

    protected GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {

		@Override
		public void onConnectionFailed(ConnectionResult conResult) {
			if (conResult.hasResolution()){
				try{
					conResult.startResolutionForResult(LocationAwareActivity.this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
				}catch(SendIntentException e){
					e.printStackTrace();
				}
			}
		}
	};

    protected GoogleApiClient mGoogleApiClient;

    protected boolean checkPermission(Context ctx) {
        if (ContextCompat.checkSelfPermission(LocationAwareActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Permission.navigate(ctx);
            finish();
            return false;
        }
        return true;
    }

    protected void buildGoogleApiClient(){
       mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle arg0) {
                        if (!checkPermission(LocationAwareActivity.this)){return;}

                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        LatLng lastLatLng;
                        if (mLastLocation != null){ // this can return null http://developer.android.com/training/location/retrieve-current.html
                            // The location object returned may be null in rare cases when the location is not available.
                            lastLatLng = Debugs.restrictLatLng(getBaseContext(), new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                        }else{
                            BusProvider.getInstance().post(new LastLocationUnavailableEvent());
                            lastLatLng = Debugs.thamrin;
                        }

//                        lastLatLng = Debugs.restrictLatLng(getBaseContext());
//                        LatLng latLng = new LatLng(-6.84382D, 107.486029D); // Padalarang, to test nearest store query
//                        if (type == MainActivity.class){
//                            BusProvider.getInstance().post(new MainActivity.MyNewLocationEvent(lastLatLng));
//                        }else if (type == Singular.class){
                            BusProvider.getInstance().post(new Singular.MyNewLocationEvent(lastLatLng));
//                        }
                    }

                    @Override
                    public void onConnectionSuspended(int arg0) {
                    }

                })
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .addApi(LocationServices.API)
                .build();
    }

    public Marker addMarkerToMap(double lat, double lng, GoogleMap googleMap, boolean isStacked){
        LatLng latlng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latlng)
                .icon(BitmapDescriptorFactory.defaultMarker(isStacked ? BitmapDescriptorFactory.HUE_AZURE:BitmapDescriptorFactory.HUE_ORANGE));
        return googleMap.addMarker(markerOptions);
    }

    public  void centerFarOnMarker(GoogleMap googleMap, final Marker marker, final boolean isShowInfoWindow){
        CameraPosition camPos = new CameraPosition.Builder().target(marker.getPosition()).zoom(ZOOM_FAR).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos), getResources().getInteger(R.integer.ms_map_anim), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                if (isShowInfoWindow) {
                    marker.showInfoWindow();
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }

    public void centerCloseOnMarker(GoogleMap googleMap, Marker marker) {
        CameraPosition camPos = new CameraPosition.Builder().target(marker.getPosition()).zoom(ZOOM_CLOSE).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos), getResources().getInteger(R.integer.ms_map_anim), null);
    }


    public View inflateInfoWindowCaret(Store store){
        View v = getLayoutInflater().inflate(R.layout.info_window_caret, null);
        ((TextView)(v.findViewById(R.id.text))).setText(store.getName());
        ((TextView)(v.findViewById(R.id.text2))).setText(store.getAddress());
        return v;
    }


    public static class LastLocationUnavailableEvent{}


    public static class LocationPermissionNotGrantedEvent{}

}
