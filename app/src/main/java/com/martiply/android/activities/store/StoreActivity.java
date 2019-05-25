package com.martiply.android.activities.store;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ViewSwitcher;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.Transition;
import com.martiply.android.MyApplication;
import com.martiply.android.R;
import com.martiply.android.activities.BusProvider;
import com.martiply.android.activities.map.Singular;
import com.martiply.android.sqlite.DaoSession;
import com.martiply.android.sqlite.QueryHistory;
import com.martiply.android.sqlite.QueryHistoryDao;
import com.martiply.android.dialog.OkDarkDialog;
import com.martiply.android.suggestion.EventGotQueryHistory;
import com.martiply.android.util.DeviceUtils;
import com.martiply.model.Img;
import com.martiply.model.Item;
import com.martiply.model.MtpResponse;
import com.martiply.model.Store;
import com.martiply.model.interfaces.AbsImg;
import com.martiply.android.api.ApiManager;
import com.squareup.otto.Subscribe;
import org.parceler.Parcels;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class StoreActivity extends AppCompatActivity implements KenBurnsView.TransitionListener {
    public static final String LOAD_STORE_ID = "LOAD_STORE_ID";
    public static final String LOAD_METADATA = "LOAD_METADATA";
    public static final String LOAD_STORE = "LOAD_STORE";
    private Call<MtpResponse<Item>> callFindStoreItems;
    private QueryHistoryDao queryHistoryDao;
    private Store mStore;
    private Unbinder unbinder;

    @BindView(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.img1) KenBurnsView backdrop1;
    @BindView(R.id.img2) KenBurnsView backdrop2;
    @BindView(R.id.view_switcher) ViewSwitcher viewSwitcher;

    private static final int TRANSITIONS_TO_SWITCH = 3;
    private int mTransitionsCount = 0;

    public static void navigate(AppCompatActivity activity, int storeId, Parcelable pMetadata){
        Intent i = new Intent(activity, StoreActivity.class);
        i.putExtra(LOAD_STORE_ID, storeId);
        i.putExtra(LOAD_METADATA, pMetadata);
        activity.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getIntExtra(LOAD_STORE_ID, -1) <=0 || getIntent().getParcelableExtra(LOAD_METADATA) == null){
            PreStoreActivity.navigate(this, getIntent().getIntExtra(LOAD_STORE_ID, -1));
            finish();
            return;
        }
        setContentView(R.layout.activity_store_main);
        setTitle("");
        unbinder = ButterKnife.bind(this);
        BusProvider.getInstance().register(this);

        DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
        queryHistoryDao = daoSession.getQueryHistoryDao();

        Parcelable parcelableMetadata = getIntent().getParcelableExtra(LOAD_METADATA);
        mStore = Parcels.unwrap(parcelableMetadata);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitleEnabled(false);
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.google_blue_dk)); //primary
        collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(this, android.R.color.transparent)); //primary dark

        StorePagerAdapter pagerAdapter = new StorePagerAdapter(getSupportFragmentManager(), mStore, getString(R.string.store_tab_info), getString(R.string.store_tab_inventory));
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Img.loadImg(mStore.getImg(), 0, this, backdrop1, R.color.google_blue_lt, R.drawable.wp_maples, AbsImg.Size.xl);
        Img.loadImg(mStore.getImg(), 1, this, backdrop2, R.color.google_blue_lt, R.drawable.wp_polo, AbsImg.Size.xl);

        backdrop1.setTransitionListener(this);
        backdrop2.setTransitionListener(this);
    }

    private void searchSimilar(String string){
        viewPager.setCurrentItem(1);
        BusProvider.getInstance().post(new StoreInventoryFragment.RequestSetTextEvent(string));
        BusProvider.getInstance().post(new StoreInventoryFragment.QueryEvent(string));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(ItemActivity.REQUEST_SEARCH_SIMILAR_CATEGORY)) {
            searchSimilar(intent.getStringExtra(ItemActivity.REQUEST_SEARCH_SIMILAR_CATEGORY));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(callFindStoreItems != null){
            callFindStoreItems.cancel();
        }
        BusProvider.getInstance().unregister(this);
        unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent i = new Intent(this, Singular.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (viewPager != null){
            if( viewPager.getCurrentItem() == 0){
                super.onBackPressed();
            }else{
                viewPager.setCurrentItem(0);
            }
        }else{
            super.onBackPressed();
        }
    }

    @Subscribe
    public void onNoPhone(StoreInfoAdapter.NoPhoneEvent event) {
        OkDarkDialog okDarkDialog = OkDarkDialog.newInstance(getString(R.string.error_telephone_none));
        okDarkDialog.show(getFragmentManager(), "dialog");
    }

    private Callback<MtpResponse<Item>> buildSearchCallback(final StoreInventoryFragment.QueryEvent event) {
        return new Callback<MtpResponse<Item>>() {
            @Override
            public void onResponse(@NonNull Call<MtpResponse<Item>> call, @NonNull Response<MtpResponse<Item>> response) {
                final MtpResponse<Item> mtpResponse = response.body();
                if (mtpResponse == null || !mtpResponse.isSuccess()) {
                    BusProvider.getInstance().post(new InventoryResponseNoItemEvent(event.query));
                    return;
                }
                BusProvider.getInstance().post(new InventoryResponseSuccessEvent());
                List<Item> data = mtpResponse.getData();
                for (Item item : data) {
                    BusProvider.getInstance().post(new InventoryResponseEmitOneEvent(item));
                }
                BusProvider.getInstance().post(new InventoryResponseEmitFooterEvent());
            }


            @Override
            public void onFailure(@NonNull Call<MtpResponse<Item>> call, @NonNull Throwable t) {
                BusProvider.getInstance().post(new InventoryResponseServerErrorEvent());
            }
        };
    }

    @Subscribe
    public void onInventoryQuery(final StoreInventoryFragment.QueryEvent event) {
        boolean isAnyItem = event.query.trim().isEmpty();
        if (!isAnyItem) {
            queryHistoryDao.insertOrReplace(new QueryHistory(event.query, System.currentTimeMillis() / 1000));
            callFindStoreItems = ApiManager.getService().findItemsInAStore(event.query, mStore.getStoreId(),null, DeviceUtils.getUid(this));
            callFindStoreItems.enqueue(buildSearchCallback(event));
        } else {
            callFindStoreItems = ApiManager.getService().findRandomInAStore(mStore.getStoreId(), null, DeviceUtils.getUid(this));
            callFindStoreItems.enqueue(buildSearchCallback(event));
        }
    }

    @Subscribe
    public void onInventoryItemClicked(InventoryRowClickedEvent event){
        ItemActivity.navigate(this, ItemActivity.ACTIVITY_ORIGIN_STORE,  event.img, event.item, this.mStore);
    }

    @Subscribe
    public void onRequestQueryHistoryEvent(StoreInventoryFragment.RequestQueryHistoryEvent event){
        if (event.query == null || event.query.trim().isEmpty()) {
            BusProvider.getInstance().post(new EventGotQueryHistory(queryHistoryDao.queryBuilder().orderDesc(QueryHistoryDao.Properties.Ts).limit(5).build().list()));
        }else{
            BusProvider.getInstance().post(new EventGotQueryHistory(queryHistoryDao.queryBuilder().where(QueryHistoryDao.Properties.Query.like(String.format("%%%s%%", event.query))).orderDesc(QueryHistoryDao.Properties.Ts).limit(5).build().list()));
        }
    }

    @Override
    public void onTransitionStart(Transition transition) {}

    @Override
    public void onTransitionEnd(Transition transition) {
        mTransitionsCount++;
        if (mTransitionsCount == TRANSITIONS_TO_SWITCH) {
            viewSwitcher.showNext();
            mTransitionsCount = 0;
        }
    }

    public static class InventoryResponseEmitOneEvent {
        Item item;

        InventoryResponseEmitOneEvent(Item item) {
            this.item = item;
        }
    }

    public static class InventoryRowClickedEvent{
        ImageView img;
        Item item;

        InventoryRowClickedEvent(ImageView img, Item item){
            this.img = img;
            this.item = item;
        }
    }

    public static class InventoryResponseEmitFooterEvent {}

    public static class InventoryResponseSuccessEvent {}

    public static class InventoryResponseNoItemEvent {
        String query;

        InventoryResponseNoItemEvent(String query) {
            this.query = query;
        }
    }

    public static class InventoryResponseServerErrorEvent {}

}
