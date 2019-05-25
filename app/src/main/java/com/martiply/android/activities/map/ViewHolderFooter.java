package com.martiply.android.activities.map;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.martiply.android.R;
import com.martiply.android.util.StringUtils;


public class ViewHolderFooter extends RecyclerView.ViewHolder {
    TextView footerText;

    public ViewHolderFooter(View itemView) {
        super(itemView);
        footerText = (TextView)itemView.findViewById(R.id.footer);
    }

    public void setCopyright(Context context){
        footerText.setText(String.format(context.getString(R.string.copyright_ys), StringUtils.getYear(), context.getString(R.string.app_name)));
    }
}
