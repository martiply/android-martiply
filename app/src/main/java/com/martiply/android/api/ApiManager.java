package com.martiply.android.api;

import com.martiply.android.Cfg;
import com.martiply.model.IPP;
import com.martiply.model.Item;
import com.martiply.model.MtpResponse;
import com.martiply.model.Store;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ApiManager {

     public interface MartiplyService {

        @GET(Cfg.PATH_FIND_STORES)
        Call<MtpResponse<Store>> getAreaStores(
                @Query(Cfg.PARAM_LAT) double lat,
                @Query(Cfg.PARAM_LNG) double lng,
                @Query(Cfg.PARAM_RADIUS) float radius,
                @Query(Cfg.PARAM_APIKEY) String apikey,
                @Query(Cfg.PARAM_DEVICE_ID) String did
        );

        @GET(Cfg.PATH_SEARCH_AREA)
        Call<MtpResponse<IPP>> getAreaItems(
                @Query(Cfg.PARAM_LAT) double lat,
                @Query(Cfg.PARAM_LNG) double lng,
                @Query(Cfg.PARAM_RADIUS) float radius,
                @Query(Cfg.PARAM_KEYWORD) String keyword,
                @Query(Cfg.PARAM_CATEGORY) String category,
                @Query(Cfg.PARAM_APIKEY) String apikey,
                @Query(Cfg.PARAM_DEVICE_ID) String did
        );

        @GET(Cfg.PATH_GET_STORE)
        Call<MtpResponse<Store>> getStore(
                @Query(Cfg.PARAM_STORE_ID) int storeId,
                @Query(Cfg.PARAM_APIKEY) String apikey,
                @Query(Cfg.PARAM_DEVICE_ID) String did
        );

        @GET(Cfg.PATH_SEARCH_STORE)
        Call<MtpResponse<Item>> findItemsInAStore(
                @Query(Cfg.PARAM_KEYWORD) String keyword,
                @Query(Cfg.PARAM_STORE_ID) int storeId,
                @Query(Cfg.PARAM_APIKEY) String apikey,
                @Query(Cfg.PARAM_DEVICE_ID) String did
        );

         @GET(Cfg.PATH_STORE_RANDOM)
         Call<MtpResponse<Item>> findRandomInAStore(
                 @Query(Cfg.PARAM_STORE_ID) int storeId,
                 @Query(Cfg.PARAM_APIKEY) String apikey,
                 @Query(Cfg.PARAM_DEVICE_ID) String did
         );
    }

    private static final Retrofit REST_ADAPTER = new Retrofit.Builder()
            .baseUrl(Cfg.API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static final MartiplyService MARTIPLY_SERVICE = REST_ADAPTER.create(MartiplyService.class);

    public static MartiplyService getService() {
        return MARTIPLY_SERVICE;
    }
}
