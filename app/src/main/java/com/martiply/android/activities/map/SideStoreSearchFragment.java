package com.martiply.android.activities.map;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.widget.ActionMenuView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
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
import com.martiply.android.view.fsv.MyFloatingSearchView;
import com.martiply.model.Img;
import com.martiply.model.Store;
import com.martiply.model.interfaces.AbsImg;
import com.mypopsy.drawable.SearchArrowDrawable;
import com.mypopsy.widget.internal.ViewUtils;
import com.squareup.otto.Subscribe;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import java.util.ArrayList;
import java.util.List;


public class SideStoreSearchFragment extends Fragment {
    private Adapter adapter;
    private List<StoreMarker> master = new ArrayList<>();
    private Unbinder unbinder;
    @BindView(android.R.id.list) RecyclerView list;
    @BindView(R.id.search) MyFloatingSearchView search;

    public static SideStoreSearchFragment newInstance() {
        return new SideStoreSearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new Adapter(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);
        View view = inflater.inflate(R.layout.fragment_side_search_store, container, false);
        unbinder = ButterKnife.bind(this, view);

        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(adapter);
        list.setItemAnimator(new SlideInUpAnimator(new DecelerateInterpolator(1.0f)));
        list.getItemAnimator().setAddDuration(getResources().getInteger(R.integer.ms_recycler_enter_anim));

        search.showLogo(false);
        search.setOnIconClickListener(() -> search.setActivated(!search.isActivated()));
        updateNavigationIcon();
        showProgressBar(false);
        showClearButton(false);

        search.setOnSearchListener(text -> matchString(text.toString()));

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                if (search.isActivated()){
                    if (query.length() == 0) {
                        restoreMaster();
                    } else {
                        showClearButton(true);
                        matchString(query.toString());
                    }
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
            }
        });



        search.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_2items:
                        BusProvider.getInstance().post(new SwitchToItemSearchEvent());
                        break;

                }
                return true;
            }
        });
//        search.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BusProvider.getInstance().post(new StartSearchForStoresEvent());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
        unbinder.unbind();
    }

    private void matchString(String test) {
        BusProvider.getInstance().post(new ListClearEvent());
        adapter.clear();
        for (StoreMarker sm : master) {
            if (sm.store.getName().toLowerCase().contains(test.toLowerCase())) {
                BusProvider.getInstance().post(new RequestAddOneStoreToMapEvent(sm.store));
            }
        }
    }

    private void restoreMaster() {
        BusProvider.getInstance().post(new ListClearEvent());
        adapter.clear();
        for (StoreMarker sm : master) {
            BusProvider.getInstance().post(new RequestAddOneStoreToMapEvent(sm.store));
        }
    }

