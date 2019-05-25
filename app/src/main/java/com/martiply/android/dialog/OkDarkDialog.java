package com.martiply.android.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.martiply.android.R;
import com.martiply.android.activities.BusProvider;

public class OkDarkDialog extends DialogFragment {

    private Unbinder unbinder;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.ok)
    TextView ok;

    public static OkDarkDialog newInstance(String content) {
        OkDarkDialog okDarkDialog = new OkDarkDialog();
        Bundle bundle = new Bundle();
        bundle.putString("content", content);
        okDarkDialog.setArguments(bundle);
        return okDarkDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_dark_ok, container);
        unbinder = ButterKnife.bind(this, rootView);
        text.setText(getArguments().getString("content"));
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getInstance().post(new DismissEvent());
                dismiss();
            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static class DismissEvent {
    }
}
