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
import butterknife.OnClick;
import butterknife.Unbinder;
import com.martiply.android.R;
import com.martiply.android.activities.BusProvider;

public class OkCancelDarkDialog extends DialogFragment {
    private Unbinder unbinder;
    @BindView(R.id.text)
    TextView text;
    @OnClick(R.id.cancel)
    public void onCancel(){
        dismiss();
    }
    @OnClick(R.id.ok)
    public void onOk(){
        BusProvider.getInstance().post(new OkEvent());
        dismiss();
    }

    public static OkCancelDarkDialog newInstance(String content) {
        OkCancelDarkDialog okCancelDarkDialog = new OkCancelDarkDialog();
        Bundle bundle = new Bundle();
        bundle.putString("content", content);
        okCancelDarkDialog.setArguments(bundle);
        return okCancelDarkDialog;
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
        View rootView = inflater.inflate(R.layout.dialog_dark_ok_cancel, container);
        unbinder = ButterKnife.bind(this, rootView);
        text.setText(getArguments().getString("content"));
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static class OkEvent{}
}
