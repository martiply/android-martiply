package com.martiply.android.activities.store;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.martiply.android.R;
import com.martiply.android.activities.map.Singular;
import com.martiply.android.util.StringUtils;
import com.martiply.android.view.MyFab;
import com.martiply.android.view.SquareImageView;
import com.martiply.model.*;
import com.martiply.model.interfaces.AbsImg;
import org.parceler.Parcels;

public class ItemActivity extends AppCompatActivity {
    public static final String REQUEST_SEARCH_SIMILAR_CATEGORY = "REQUEST_SEARCH_SIMILAR_CATEGORY";
    private static final String LOAD_ITEM = "LOAD_ITEM";
    private static final String LOAD_STORE = "LOAD_STORE";
    private static final String LOAD_ORIGIN_ACTIVITY = "LOAD_ORIGIN_ACTIVITY";
    public static final int ACTIVITY_ORIGIN_SINGULAR = 34;
    public static final int ACTIVITY_ORIGIN_STORE = 98;

    private Unbinder unbinder;
    @BindView(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.img) SquareImageView img;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.price) TextView price;
    @BindView(R.id.store_name) TextView storeName;
    @BindView(R.id.description_head) View descriptionHead;
    @BindView(R.id.description) TextView description;
    @BindView(R.id.fab) MyFab fab;
    @BindView(R.id.fab_sheet) View fabSheet;
    @BindView(R.id.overlay) View fabOverlay;
    @BindView(R.id.apparel_divider) View apparelDivider;
    @BindView(R.id.apparel_gender_head) TextView apparelGenderHead;
    @BindView(R.id.apparel_gender) TextView apparelGender;
    @BindView(R.id.apparel_age_head) TextView apparelAgeHead;
    @BindView(R.id.apparel_age) TextView apparelAge;
    @BindView(R.id.apparel_size_head) TextView apparelSizeHead;
    @BindView(R.id.apparel_size) TextView apparelSize;
    @BindView(R.id.apparel_color_head) TextView apparelColorHead;
    @BindView(R.id.apparel_color) TextView apparelColor;
    @BindView(R.id.apparel_material_head) TextView apparelMaterialHead;
    @BindView(R.id.apparel_material) TextView apparelMaterial;
    @BindView(R.id.apparel_feature_head) TextView apparelFeatureHead;
    @BindView(R.id.apparel_feature) TextView apparelFeature;
    @BindView(R.id.item_menu_search_similar) TextView menuSearchSimilar;
    @BindView(R.id.item_menu_to_store) TextView menuToStore;

    private MaterialSheetFab materialSheetFab;
    private Item item;
    private Store store;

    public static void navigate(AppCompatActivity activity, int originActivity, View transitionImage, Item item, Store store) {
        Intent intent = new Intent(activity, ItemActivity.class);
        intent.putExtra(LOAD_ITEM, Parcels.wrap(item));
        intent.putExtra(LOAD_STORE, Parcels.wrap(store));
        intent.putExtra(LOAD_ORIGIN_ACTIVITY, originActivity);
        String transitionName = activity.getString(R.string.trans_gallery_img);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, transitionName);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        unbinder = ButterKnife.bind(this);
        initActivityTransitions();
        this.item = Parcels.unwrap(getIntent().getParcelableExtra(LOAD_ITEM));
        this.store = Parcels.unwrap(getIntent().getParcelableExtra(LOAD_STORE));

        ViewCompat.setTransitionName(appBarLayout, getString(R.string.trans_gallery_img));

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout.setTitle(getString(R.string.act_item_title));
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));

        int fabColor = ContextCompat.getColor(this, android.R.color.holo_orange_light);
        int sheetColor = ContextCompat.getColor(this, android.R.color.white);
        fab.setBackgroundTintList(ColorStateList.valueOf(fabColor));
        materialSheetFab = new MaterialSheetFab<>(fab, fabSheet, fabOverlay, sheetColor, fabColor);
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.google_blue_dk)); //primary
        collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(this, R.color.google_blue_dk)); //primary dark
        Img.loadImg(item.getImg(), 0, this, img, R.drawable.wp_crumpled_paper, R.drawable.wp_crumpled_paper, AbsImg.Size.xl);
        final int originActivity = getIntent().getIntExtra(LOAD_ORIGIN_ACTIVITY, -1);
        menuSearchSimilar.setOnClickListener(v -> {
            Intent i = new Intent(ItemActivity.this, originActivity == ACTIVITY_ORIGIN_SINGULAR ? Singular.class : StoreActivity.class);
            i.putExtra(REQUEST_SEARCH_SIMILAR_CATEGORY, item.getCategory());
            onBackPressed();
            startActivity(i);
        });

        menuToStore.setOnClickListener(v -> {
            materialSheetFab.hideSheet();
            onBackPressed();
            switch (originActivity){
                case ACTIVITY_ORIGIN_SINGULAR: PreStoreActivity.navigate(ItemActivity.this, store.getStoreId());break;
                case ACTIVITY_ORIGIN_STORE:
                    Intent i = new Intent(ItemActivity.this, StoreActivity.class);
                    startActivity(i);
            }
        });

        name.setText(item.getName());
        if (item.getSale() != null && item.getSale().getSaleEnd() > System.currentTimeMillis() / 1000){
            price.setText(buildSale());
        }else{
            price.setText(StringUtils.getFriendlyCurrencyString(this, store.getCurrency(), item.getPrice()));
        }

        storeName.setText(store.getName());
        if(item.getApparelExtension() != null){
            buildApparel(item.getApparelExtension());
        }

        if (item.getDescription() != null && item.getDescription().trim().length() > 0){
            description.setText(item.getDescription());
            description.setVisibility(View.VISIBLE);
            descriptionHead.setVisibility(View.VISIBLE);
        }
    }

    private void buildApparel(ApparelExtension ae){
        apparelDivider.setVisibility(View.VISIBLE);
        apparelGender.setText(ae.getGender().toString());
        apparelGender.setVisibility(View.VISIBLE);
        apparelGenderHead.setVisibility(View.VISIBLE);
        apparelAge.setText(ae.getAge().toString());
        apparelAge.setVisibility(View.VISIBLE);
        apparelAgeHead.setVisibility(View.VISIBLE);
        String sizeHead = "";
         switch(ae.getSizeSystem()) {
             case SML: sizeHead = getString(R.string.item_apparel_size, getString(R.string.item_apparel_sizing_regular));
             default: getString(R.string.item_apparel_size, ae.getSizeSystem());
         }
        apparelSize.setText(ae.getSize());
        apparelSize.setVisibility(View.VISIBLE);
        apparelSizeHead.setText(sizeHead);
        apparelSizeHead.setVisibility(View.VISIBLE);
        apparelColor.setText(ae.getColor());
        apparelColor.setVisibility(View.VISIBLE);
        apparelColorHead.setVisibility(View.VISIBLE);
        apparelMaterial.setText(ae.getMaterial());
        apparelMaterial.setVisibility(View.VISIBLE);
        apparelMaterialHead.setVisibility(View.VISIBLE);
        apparelFeature.setText(ae.getFeature());
        apparelFeature.setVisibility(View.VISIBLE);
        apparelFeatureHead.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
            ChangeBounds changeBoundsTransition = new ChangeBounds();
            getWindow().setSharedElementEnterTransition(changeBoundsTransition);
            getWindow().setSharedElementExitTransition(changeBoundsTransition);
            getWindow().setAllowReturnTransitionOverlap(false);
            getWindow().setAllowEnterTransitionOverlap(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (materialSheetFab!= null && materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            setAllElementsInvisible(); // need to do this to prevent ugly text mark during transition
            super.onBackPressed();
        }
    }

    private SpannableString buildSale(){
        Sale sale = item.getSale();
        String orgPrice = StringUtils.getFriendlyCurrencyString(this, store.getCurrency(), item.getPrice());
        String salePrice = StringUtils.getFriendlyCurrencyString(this, store.getCurrency(), sale.getSalePrice());
        CharSequence endDate = StringUtils.millisToDate(this, sale.getSaleEnd() * 1000);
        String res = getString(R.string.item_sale_composite, salePrice, orgPrice, endDate);
        SpannableString ss=  new SpannableString(res);
        int start = res.indexOf(salePrice) + salePrice.length();
        ss.setSpan(new RelativeSizeSpan(0.7f), start, res.length(), 0); // set size
        return ss ;
    }

    private void setAllElementsInvisible(){
        name.setVisibility(View.INVISIBLE);
        price.setVisibility(View.INVISIBLE);
        description.setVisibility(View.INVISIBLE);
        descriptionHead.setVisibility(View.INVISIBLE);
        if (item.getApparelExtension() != null){
            apparelDivider.setVisibility(View.INVISIBLE);
            apparelGender.setVisibility(View.INVISIBLE);
            apparelGenderHead.setVisibility(View.INVISIBLE);
            apparelAge.setVisibility(View.INVISIBLE);
            apparelAgeHead.setVisibility(View.INVISIBLE);
            apparelSize.setVisibility(View.INVISIBLE);
            apparelSizeHead.setVisibility(View.INVISIBLE);
            apparelColor.setVisibility(View.INVISIBLE);
            apparelColorHead.setVisibility(View.INVISIBLE);
            apparelMaterial.setVisibility(View.INVISIBLE);
            apparelMaterialHead.setVisibility(View.INVISIBLE);
            apparelFeature.setVisibility(View.INVISIBLE);
            apparelFeatureHead.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
