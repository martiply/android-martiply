package com.martiply.android.activities.map;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.google.android.gms.maps.model.Marker;
import com.martiply.android.R;
import com.martiply.android.activities.BusProvider;
import com.martiply.android.suggestion.EventGotQueryHistory;
import com.martiply.android.suggestion.SuggestionAdapter;
import com.martiply.android.util.StringUtils;
import com.martiply.android.view.fsv.MyFloatingSearchView;
import com.martiply.model.Img;
import com.martiply.model.Item;
import com.martiply.model.Store;
import com.martiply.model.interfaces.AbsImg;
import com.martiply.model.interfaces.IItem;
import com.mypopsy.drawable.SearchArrowDrawable;
import com.mypopsy.widget.internal.ViewUtils;
import com.squareup.otto.Subscribe;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import java.util.ArrayList;

public class SideItemSearchFragment extends Fragment {
    private Adapter adapter;
    private SuggestionAdapter suggestionAdapter;
    private String categoryFilter;
    private Unbinder unbinder;
    @BindView(android.R.id.list) RecyclerView list;
    @BindView(R.id.search) MyFloatingSearchView search;
    @BindView(R.id.guide_anchor) View guide;

    public static SideItemSearchFragment newInstance(){
        return new SideItemSearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new Adapter(getActivity());
        suggestionAdapter = new SuggestionAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);
        View view = inflater.inflate(R.layout.fragment_side_search_item, container, false);
        unbinder = ButterKnife.bind(this, view);
        BusProvider.getInstance().post(new ResetPopUpRadio());
        list.setLayoutManager( new LinearLayoutManager(getActivity()));
        list.setAdapter(adapter);
        list.setItemAnimator(new SlideInUpAnimator(new DecelerateInterpolator(1.0f)));
        list.getItemAnimator().setAddDuration(getResources().getInteger(R.integer.ms_recycler_enter_anim));

        setCategoryFilter(0);
        search.showLogo(false);
        search.setOnIconClickListener(new MyFloatingSearchView.OnIconClickListener() {
            @Override
            public void onNavigationClick() {
                search.setActivated(!search.isActivated());
            }
        });
        updateNavigationIcon();
        showProgressBar(false);
        showClearButton(false);

        search.setOnSearchListener(new MyFloatingSearchView.OnSearchListener() {
            @Override
            public void onSearchAction(CharSequence text) {
                search.setActivated(false);
                startQuery(text.toString());
            }
        });

