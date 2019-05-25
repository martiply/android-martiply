package com.martiply.android.activities.store;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.martiply.android.R;

public class ViewHolderFeatures extends RecyclerView.ViewHolder {
    @BindView(R.id.feature_img) ImageView featureImg;
    @BindView(R.id.feature_text) TextView featureText;

    public ViewHolderFeatures(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
