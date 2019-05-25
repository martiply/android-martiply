package com.martiply.android.activities.store;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.martiply.android.R;

public class ViewHolderBasics extends RecyclerView.ViewHolder {
    @BindView(R.id.name) public TextView name;
    @BindView(R.id.address) public TextView address;
    @BindView(R.id.hours) public TextView hours;
    @BindView(R.id.phone) public TextView phone;
    @BindView(R.id.menu) public ImageView menu;

    public ViewHolderBasics(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
