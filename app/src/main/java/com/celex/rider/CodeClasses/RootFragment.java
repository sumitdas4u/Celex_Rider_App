package com.celex.rider.CodeClasses;


import androidx.fragment.app.Fragment;

import com.celex.rider.interfaces.OnBackPressListener;

public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {
        return new BackPressImplimentation(this).onBackPressed();
    }
}