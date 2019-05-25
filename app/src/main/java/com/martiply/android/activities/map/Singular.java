package com.martiply.android.activities.map;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.leo.simplearcloader.SimpleArcLoader;
import com.martiply.android.MyApplication;
import com.martiply.android.R;
import com.martiply.android.activities.BusProvider;
import com.martiply.android.activities.store.ItemActivity;
import com.martiply.android.activities.store.PreStoreActivity;
import com.martiply.android.api.ApiManager;
import com.martiply.android.dialog.OkLightDialog;
import com.martiply.android.sqlite.DaoSession;
import com.martiply.android.sqlite.QueryHistory;
import com.martiply.android.sqlite.QueryHistoryDao;
import com.martiply.android.suggestion.EventGotQueryHistory;
import com.martiply.android.util.DeviceUtils;
import com.martiply.android.util.PreferenceUtils;
import com.martiply.android.view.MyMapView;
import com.martiply.android.view.PassTouchEventToolbar;
import com.martiply.model.IPP;
import com.martiply.model.MtpResponse;
import com.martiply.model.Store;
import com.squareup.otto.Subscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Singular extends LocationAwareActivity implements OnMapReadyCallback {
    private static final float RADIUS_KM_SEARCH_FOR_STORES = 2.0f;
    private static final float RADIUS_KM_SEARCH_FOR_ITEMS = 2.0f;
    private static final int DIALOG_ID_LOCATION_PERMISSION_NOT_GRANTED = 2;

    private QueryHistoryDao queryHistoryDao;
    private Circle myCircle;
    private LinkedHashMap<Marker, StoreWrap> markerMap = new LinkedHashMap<>();
    private GoogleMap mMap;
    private DrawerLayout mDrawerLayout;
    private LatLng mLatLng;
    private ActionBarDrawerToggle mDrawerToggle;
    private Call<MtpResponse<IPP>> callFindItems;
    private Call<MtpResponse<Store>> callFindStores;
    private GoogleMap.OnMarkerClickListener sideItemMarkerClickListener = new GoogleMap.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {
            StoreWrap sw = markerMap.get(marker);
            if (sw != null && sw.items.size() == 1) {
                centerFarOnMarker(mMap, marker, true);
                BusProvider.getInstance().post(new SideItemSearchFragment.MarkerClickedEvent(sw));
            }
            return false;
        }
    };

    private GoogleMap.OnMapClickListener sideItemMapClickListener = latLng -> BusProvider.getInstance().post(new SideItemSearchFragment.MapClickedEvent());

    private Unbinder unbinder;
    @BindView(R.id.left_drawer) FrameLayout leftDrawer;
    @BindView(R.id.pb) SimpleArcLoader pb;
    @BindView(R.id.guide_anchor) View guideMapAnchor;
    @BindView(R.id.map) MyMapView myMapView;

    public static void navigate(Context ctx) {
        Intent intent = new Intent(ctx, Singular.class);
        ctx.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle inState) {
        super.onCreate(inState);
        super.type = Singular.class;
        BusProvider.getInstance().register(this);
        setContentView(R.layout.activity_singular);
        unbinder = ButterKnife.bind(this);
        DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
        queryHistoryDao = daoSession.getQueryHistoryDao();

        mLatLng = PreferenceUtils.getLatLng(this);
        PassTouchEventToolbar toolbar = (PassTouchEventToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbar.setNavigationIcon(R.drawable.ic_logo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setToolbarNavigationClickListener(v -> {
            if (mDrawerLayout.isDrawerOpen(leftDrawer)) {
                mDrawerLayout.closeDrawer(leftDrawer);
            } else {
                mDrawerLayout.openDrawer(leftDrawer);
            }
        });
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.closeDrawer(GravityCompat.START);

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        MapsInitializer.initialize(this);

        myMapView.onCreate(inState);
        myMapView.getMapAsync(this);

        getFragmentManager()
            .beginTransaction()
            .add(R.id.left_drawer, SideItemSearchFragment.newInstance())
            .add(R.id.container, ItemOverlayFragment.newInstance(), "itemOverlay")
            .commit();

        GuideHelper.showHowToSearch(this, guideMapAnchor, toolbar, mDrawerLayout);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        if (!checkPermission(Singular.this)) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(() -> {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
            mGoogleApiClient.connect();
            return false;
        });

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setOnMyLocationButtonClickListener(() -> {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
            mGoogleApiClient.connect();
            return true;
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Store store = markerMap.get(marker).store;
                return Singular.super.inflateInfoWindowCaret(store);
            }
        });

        mMap.setOnInfoWindowClickListener(marker -> {
            Store storeInfo = markerMap.get(marker).store;
            PreStoreActivity.navigate(Singular.this, storeInfo.getStoreId());
        });

        mMap.setOnMarkerClickListener(sideItemMarkerClickListener);
        mMap.setOnMapClickListener(sideItemMapClickListener);

        myMapView.setListener(new MyMapView.Listener() {

            @Override
            public void onDoubleClicked(MotionEvent motionEvent) {
                Projection p = mMap.getProjection();
                mLatLng = p.fromScreenLocation(new Point((int) motionEvent.getX(), (int) motionEvent.getY()));
                resetMarkers();
                makeCircle();
                zoomOut();
                BusProvider.getInstance().post(new ManualPositionSetEvent());
            }
        });
        mGoogleApiClient.connect();
//        Debugs.resetGuide(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getStringExtra(ItemActivity.REQUEST_SEARCH_SIMILAR_CATEGORY) != null) {
            if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            BusProvider.getInstance().post(new SearchSimilarEvent(intent.getStringExtra(ItemActivity.REQUEST_SEARCH_SIMILAR_CATEGORY)));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        super.mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        unbinder.unbind();
        if (myMapView != null)
            myMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (myMapView != null)
            myMapView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (myMapView != null)
            myMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (myMapView != null)
            myMapView.onResume();
    }

    public void resetMarkers() {
        for (Marker key : markerMap.keySet()) {
            key.hideInfoWindow();
            key.remove();
        }
        markerMap.clear();
    }

    private void zoomOut() {
        CameraPosition camPos = new CameraPosition.Builder().target(mMap.getCameraPosition().target).zoom(ZOOM_FAR).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos), getResources().getInteger(R.integer.ms_map_anim), null);
    }

    public void makeCircle() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, ZOOM_FAR), getResources().getInteger(R.integer.ms_map_anim), null);
        if (myCircle != null) {
            myCircle.remove();
        }
        myCircle = mMap.addCircle(new CircleOptions()
            .center(mLatLng)
            .radius(RADIUS_KM_SEARCH_FOR_ITEMS * 1300) // not very precise
            .strokeColor(ContextCompat.getColor(this, android.R.color.transparent))
            .fillColor(ContextCompat.getColor(this, R.color.blue_op1)));
    }

    private Marker getMarkerPosition(int pos) {
        return (new ArrayList<>(markerMap.keySet())).get(pos);
    }

    private void cancelAllCalls() {
        if (callFindItems != null) {
            callFindItems.cancel();
        }
        if (callFindStores != null) {
            callFindStores.cancel();
        }
    }

    @Subscribe
    public void onFinish(FinishEvent event) {
        cancelAllCalls();
        finish();
    }

    @Subscribe
    public void onSwitchToItemSearch(SideStoreSearchFragment.SwitchToItemSearchEvent event) {
        cancelAllCalls();
        showToastProgressBar(false);
        mMap.setOnMarkerClickListener(sideItemMarkerClickListener);
        mMap.setOnMapClickListener(sideItemMapClickListener);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.left_drawer, SideItemSearchFragment.newInstance())
                .replace(R.id.container, ItemOverlayFragment.newInstance(), "itemOverlay")
                .commit();
        mDrawerLayout.openDrawer(GravityCompat.START);
        resetMarkers();
        zoomOut();
    }

    @Subscribe
    public void onSwitchToScanForStores(SideItemSearchFragment.SwitchToScanForStoresEvent event) {
        cancelAllCalls();
        showToastProgressBar(false);
        mMap.setOnMarkerClickListener(null);
        mMap.setOnMapClickListener(null);
        ItemOverlayFragment remove = (ItemOverlayFragment) getFragmentManager().findFragmentByTag("itemOverlay");

        getFragmentManager()
                .beginTransaction()
                .remove(remove)
                .replace(R.id.left_drawer, SideStoreSearchFragment.newInstance())
                .commit();
        mDrawerLayout.closeDrawer(GravityCompat.START);
        resetMarkers();
        zoomOut();
    }

    private void showToastProgressBar(boolean isShow) {
        pb.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Subscribe
    public void onStartSearchForStores(SideStoreSearchFragment.StartSearchForStoresEvent event) {
        showToastProgressBar(true);
        cancelAllCalls();
        resetMarkers();

        callFindStores = ApiManager.getService().getAreaStores(mLatLng.latitude, mLatLng.longitude, RADIUS_KM_SEARCH_FOR_STORES, null, DeviceUtils.getUid(Singular.this));
        callFindStores.enqueue(new Callback<MtpResponse<Store>>() {
            @Override
            public void onResponse(@NonNull Call<MtpResponse<Store>> call, @NonNull Response<MtpResponse<Store>> response) {
                showToastProgressBar(false);
                final MtpResponse<Store> mtpResponse = response.body();
                if (mtpResponse == null || !mtpResponse.isSuccess()) {
                    if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                        Snackbar.make(myMapView, R.string.map_store_empty, Snackbar.LENGTH_SHORT).show();
                    }
                    BusProvider.getInstance().post(new ScanForStoresResponseNotFoundEvent());
                    return;
                }
                List<Store> data = mtpResponse.getData();
                BusProvider.getInstance().post(new ScanForStoreResponseSuccessEvent(data.size()));
                for (Store store : data) {
                    Marker marker = Singular.super.addMarkerToMap(store.getLat(), store.getLng(), mMap, false);
                    markerMap.put(marker, new StoreWrap(store));
                    BusProvider.getInstance().post(new ScanForStoresEmitOneEvent(store, marker, true));
                }
                Snackbar.make(myMapView, getString(R.string.search_foundStores, data.size()), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<MtpResponse<Store>> call, @NonNull Throwable t) {
                showToastProgressBar(false);
                if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    Snackbar.make(myMapView, R.string.error_server, Snackbar.LENGTH_LONG).show();
                }
                BusProvider.getInstance().post(new ScanForStoreResponseFailedEvent());
            }
        });
    }

    @Subscribe
    public void onStoreQueryMatchingStarts(SideStoreSearchFragment.ListClearEvent event) {
        resetMarkers();
    }

    @Subscribe
    public void onStoreQueryMatchedOne(SideStoreSearchFragment.RequestAddOneStoreToMapEvent event) {
        Marker marker = Singular.super.addMarkerToMap(event.store.getLat(), event.store.getLng(), mMap, false);
        markerMap.put(marker, new StoreWrap(event.store));
        BusProvider.getInstance().post(new ScanForStoresEmitOneEvent(event.store, marker, false));
    }

    @Subscribe
    public void onNewLocation(MyNewLocationEvent event) {
        mLatLng = event.latLng;
        makeCircle();
        resetMarkers();
        BusProvider.getInstance().post(new ManualPositionSetEvent());
    }

    @Subscribe
    public void onStoreRowClicked(SideStoreSearchFragment.StoreRowClickedEvent event) {
        Marker activeMarker = getMarkerPosition(event.position);
        activeMarker.showInfoWindow();
        super.centerCloseOnMarker(mMap, activeMarker);
    }

    @Subscribe
    public void onQueryForAreaItems(final SideItemSearchFragment.QueryEvent event) {
        cancelAllCalls();
        resetMarkers();
        zoomOut();
        showToastProgressBar(true);
        if(event.query.trim().isEmpty()){
            return;
        }
        queryHistoryDao.insertOrReplace(new QueryHistory(event.query, System.currentTimeMillis() / 1000));
        callFindItems = ApiManager.getService().getAreaItems(mLatLng.latitude, mLatLng.longitude, RADIUS_KM_SEARCH_FOR_ITEMS, event.query, event.category, null, DeviceUtils.getUid(this));
        callFindItems.enqueue(new Callback<MtpResponse<IPP>>() {

            @Override
            public void onResponse(@NonNull Call<MtpResponse<IPP>> call, @NonNull Response<MtpResponse<IPP>> response) {
                showToastProgressBar(false);
                MtpResponse<IPP> mtpResponse = response.body();
                if (mtpResponse == null || !mtpResponse.isSuccess()) {
                    if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                        Snackbar.make(myMapView, String.format(getString(R.string.search_notfoundItem), event.query), Snackbar.LENGTH_SHORT).show();
                    }
                    BusProvider.getInstance().post(new AreaItemsResponseNotFoundEvent(event.query));
                    return;
                }
                BusProvider.getInstance().post(new AreaItemsResponseSuccessEvent());
                LinkedHashMap<Integer, StoreWrap> tMap = new LinkedHashMap<>(); // <StoreId, StoreWrap>
                List<IPP> data = mtpResponse.getData();
                for (int i = 0; i < data.size(); i++) {
                    IPP ipp = data.get(i);
                    StoreWrap sw1 = tMap.get(ipp.getStore().getStoreId());
                    if (sw1 == null) {
                        sw1 = new StoreWrap(ipp.getStore(), ipp.getItem(), i);
                        tMap.put(ipp.getStore().getStoreId(), sw1);
                    } else {
                        sw1.items.add(ipp.getItem());
                    }
                }
                for (StoreWrap sw2 : tMap.values()) {
                    Marker m = addMarkerToMap(sw2.store.getLat(), sw2.store.getLng(), mMap, sw2.items.size() > 1);
                    markerMap.put(m, sw2);
                    sw2.marker = m;
                    BusProvider.getInstance().post(new AreaItemEmitsOneEvent(sw2));
                }
                BusProvider.getInstance().post(new AreaItemEmitsFooterEvent());
                if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    Snackbar.make(myMapView, getString(R.string.search_foundItems, data.size(), event.query), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MtpResponse<IPP>> call, Throwable t) {
                showToastProgressBar(false);
                if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    Snackbar.make(myMapView, R.string.error_server, Snackbar.LENGTH_LONG).show();
                }
                BusProvider.getInstance().post(new AreaItemServerErrorEvent());
            }
        });

    }

    @Subscribe
    public void onDrawerItemClicked(SideItemSearchFragment.DrawerItemClickedEvent event) {
        super.centerFarOnMarker(mMap, event.storeItemMarker.marker, true);
        mDrawerLayout.closeDrawers();
    }

    @Subscribe
    public void onDrawerItemClicked(SideStoreSearchFragment.DrawerItemClickedEvent event) {
        super.centerFarOnMarker(mMap, event.marker, true);
        mDrawerLayout.closeDrawers();
    }

    @Subscribe
    public void onDrawerImageClicked(SideItemSearchFragment.ItemActivityRequestEvent event) {
        ItemActivity.navigate(this, ItemActivity.ACTIVITY_ORIGIN_SINGULAR, event.origin, event.item, event.store);
    }

    @Subscribe
    public void onMultipleItemPanelClicked(ItemOverlayFragment.MultipleItemPanelClickedEvent event) {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Subscribe
    public void onDialogDismissed(OkLightDialog.DismissEvent event) {
        switch (event.id) {
            case DIALOG_ID_LOCATION_PERMISSION_NOT_GRANTED:
                finish();
                break;
            default:
        }
    }

    @Subscribe
    public void onReceiveRequestZoomOnMarker(SideItemSearchFragment.RequestZoomOnMarkerEvent event) {
        centerFarOnMarker(mMap, event.marker, true);
    }

    @Subscribe
    public void onLocationPermissionNotGranted(LocationAwareActivity.LocationPermissionNotGrantedEvent event) {
        OkLightDialog tip = OkLightDialog.newInstance(getString(R.string.error_permission_location), DIALOG_ID_LOCATION_PERMISSION_NOT_GRANTED);
        tip.show(getFragmentManager(), "dialog");
    }


    @Subscribe
    public void onDrawerToggleRequested(RequestDrawerToggleEvent event) {
        if (event.open) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Subscribe
    public void onLastLocationUnavailable(LocationAwareActivity.LastLocationUnavailableEvent event) {
        Snackbar.make(myMapView, R.string.error_location_na, Snackbar.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onRequestQueryHistoryEvent(SideItemSearchFragment.RequestQueryHistoryEvent event) {
        if (event.query.trim().isEmpty()) {
            BusProvider.getInstance().post(new EventGotQueryHistory(queryHistoryDao.queryBuilder().orderDesc(QueryHistoryDao.Properties.Ts).limit(5).build().list()));
        } else {
            BusProvider.getInstance().post(new EventGotQueryHistory(queryHistoryDao.queryBuilder().orderDesc(QueryHistoryDao.Properties.Ts).limit(5)
                    .where(QueryHistoryDao.Properties.Query.like(String.format("%%%s%%", event.query))).build().list()));
        }
    }

    public static class ManualPositionSetEvent {
    }

    public static class AreaItemsResponseSuccessEvent {
    }

    public static class AreaItemsResponseNotFoundEvent {
        String query;

        AreaItemsResponseNotFoundEvent(String query) {
            this.query = query;
        }
    }

    public static class AreaItemEmitsOneEvent {
        StoreWrap sw;

        AreaItemEmitsOneEvent(StoreWrap sw) {
            this.sw = sw;
        }
    }

    public static class AreaItemEmitsFooterEvent {
    }

    public static class AreaItemServerErrorEvent {
    }

    public static class ScanForStoreResponseSuccessEvent {
        int numStores;

        ScanForStoreResponseSuccessEvent(int numStores) {
            this.numStores = numStores;
        }
    }

    public static class ScanForStoresEmitOneEvent {
        Store store;
        Marker marker;
        boolean isAddToMaster;

        ScanForStoresEmitOneEvent(Store store, Marker marker, boolean isAddToMaster) {
            this.store = store;
            this.marker = marker;
            this.isAddToMaster = isAddToMaster;
        }
    }

    public static class ScanForStoreEmitFooterEvent {
    }

    public static class ScanForStoresResponseNotFoundEvent {
    }

    public static class ScanForStoreResponseFailedEvent {
    }

    public static class FinishEvent {
    }

    public static class SearchSimilarEvent {
        String category;

        SearchSimilarEvent(String category) {
            this.category = category;
        }
    }

    public static class MyNewLocationEvent {
        LatLng latLng;

        MyNewLocationEvent(LatLng latLng) {
            this.latLng = latLng;
        }
    }

    public static class RequestDrawerToggleEvent {
        boolean open;

        RequestDrawerToggleEvent(boolean open) {
            this.open = open;
        }
    }
}