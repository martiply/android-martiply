package com.martiply.android.activities.permission;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.Unbinder;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.martiply.android.R;
import com.martiply.android.activities.map.Singular;

public class Permission extends AppCompatActivity {
    private static final int REQUEST_FINE_LOCATION_CODE= 43;
    private MaterialDialog retryDialog;
    private MaterialDialog criticalDialog;


    public static void navigate(Context ctx){
        Intent i = new Intent(ctx, Permission.class);
        ctx.startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        MaterialDialog.Builder builderCurious = new MaterialDialog.Builder(this)
                .title(R.string.permission_rationale_title_retry)
                .content(R.string.permission_rationale_body_retry)
                .positiveText(R.string.retry)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        firePermissionDialog();
                    }
                });
        retryDialog = builderCurious.build();

        MaterialDialog.Builder builderCritical = new MaterialDialog.Builder(this)
                .title(R.string.permission_rationale_title_critical)
                .content(R.string.permission_rationale_body_critical)
                .positiveText(R.string.settings)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", Permission.this.getPackageName(), null);
                        intent.setData(uri);
                        Permission.this.startActivity(intent);
                        Permission.this.finish();
                    }
                });

        criticalDialog = builderCritical.build();

        firePermissionDialog();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION_CODE){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Singular.navigate(this);
                finish();
            } else {
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.ACCESS_FINE_LOCATION);
                if (showRationale){ // retry reject
                    retryDialog.show();
                }else{ // critical reject: user checked don't ask this again
                    criticalDialog.show();
                }
            }
        }
    }

    private void firePermissionDialog(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
