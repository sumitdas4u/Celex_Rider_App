package com.celex.rider.CodeClasses;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.celex.rider.interfaces.OnBackPressListener;

public class BackPressImplimentation implements OnBackPressListener {

    private Fragment parentFragment;

    public BackPressImplimentation(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    public boolean onBackPressed() {

        if (parentFragment == null) return false;

        int childCount = parentFragment.getChildFragmentManager().getBackStackEntryCount();

        if (childCount == 0) {
            return false;
        } else {
            // get the child Fragment
            FragmentManager childFragmentManager = parentFragment.getChildFragmentManager();
            OnBackPressListener childFragment = (OnBackPressListener) childFragmentManager.getFragments().get(0);

             if (!childFragment.onBackPressed()) {
                 childFragmentManager.popBackStackImmediate();
             }

            return true;

        }
    }

}