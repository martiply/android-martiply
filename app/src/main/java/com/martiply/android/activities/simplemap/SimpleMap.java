package com.martiply.android.activities.simplemap;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.martiply.android.R;
import com.martiply.android.activities.BusProvider;
import com.martiply.android.activities.map.LocationAwareActivity;
import com.martiply.android.view.PassTouchEventToolbar;
import com.martiply.model.Store;
import org.parceler.Parcels;

public class SimpleMap extends LocationAwareActivity implements OnMapReadyCallback {
    public static final String LOAD_STORE_LOCATION = "LOAD_STORE_LOCATION";
    @BindView(R.id.panel_simple_store) LinearLayout simpleStorePanel;
    @BindView(R.id.panel_near_offer) LinearLayout offerPanel;
    @BindView(R.id.center) ImageView center;
    @BindView(R.id.back) ImageView back;
    @BindView(R.id.text) TextView text;
    @BindView(R.id.map) MapView mapView;
    private Store store;
    private Marker marker;
    private GoogleMap mMap;
    private Unbinder unbinder;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return SimpleMap.super.inflateInfoWindowCaret(store);
            }
        });

        if (getIntent().getParcelableExtra(LOAD_STORE_LOCATION) != null) {
            simpleStorePanel.setVisibility(View.VISIBLE);
            offerPanel.setVisibility(View.GONE);
            store = Parcels.unwrap(getIntent().getParcelableExtra(LOAD_STORE_LOCATION));
            marker = super.addMarkerToMap(store.getLat(), store.getLng(), mMap, false);
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker marker) {
                    finish();
                }
            });
            center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SimpleMap.super.centerFarOnMarker(mMap, marker, true);
                }
            });
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        marker = super.addMarkerToMap(store.getLat(), store.getLng(), mMap, false);
        super.centerFarOnMarker(mMap, marker, true);
    }


    @Override
    public void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_map_simple);
        unbinder = ButterKnife.bind(this);
        PassTouchEventToolbar toolbar = (PassTouchEventToolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_logo);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        MapsInitializer.initialize(this);
        mapView.onCreate(inState);
        mapView.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        unbinder.unbind();
        if (mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null)
            mapView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null)
            mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.onResume();
    }


}
