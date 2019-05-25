package com.martiply.android.activities.store;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.martiply.android.R;
import com.martiply.android.activities.BusProvider;
import com.martiply.model.Store;
import org.parceler.Parcels;

public class StoreInfoFragment extends Fragment {
    private StoreInfoAdapter adapter;
    private Unbinder unbinder;
    @BindView(android.R.id.list) RecyclerView list;

    public static StoreInfoFragment newInstance(Store store){
        StoreInfoFragment storeInfoFragment = new StoreInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(StoreActivity.LOAD_STORE, Parcels.wrap(store));
        storeInfoFragment.setArguments(bundle);
        return storeInfoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Store store = Parcels.unwrap(getArguments().getParcelable(StoreActivity.LOAD_STORE));
        adapter = new StoreInfoAdapter(store, getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);
        View rootView = inflater.inflate(R.layout.fragment_store_info, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(linearLayoutManager);
        list.setAdapter(adapter);
        list.setItemAnimator(new DefaultItemAnimator());
        list.setHasFixedSize(true);
    }


}