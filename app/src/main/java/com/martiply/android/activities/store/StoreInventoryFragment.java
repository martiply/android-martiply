package com.martiply.android.activities.store;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.widget.ActionMenuView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.martiply.android.R;
import com.martiply.android.activities.BusProvider;
import com.martiply.android.suggestion.EventGotQueryHistory;
import com.martiply.android.suggestion.SuggestionAdapter;
import com.martiply.android.view.fsv.MyFloatingSearchView;
import com.martiply.model.Store;
import com.mypopsy.drawable.SearchArrowDrawable;
import com.mypopsy.widget.internal.ViewUtils;
import com.squareup.otto.Subscribe;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import org.parceler.Parcels;

//import com.martiply.android.view.myedittext.FloatingSearchView;
//import com.martiply.android.view.myedittext.internal.ViewUtils;

public class StoreInventoryFragment extends Fragment {

    private StoreInventoryAdapter adapter;
    private SuggestionAdapter suggestionAdapter;
    private Unbinder unbinder;
    @BindView(android.R.id.list) RecyclerView list;
    @BindView(R.id.search) MyFloatingSearchView search;

    public static StoreInventoryFragment newInstance(Store store) {
        StoreInventoryFragment storeInventoryFragment = new StoreInventoryFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(StoreActivity.LOAD_STORE, Parcels.wrap(store));
        storeInventoryFragment.setArguments(bundle);
        return storeInventoryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Store store = Parcels.unwrap(getArguments().getParcelable(StoreActivity.LOAD_STORE));
        adapter = new StoreInventoryAdapter(getActivity(), store);
        suggestionAdapter = new SuggestionAdapter();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);
        View rootView = inflater.inflate(R.layout.fragment_store_inventory, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(adapter);
        list.setHasFixedSize(false);
        list.setItemAnimator(new SlideInUpAnimator(new DecelerateInterpolator(1f)));
        list.getItemAnimator().setAddDuration(getResources().getInteger(R.integer.ms_recycler_enter_anim));

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
                startQuery(text.toString());
            }
        });

        search.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_random:
                        BusProvider.getInstance().post(new QueryEvent(""));
                        break;
                    case R.id.menu_clear:
                        search.setText(null);
                        search.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                        break;

                }
                return true;
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                showClearButton(query.length() > 0 && search.isActivated());
                if (search.isActivated()) {
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
                if (focused) {
                    BusProvider.getInstance().post(new RequestQueryHistoryEvent(search.getText().toString()));
                }
            }
        });

        search.setAdapter(suggestionAdapter);

        return rootView;
    }

    private void showClearButton(boolean show) {
        search.getMenu().findItem(R.id.menu_clear).setVisible(show);
    }


    private void showProgressBar(boolean show) {
        search.getMenu().findItem(R.id.menu_progress).setVisible(show);
    }

    private void updateNavigationIcon() {
        Context context = search.getContext();
        Drawable drawable = new SearchArrowDrawable(context);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ViewUtils.getThemeAttrColor(context, R.attr.colorControlNormal));
        search.setIcon(drawable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
        unbinder.unbind();
    }

    @Subscribe
    public void onQuery(final QueryEvent event) {
        showProgressBar(true);
        adapter.clear();
    }

    @Subscribe
    public void onResponseSuccess(StoreActivity.InventoryResponseSuccessEvent event) {
        showProgressBar(false);
    }

    @Subscribe
    public void onResponseServerError(StoreActivity.InventoryResponseServerErrorEvent event) {
        showProgressBar(false);
        adapter.addMessage(getString(R.string.error_network));
    }

    @Subscribe
    public void onResponseNoItem(StoreActivity.InventoryResponseNoItemEvent event) {
        showProgressBar(false);
        adapter.addMessage(getString(R.string.search_notfoundItemInStore, search.getText().toString()));
    }

    @Subscribe
    public void onReceiveResponseOneItem(StoreActivity.InventoryResponseEmitOneEvent event) {
        adapter.addItem(event.item);
    }

    @Subscribe
    public void onReceiveResponseFooter(StoreActivity.InventoryResponseEmitFooterEvent event) {
        adapter.addFooter();
        adapter.notifyDataSetChanged();

    }

    @Subscribe
    public void onRequestSetText(RequestSetTextEvent event) {
        search.setText(event.text);
    }

    @Subscribe
    public void onGotQueryHistory(EventGotQueryHistory event) {
        suggestionAdapter.swap(event.queryHistories);
    }

    @Subscribe
    public void onSuggestionTextClicked(SuggestionAdapter.TextClickEvent event) {
        search.setText(event.text);
        startQuery(event.text);
    }

    @Subscribe
    public void onSuggestionIconClicked(SuggestionAdapter.IconClickEvent event) {
        search.setText(event.text);
    }

    private void startQuery(String text) {
        search.setActivated(false);
        BusProvider.getInstance().post(new QueryEvent(text));
    }

    public static class QueryEvent {
        String query;

        QueryEvent(String query) {
            this.query = query;
        }
    }

    public static class RequestSetTextEvent {
        String text;

        RequestSetTextEvent(String text) {
            this.text = text;
        }

    }

    public static class RequestQueryHistoryEvent {
        String query;

        RequestQueryHistoryEvent(String query) {
            this.query = query;
        }
    }

}
