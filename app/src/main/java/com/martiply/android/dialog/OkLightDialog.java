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

public class OkLightDialog extends DialogFragment {
    private static final String CONTENT = "content";
    private static final String DIALOG_ID = "id";
    private Unbinder unbinder;

    @BindView(R.id.text) TextView text;
    @OnClick(R.id.ok)
    public void onOk(){
        BusProvider.getInstance().post(new DismissEvent(getArguments().getInt(DIALOG_ID)));
        dismiss();
    }

    public static OkLightDialog newInstance(String content, int dialogId) {
        OkLightDialog okLightDialog = new OkLightDialog();
        Bundle bundle = new Bundle();
        bundle.putString(CONTENT, content);
        bundle.putInt(DIALOG_ID, dialogId);
        okLightDialog.setArguments(bundle);
        return okLightDialog;
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
        View rootView = inflater.inflate(R.layout.dialog_light_ok, container);
        unbinder = ButterKnife.bind(this, rootView);
        text.setText((getArguments().getString(CONTENT)));
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static class DismissEvent{
        public int id;
        public DismissEvent(int id){
            this.id = id;
        }

    }
}
