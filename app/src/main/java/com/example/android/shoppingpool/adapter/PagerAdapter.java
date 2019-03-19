package com.example.android.shoppingpool.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentManager;

import com.example.android.shoppingpool.fragments.AllProductsFragment;
import com.example.android.shoppingpool.fragments.RecommenderFragment;
import com.example.android.shoppingpool.fragments.SpecialOfferFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AllProductsFragment tab1 = new AllProductsFragment();
                return tab1;
            case 1:
                SpecialOfferFragment tab2 = new SpecialOfferFragment();
                return tab2;
            case 2:
                RecommenderFragment tab3 = new RecommenderFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