//    private void clearQuery(){
//        search.setText(null);
//        search.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
//    }

    private void updateNavigationIcon() {
        Context context = search.getContext();
        Drawable drawable = new SearchArrowDrawable(context);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ViewUtils.getThemeAttrColor(context, R.attr.colorControlNormal));
        search.setIcon(drawable);
    }

    private void showProgressBar(boolean show) {
        search.getMenu().findItem(R.id.menu_progress).setVisible(show);
    }

    private void showClearButton(boolean show) {
        search.getMenu().findItem(R.id.menu_clear).setVisible(show);
    }

    @Subscribe
    public void onStoreAreaSearchSuccess(Singular.ScanForStoreResponseSuccessEvent event) {
        showProgressBar(false);
//        search.setVisibility(View.VISIBLE);
//        list.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onStoreAreaReceiveOne(Singular.ScanForStoresEmitOneEvent event) {
        StoreMarker sm = new StoreMarker(StoreMarker.ROW_MAIN, event.store, event.marker);
        adapter.add(sm);
        if (event.isAddToMaster) {
            master.add(sm);
        }
    }

    @Subscribe
    public void onStoreAreaReceiveFooter(Singular.ScanForStoreEmitFooterEvent event){
        adapter.add(new StoreMarker(StoreMarker.ROW_FOOTER));
        adapter.notifyItemRangeInserted(0, adapter.getItemCount() - 1);
    }

    @Subscribe
    public void onStoreAreaNotFound(Singular.ScanForStoresResponseNotFoundEvent event) {
        showProgressBar(false);
        adapter.add(new StoreMarker(StoreMarker.ROW_EMPTY));
        adapter.add(new StoreMarker(StoreMarker.ROW_FOOTER));

    }

    @Subscribe
    public void onStoreAreaSearchFailed(Singular.ScanForStoreResponseFailedEvent event) {
        showProgressBar(false);
        adapter.add(new StoreMarker(StoreMarker.ROW_SERVER_ERROR));
        adapter.add(new StoreMarker(StoreMarker.ROW_FOOTER));

    }

    @Subscribe
    public void onNewSearchByDoubleTap(SideStoreSearchFragment.StartSearchForStoresEvent event) {
        adapter.clear();
        master.clear();
        showProgressBar(true);
    }

    @Subscribe
    public void onDoubleTapOnMap(Singular.ManualPositionSetEvent event) {
        BusProvider.getInstance().post(new StartSearchForStoresEvent());
    }


    private static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<StoreMarker> sms = new ArrayList<>();
        private Context context;

        Adapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemViewType(int position) {
            return sms.get(position).rowType;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case StoreMarker.ROW_FOOTER:
                    View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_copyright, parent, false);
                    return new ViewHolderFooter(convertView);
                case StoreMarker.ROW_SERVER_ERROR:
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sm_map_item_empty, parent, false);
                    return new ViewHolderServerError(convertView);
                case StoreMarker.ROW_EMPTY:
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sm_map_item_empty, parent, false);
                    return new ViewHolderEmpty(convertView);
                default:
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sm_map_store_search, parent, false);
                    return new ViewHolder(convertView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ViewHolder){
                ViewHolder h = (ViewHolder) holder;
                final StoreMarker sm = sms.get(position);
                h.t1.setText(sm.store.getName());
                h.t2.setText(sm.store.getAddress());
                Img.loadImg(sm.store.getImg(), 0, context, h.icon,  R.drawable.wp_crumpled_paper, R.drawable.wp_crumpled_paper, AbsImg.Size.xl);
                h.top.setOnClickListener(v -> BusProvider.getInstance().post(new DrawerItemClickedEvent(sm.marker)));
            }else if (holder instanceof  ViewHolderEmpty ){
                ViewHolderEmpty h =(ViewHolderEmpty) holder;
                h.empty.setText(R.string.map_store_empty);
            }else if (holder instanceof  ViewHolderServerError){
                ViewHolderServerError h = (ViewHolderServerError)holder;
                h.empty.setText(R.string.error_server);
            }
            else if (holder instanceof ViewHolderFooter){
                ViewHolderFooter h = (ViewHolderFooter)holder;
                h.setCopyright(context);
            }
        }

        @Override
        public int getItemCount() {
            return sms.size();
        }

        public void clear() {
            sms.clear();
            notifyDataSetChanged();
        }

        public void add(StoreMarker sm) {
            sms.add(sm);
        }
    }

    static class ViewHolderEmpty extends RecyclerView.ViewHolder{
        @BindView(android.R.id.empty) TextView empty;

         ViewHolderEmpty(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ViewHolderServerError extends RecyclerView.ViewHolder{
        @BindView(android.R.id.empty) TextView empty;

         ViewHolderServerError(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

     static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text) TextView t1;
        @BindView(R.id.text2) TextView t2;
        @BindView(R.id.icon) ImageView icon;
        @BindView(R.id.top) LinearLayout top;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public static class StoreMarker {
        static final int ROW_MAIN = 10;
        static final int ROW_EMPTY = 7;
        static final int ROW_SERVER_ERROR = 20;
        static final int ROW_FOOTER = 3;
        int rowType;
        Store store;
        Marker marker;

        StoreMarker(int rowType, Store store, Marker marker) {
            this.rowType = rowType;
            this.store = store;
            this.marker = marker;
        }
         StoreMarker(int rowType) {
            this.rowType = rowType;
        }

    }

    public static class DrawerItemClickedEvent {
        Marker marker;

        DrawerItemClickedEvent(Marker marker) {
            this.marker = marker;
        }
    }

    public static class ListClearEvent {
    }

    public static class RequestAddOneStoreToMapEvent {
        Store store;

        RequestAddOneStoreToMapEvent(Store store) {
            this.store = store;
        }
    }

    public static class StoreRowClickedEvent {
        int position;

        public StoreRowClickedEvent(int position) {
            this.position = position;
        }
    }

    public static class StartSearchForStoresEvent {
    }

    public static class SwitchToItemSearchEvent {
    }
}