package com.martiply.android.activities.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;
import com.martiply.model.Item;
import com.martiply.model.Store;

import java.util.ArrayList;
import java.util.List;

public class StoreWrap implements ClusterItem {

    public Marker marker;
    public Store store;
    List<Item> items = new ArrayList<>();
    public int listIndex;

    public StoreWrap(Store store, Item item, int listIndex) {
        this.store = store;
        this.items.add(item);
        this.listIndex = listIndex;
    }

    public StoreWrap(Store store) {
        this.store = store;
    }

    @Override
    public LatLng getPosition() {
        return marker.getPosition();
    }

    @Override
    public String getTitle() {
        return store.getName();
    }

    @Override
    public String getSnippet() {
        return String.valueOf(store.getDistance());
    }
}
