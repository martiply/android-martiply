package com.martiply.android.activities.store;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import com.martiply.android.R;
import com.martiply.android.activities.BusProvider;
import com.martiply.android.util.DeviceUtils;
import com.martiply.model.Img;
import com.martiply.model.Store;
import com.martiply.model.interfaces.AbsImg;

import java.util.ArrayList;
import java.util.Locale;


class StoreInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private Store store;

    private static final int VIEW_TYPE_BASICS = 11;
    private static final int VIEW_TYPE_FEATURES = 23;


    private ArrayList<Integer> items = new ArrayList<>();

    StoreInfoAdapter(Store store, Context context) {
        this.store = store;
        this.context = context;
        items.add(VIEW_TYPE_BASICS);
        if (store.getStory() != null && !store.getStory().trim().isEmpty()) {
            items.add(VIEW_TYPE_FEATURES);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView;
        switch (viewType) {
            case VIEW_TYPE_BASICS:
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_store_info_basics, parent, false);
                return new ViewHolderBasics(convertView);
            case VIEW_TYPE_FEATURES:
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_store_info_feature, parent, false);
                return new ViewHolderFeatures(convertView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderBasics) {
            buildBasics((ViewHolderBasics) holder);
        }else if (holder instanceof ViewHolderFeatures) {
            buildFeatures((ViewHolderFeatures) holder);
        }
    }

    private void buildBasics(final ViewHolderBasics holder) {

        holder.menu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_call:
                                if (DeviceUtils.isTelephonyEnabled(context)) {
                                    Intent i1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + store.getPhone()));
                                    context.startActivity(i1);
                                } else {
                                    BusProvider.getInstance().post(new NoPhoneEvent());
                                }
                                return true;
                            case R.id.menu_gmap:
                                Uri gmmIntentUri = Uri.parse(String.format(Locale.getDefault(), "geo:%.7f,%.7f", store.getLat(), store.getLng()))
                                        .buildUpon()
                                        .appendQueryParameter("q", store.getAddress())
                                        .build();
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                context.startActivity(mapIntent);
                            default:
                                break;
                        }
                        return false;
                    }
                });

                //	        popupMenu.setOnDismissListener(onDismissListener);
                popupMenu.inflate(R.menu.menu_store_info);
                popupMenu.show();
            }
        });


        holder.name.setText(store.getName());
        holder.address.setText(store.getAddress());
        holder.phone.setText(store.getPhone());
        holder.hours.setText(store.getOpen());
    }

    private void buildFeatures(ViewHolderFeatures holder) {
        holder.featureText.setText(store.getStory());
        holder.featureText.setVisibility(View.VISIBLE);
        Img.loadImg(store.getImg(), 0, context, holder.featureImg, R.drawable.wp_crumpled_paper, R.drawable.wp_crumpled_paper, AbsImg.Size.m);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position);
    }

    static class NoPhoneEvent {
    }
}
