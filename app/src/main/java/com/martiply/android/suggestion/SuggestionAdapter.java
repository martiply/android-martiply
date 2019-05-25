package com.martiply.android.suggestion;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.martiply.android.R;
import com.martiply.android.activities.BusProvider;
import com.martiply.android.sqlite.QueryHistory;

import java.util.ArrayList;
import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<QueryHistory> data = new ArrayList<>();

    public SuggestionAdapter() {
        setHasStableIds(true);
    }

    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
        return new SuggestionViewHolder(inflater.inflate(R.layout.row_item_suggestion, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final String text = data.get(position).getQuery();
        SuggestionViewHolder h = (SuggestionViewHolder) holder;
        h.text.setText(text);
        h.text.setOnClickListener(view -> BusProvider.getInstance().post(new TextClickEvent(text)));
        h.icon.setOnClickListener(view -> BusProvider.getInstance().post(new IconClickEvent(text)));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void swap(List<QueryHistory> newQueryHistorys) {
        this.data.clear();
        this.data.addAll(newQueryHistorys);
        notifyDataSetChanged();
    }

    public void clear() {
        this.data.clear();
        notifyDataSetChanged();
    }


     static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        private Unbinder unbinder;
        @BindView(R.id.text) TextView text;
        @BindView(R.id.icon) FrameLayout icon;

        SuggestionViewHolder(final View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }

    public static class TextClickEvent{
        public String text;

        TextClickEvent(String text) {
            this.text = text;
        }
    }
    public static class IconClickEvent{
        public String text;

        IconClickEvent(String text) {
            this.text = text;
        }
    }


}

