package com.celex.rider.Adapters;


import android.content.res.Resources;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.celex.rider.AllFragments.HomeFragments.Delivered_Order;
import com.celex.rider.AllFragments.HomeFragments.Home_F;
import com.celex.rider.AllFragments.HomeFragments.Processing_Order;


public class Pager_Adapter extends FragmentPagerAdapter {

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public Pager_Adapter(final Resources resources, FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        final Fragment result;
        switch (position) {
            case 0:
                result = new Home_F();
                break;

            case 1:
                result = new Processing_Order();
                break;

            case 2:
                result=new Delivered_Order();
                break;

            default:
                result = null;
                break;
        }

        return result;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    public Fragment getRegisteredFragment(int position) {

        return registeredFragments.get(position);
    }
}
