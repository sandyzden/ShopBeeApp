package com.app.shopbee.adapter;

/**
 * Created by sendilkumar on 28/06/15.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.app.shopbee.model.Store;
import com.app.shopbee.R;

import java.util.List;

public class StoreCardAdapter extends RecyclerView.Adapter<StoreCardAdapter.StoreViewHolder> {

    private List<Store> storeList;

    public StoreCardAdapter(List<Store> storeList) {
        this.storeList = storeList;
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    @Override
    public void onBindViewHolder(StoreViewHolder storeViewHolder, int i) {
        Store ci = storeList.get(i);
        storeViewHolder.vstoreName.setText(ci.getStoreName());
        storeViewHolder.vlocality.setText(ci.getLocality());
        storeViewHolder.vcontactNo.setText(ci.getContactNo());


    }

    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.store_main_fragment, viewGroup, false);

        return new StoreViewHolder(itemView);
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {

        protected TextView vstoreName;
        protected TextView vlocality;
        protected TextView vcontactNo;

        public StoreViewHolder(View v) {
            super(v);
            vstoreName =  (TextView) v.findViewById(R.id.storeName);
            vlocality = (TextView)  v.findViewById(R.id.locality);
            vcontactNo = (TextView)  v.findViewById(R.id.contactNo);

        }

    }
}
