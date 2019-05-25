package com.martiply.android.activities.store;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.martiply.model.Store;

class StorePagerAdapter extends FragmentStatePagerAdapter {
    private Store store;
    private String titleInfo, titleInventory;

    private StorePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    StorePagerAdapter(FragmentManager fm, Store store, String titleInfo, String titleInventory){
        this(fm);
        this.store = store;
        this.titleInfo = titleInfo;
        this.titleInventory = titleInventory;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1: return StoreInventoryFragment.newInstance(this.store);
            default: return StoreInfoFragment.newInstance(this.store);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 1: return titleInventory;
            default: return titleInfo;
        }
    }
}
