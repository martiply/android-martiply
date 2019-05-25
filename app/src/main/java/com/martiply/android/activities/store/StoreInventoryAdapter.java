package com.martiply.android.activities.store;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.martiply.android.R;
import com.martiply.android.activities.BusProvider;
import com.martiply.android.util.StringUtils;
import com.martiply.model.Img;
import com.martiply.model.Item;
import com.martiply.model.Store;
import com.martiply.model.interfaces.AbsImg;

import java.util.ArrayList;

class StoreInventoryAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int VIEW_TYPE_RESULT = 52;
    private static final int VIEW_TYPE_EMPTY = 67;
    private static final int VIEW_TYPE_FOOTER = 78;

    private Context context;
    private ArrayList<Data> items = new ArrayList<>();
    private Store store;

    StoreInventoryAdapter(Context context, Store store) {
        this.context = context;
        this.store = store;
        items.add(new Data(context.getString((R.string.guide_inventory))));
    }

    public void clear(){
        items.clear();
        notifyDataSetChanged();
    }

    void addItem(Item item){
        items.add(new Data(item));
        notifyItemInserted(items.size() - 1);
    }


    void addFooter(){
        items.add(new Data(VIEW_TYPE_FOOTER));
        notifyItemInserted(items.size() - 1);
    }

    void addMessage(String message){
        items.add(new Data(message));
        notifyItemInserted(items.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEW_TYPE_RESULT:
                View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_store_inventory_result, parent, false);
                return new ViewHolderResult(convertView);
            case VIEW_TYPE_EMPTY:
                 convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_store_inventory_empty, parent, false);
                return new ViewHolderEmpty(convertView);
            case VIEW_TYPE_FOOTER:
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_copyright, parent, false);
                return new ViewHolderFooter(convertView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ViewHolderResult){
            final ViewHolderResult h = (ViewHolderResult)holder;
            final Item item = items.get(position).item;

            h.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusProvider.getInstance().post(new StoreActivity.InventoryRowClickedEvent(h.img, item));
                }
            });
            Img.loadImg(item.getImg(), 0, context, h.img, R.drawable.wp_crumpled_paper, R.drawable.wp_crumpled_paper, AbsImg.Size.m);
            String s = item.getName();
            String price = item.getPrice();

            h.text.setText(s);
            h.price.setText(StringUtils.getFriendlyCurrencyString(context, store.getCurrency(), price));


        }else if(holder instanceof ViewHolderEmpty){
            final ViewHolderEmpty h = (ViewHolderEmpty)holder;
            Data data = items.get(position);
            h.message.setText(data.emptyMessage);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private static class Data {
        int viewType;
        Item item;
        String emptyMessage;

        Data(int viewType) {
            this.viewType = viewType;
        }

        Data(Item item){
            this(VIEW_TYPE_RESULT);
            this.item = item;
        }

        Data(String emptyMessage){
            this(VIEW_TYPE_EMPTY);
            this.emptyMessage = emptyMessage;
        }
    }

     static class ViewHolderResult extends RecyclerView.ViewHolder {
        @BindView(R.id.row) LinearLayout row;
        @BindView(R.id.img) ImageView img;
        @BindView(R.id.text) TextView text;
        @BindView(R.id.text2) TextView price;

        ViewHolderResult(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


     static class ViewHolderEmpty extends RecyclerView.ViewHolder{
        @BindView(android.R.id.empty) TextView message;

        ViewHolderEmpty(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class ViewHolderFooter extends  RecyclerView.ViewHolder{
        ViewHolderFooter(View itemView) {
            super(itemView);
        }
    }
}
