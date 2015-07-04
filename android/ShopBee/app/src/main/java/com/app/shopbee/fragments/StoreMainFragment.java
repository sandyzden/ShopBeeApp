package com.app.shopbee.fragments;

/**
 * Created by sendilkumar on 22/06/15.
 */
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.shopbee.R;


public class StoreMainFragment extends Fragment {


    CardView mCardView;

    public static StoreMainFragment newInstance() {
        StoreMainFragment fragment = new StoreMainFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public StoreMainFragment() {
        // singleton
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.store_main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCardView = (CardView) view.findViewById(R.id.card_view);
    }
}
