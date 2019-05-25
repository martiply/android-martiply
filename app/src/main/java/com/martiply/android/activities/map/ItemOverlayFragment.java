package com.martiply.android.activities.map;

import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.martiply.android.R;
import com.martiply.android.activities.BusProvider;
import com.martiply.android.util.StringUtils;
import com.martiply.model.Img;
import com.martiply.model.Item;
import com.martiply.model.Store;
import com.martiply.model.interfaces.AbsImg;
import com.squareup.otto.Subscribe;

public class ItemOverlayFragment extends Fragment {
    private Unbinder unbinder;
    @BindView(R.id.text) TextView text;
    @BindView(R.id.text2) TextView text2;
    @BindView(R.id.text3) TextView text3;
    @BindView(R.id.img) ImageView img;
    @BindView(R.id.single) LinearLayout single;
    @BindView(R.id.multiple) LinearLayout multiple;
    @BindView(R.id.panel) LinearLayout panel;

    public static ItemOverlayFragment newInstance() {
        return new ItemOverlayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_item_overlay, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
        unbinder.unbind();
    }


    @Subscribe
    public void onItemClickFromSideSearch(SideItemSearchFragment.DrawerItemClickedEvent event) {
        buildSingleItemPanel(event.storeItemMarker.item, event.listIndex, event.store);
    }

    private void buildSingleItemPanel(final Item item, final int listIndex, final Store store){
        text.setText(item.getName());
        text2.setText(StringUtils.getFriendlyCurrencyString(getActivity(), store.getCurrency(), item.getPrice()));
        Img.loadImg(item.getImg(), 0, getActivity(), img, R.drawable.wp_crumpled_paper, R.drawable.wp_crumpled_paper, AbsImg.Size.m);

        single.setVisibility(View.VISIBLE);
        multiple.setVisibility(View.GONE);
        panel.setVisibility(View.VISIBLE);
        panel.setOnClickListener(v -> BusProvider.getInstance().post(new SideItemSearchFragment.ItemActivityRequestEvent(item, store, img)));
    }

    private void buildMultipleItemPanel(int nItem, final int listIndex){
        single.setVisibility(View.GONE);
        multiple.setVisibility(View.VISIBLE);
        text3.setText(getString(R.string.n_items_sold_here_by_query, nItem));
        panel.setVisibility(View.VISIBLE);
        panel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BusProvider.getInstance().post(new MultipleItemPanelClickedEvent(listIndex));
            }
        });
    }

    @Subscribe
    public void onQuery(SideItemSearchFragment.QueryEvent event) {
        panel.setVisibility(View.GONE);
    }

    @Subscribe
    public void onMarkerClicked(SideItemSearchFragment.MarkerClickedEvent event){
        int size = event.sw.items.size();
        if (size == 1){
            buildSingleItemPanel(event.sw.items.get(0), event.sw.listIndex, event.sw.store);
        }else{
            buildMultipleItemPanel(size, event.sw.listIndex);
        }
    }

    @Subscribe
    public void onSwitchToScanForStores(SideItemSearchFragment.SwitchToScanForStoresEvent event) {
        panel.setVisibility(View.GONE);
    }

    @Subscribe
    public void onMapClicked(SideItemSearchFragment.MapClickedEvent event){
        panel.setVisibility(View.GONE);
    }

//    public static class ItemPanelClickedEvent {
//        int listIndex;
//
//        public ItemPanelClickedEvent(int listIndex) {
//            this.listIndex = listIndex;
//        }
//    }

    public static class MultipleItemPanelClickedEvent{
        int listIndex;
        MultipleItemPanelClickedEvent(int listIndex) {
            this.listIndex = listIndex;
        }
    }

}