        search.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
//                case R.id.menu_filter_all:
//                case R.id.menu_filter_culinary:
//                case R.id.menu_filter_product:
//                    item.setChecked(true);
//                    categoryFilter = setCategoryFilter(item.getItemId());
//                    break;
//                case R.id.menu_2store:
//                    BusProvider.getInstance().post(new SwitchToScanForStoresEvent());
//                    break;
                case R.id.menu_clear:
                    clearQuery();
                    break;
            }
            return true;
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                showClearButton(query.length() > 0 && search.isActivated());
                if (search.isActivated()){
                    BusProvider.getInstance().post(new RequestQueryHistoryEvent(search.getText().toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        search.setOnSearchFocusChangedListener(new MyFloatingSearchView.OnSearchFocusChangedListener() {
            @Override
            public void onFocusChanged(final boolean focused) {
                boolean textEmpty = search.getText().length() == 0;
                showClearButton(focused && !textEmpty);
                if (focused){
                    BusProvider.getInstance().post(new RequestQueryHistoryEvent(search.getText().toString()));
                }
            }
        });

        search.setAdapter(suggestionAdapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void showClearButton(boolean show) {
        search.getMenu().findItem(R.id.menu_clear).setVisible(show);
    }

    private void showProgressBar(boolean show) {
        search.getMenu().findItem(R.id.menu_progress).setVisible(show);
    }

    private String setCategoryFilter(int menuItemId){
        switch (menuItemId){
//            case R.id.menu_filter_product: return Item.RootCategory.product.toString();
//            case R.id.menu_filter_culinary : return Item.RootCategory.culinary.toString();
            default: return Item.RootCategory.all.toString();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
        unbinder.unbind();
    }

    private static class Adapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {
        static final int ROW_MAIN = 10;
        static final int ROW_FOOTER = 3;
        static final int ROW_EMPTY = 5;
        private ArrayList<StoreItemMarker> sims = new ArrayList<>();
        private Context context;

         Adapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemViewType(int position) {
            return sims.get(position).rowType;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType){
                case ROW_FOOTER:
                    View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_copyright, parent, false);
                    return new ViewHolderFooter(convertView);
                case ROW_EMPTY:
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sm_map_item_empty, parent, false);
                    return new ViewHolderEmpty(convertView);
                default:
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sm_map_item_search, parent, false);
                    return new ViewHolder(convertView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final StoreItemMarker sim = sims.get(position);
            final int pos = position;
            if (holder instanceof ViewHolder ){
                final ViewHolder h = (ViewHolder)holder;
                h.name.setText(sim.item.getName());
                Img.loadImg(sim.item.getImg(), 0, context, h.icon, R.drawable.wp_crumpled_paper, R.drawable.wp_crumpled_paper, AbsImg.Size.m);
                h.icon.setOnClickListener(v -> BusProvider.getInstance().post(new ItemActivityRequestEvent(sim.item, sim.store, h.icon)));
                h.price.setText(StringUtils.getFriendlyCurrencyString(context, sim.store.getCurrency(), sim.item.getPrice()));
                h.store.setText(sim.store.getName());
                h.top.setOnClickListener(v -> BusProvider.getInstance().post(new DrawerItemClickedEvent(sim, pos, sim.store)));
            }else if (holder instanceof ViewHolderFooter){
                ViewHolderFooter h = (ViewHolderFooter)holder;
                h.setCopyright(context);
            }else if (holder instanceof  ViewHolderEmpty){
                ViewHolderEmpty h = (ViewHolderEmpty)holder;
                h.message.setText(sim.message);
            }
        }

        @Override
        public int getItemCount() {
            return sims.size();
        }

        public void clear(){
            sims.clear();
            notifyDataSetChanged();
        }

        public void add(StoreItemMarker sim){
            sims.add(sim);
            notifyItemInserted(sims.size() - 1);
        }

         void addMessage(String message){
            sims.add(new StoreItemMarker(message));
            notifyItemInserted(sims.size() - 1);
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon) ImageView icon;
        @BindView(R.id.name) TextView name;
        @BindView(R.id.price) TextView price;
        @BindView(R.id.store) TextView store;
        @BindView(R.id.top) LinearLayout top;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ViewHolderEmpty extends RecyclerView.ViewHolder{
        @BindView(android.R.id.empty)
        TextView message;

         ViewHolderEmpty(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void startQuery(String query){
        search.setText(query);
        showProgressBar(true);
        adapter.clear();
        BusProvider.getInstance().post(new QueryEvent(query, categoryFilter));
    }

    @Subscribe
    public void onQueryResponseSuccess(Singular.AreaItemsResponseSuccessEvent event){
        showProgressBar(false);
    }

    @Subscribe
    public void onQueryResponseServerError(Singular.AreaItemServerErrorEvent event){
        showProgressBar(false);
        adapter.addMessage(getString(R.string.error_server));
    }

    @Subscribe
    public void onQueryResponseNotFound(Singular.AreaItemsResponseNotFoundEvent event){
        showProgressBar(false);
        adapter.addMessage(String.format(getString(R.string.search_notfoundItem), event.query));
    }

    @Subscribe
    public void onReceiveOneItem(Singular.AreaItemEmitsOneEvent event){
        for (Item item : event.sw.items) {
            adapter.add(new StoreItemMarker(Adapter.ROW_MAIN, event.sw.store, item, event.sw.marker, event.sw.items.size()));
        }
        GuideHelper.showItemTextImage(getActivity());
    }

    @Subscribe
    public void onReceiveFooter(Singular.AreaItemEmitsFooterEvent event){
        adapter.add(new StoreItemMarker(Adapter.ROW_FOOTER));
    }

    @Subscribe
    public void onManualPositionSet(Singular.ManualPositionSetEvent event){
        adapter.clear();
        if (search.getText() == null || search.getText().toString().trim().length() == 0){return;}
        startQuery(search.getText().toString());
    }

    @Subscribe
    public void onOverlayMultipleItemPanelClicked(ItemOverlayFragment.MultipleItemPanelClickedEvent event){
        list.smoothScrollToPosition(event.listIndex);
    }

    @Subscribe
    public void onGotQueryHistory(EventGotQueryHistory event){
        suggestionAdapter.swap(event.queryHistories);
    }

//    @Subscribe
//    public void onOverlaySingleItemPanelClicked(ItemOverlayFragment.ItemPanelClickedEvent event){
//        Marker marker = adapter.getItem(event.listIndex).marker;
//        BusProvider.getInstance().post(new RequestZoomOnMarkerEvent(marker));
//    }

    @Subscribe
    public void onSimilarSearchRequested(Singular.SearchSimilarEvent event){
        search.setActivated(false);
        search.setText(event.category);
        startQuery(event.category);
    }

    @Subscribe
    public void onSuggestionTextClicked(SuggestionAdapter.TextClickEvent event){
        search.setActivated(false);
        startQuery(event.text);
    }

    @Subscribe
    public void onSuggestionIconClicked(SuggestionAdapter.IconClickEvent event){
         search.setText(event.text);
    }

    public static class QueryEvent {
        String query;
        String category;

        QueryEvent(String query, String category) {
            this.category = category != null ? category : Item.RootCategory.all.toString();
            this.query = query;
        }
    }

    public static class DrawerItemClickedEvent {
        StoreItemMarker storeItemMarker;
        int listIndex;
        Store store;

        DrawerItemClickedEvent(StoreItemMarker itemMarker, int listIndex, Store store) {
            this.storeItemMarker = itemMarker;
            this.listIndex = listIndex;
            this.store = store;
        }
    }

    public static class ItemActivityRequestEvent {
        Item item;
        ImageView origin;
        Store store;

        ItemActivityRequestEvent(Item item, Store store, ImageView origin){
            this.item = item;
            this.origin = origin;
            this.store = store;
        }
    }

    public static class SwitchToScanForStoresEvent {}

    public static class StoreItemMarker{
        int rowType;
        Store store;
        Item item;
        Marker marker;
        int nItem;
        String message;

        StoreItemMarker(int rowType, Store store, Item item, Marker marker, int nItem) {
            this.store = store;
            this.item = item;
            this.marker = marker;
            this.nItem = nItem;
            this.rowType = rowType;
        }

        StoreItemMarker(int rowType) {
            this.rowType = rowType;
        }

         StoreItemMarker(String message){
            this.rowType = Adapter.ROW_EMPTY;
            this.message = message;
        }
    }

    public static class MarkerClickedEvent{
        StoreWrap sw;

         MarkerClickedEvent(StoreWrap sw) {
            this.sw = sw;
        }
    }
    public static class MapClickedEvent{}

    public static class RequestZoomOnMarkerEvent {
        Marker marker;

        public RequestZoomOnMarkerEvent(Marker marker) {
            this.marker = marker;
        }
    }

    public static class ResetPopUpRadio{}

    public static class RequestQueryHistoryEvent{
        String query;

        RequestQueryHistoryEvent(String query) {
            this.query = query;
        }
    }


    private void clearQuery(){
        search.setText(null);
        search.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
    }

    private void updateNavigationIcon() {
        Context context = search.getContext();
        Drawable drawable = new SearchArrowDrawable(context);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ViewUtils.getThemeAttrColor(context, R.attr.colorControlNormal));
        search.setIcon(drawable);
    }

}
