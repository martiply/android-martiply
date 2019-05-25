package com.martiply.android.activities.store;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.martiply.android.R;
import com.martiply.android.api.ApiManager;
import com.martiply.android.util.DeviceUtils;
import com.martiply.model.MtpResponse;
import com.martiply.model.Store;
import org.parceler.Parcels;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreStoreActivity extends AppCompatActivity {
    private Call<MtpResponse<Store>> call ;
    private static final int[] MS_PATHVIEW_DURATION = {200, 2500, 1000};
    private int storeId;
    private Unbinder unbinder;

    @OnClick(R.id.back) public void backButton(){
        finish();
    }
    @OnClick(R.id.refresh)
    public void refresh(){
        setContentView(R.layout.activity_store_preload);
        ButterKnife.bind(PreStoreActivity.this);
        getStoreInfo();
    }
    @BindView(R.id.pb) LinearLayout pb;
    @BindView(R.id.snippet_network_error) LinearLayout networkErrorSnippet;


    public static void navigate(Context context, int storeId){
        Intent intent = new Intent(context, PreStoreActivity.class);
        intent.putExtra(StoreActivity.LOAD_STORE_ID, storeId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_preload);
        unbinder = ButterKnife.bind(this);
        storeId = getIntent().getIntExtra(StoreActivity.LOAD_STORE_ID, 0);
        call = ApiManager.getService().getStore(storeId, null, DeviceUtils.getUid(this));
        getStoreInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (call != null){
            call.cancel();
        }
    }

    private void getStoreInfo() {
        if (!DeviceUtils.isNetworkOnline(getBaseContext())) {
            pb.setVisibility(View.GONE);
            networkErrorSnippet.setVisibility(View.VISIBLE);
            return;
        }
        pb.setVisibility(View.VISIBLE);
        networkErrorSnippet.setVisibility(View.GONE);

        final long startTime = System.currentTimeMillis();

        call.enqueue(new Callback<MtpResponse<Store>>() {
            @Override
            public void onResponse(@NonNull Call<MtpResponse<Store>> cb, @NonNull final Response<MtpResponse<Store>> response) {
                long restAnimTime = getRestAnimTime(System.currentTimeMillis(), startTime);

                if (restAnimTime > 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handleResponse(response.body());
                        }
                    }, restAnimTime + MS_PATHVIEW_DURATION[2]);
                } else {
                    handleResponse(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MtpResponse<Store>> cb, @NonNull Throwable t) {
                long restAnimTime = getRestAnimTime(System.currentTimeMillis(), startTime);
                if (restAnimTime > 0) {
                    new Handler()
                        .postDelayed(new Runnable() {
                              @Override
                              public void run() {
                                  pb.setVisibility(View.GONE);
                                  networkErrorSnippet.setVisibility(View.VISIBLE);
                              }
                          },
                        restAnimTime + MS_PATHVIEW_DURATION[2]);
                } else {
                    pb.setVisibility(View.GONE);
                    networkErrorSnippet.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void handleResponse(MtpResponse<Store> mtpResponse) {
        if (mtpResponse == null || !mtpResponse.isSuccess()) {
            pb.setVisibility(View.GONE);
            networkErrorSnippet.setVisibility(View.VISIBLE);
            return;
        }
        if (mtpResponse.getData().isEmpty()) {
            pb.setVisibility(View.GONE);
            networkErrorSnippet.setVisibility(View.VISIBLE);
            return;
        }
        Store store = mtpResponse.getData().get(0);
        StoreActivity.navigate(this, storeId, Parcels.wrap(store));
        finish();
    }

    private long getRestAnimTime(long currentMs, long startTime) {
        return (currentMs - MS_PATHVIEW_DURATION[0] - MS_PATHVIEW_DURATION[1] - startTime) * -1;
    }
}
