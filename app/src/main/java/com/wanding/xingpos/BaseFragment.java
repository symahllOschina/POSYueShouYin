package com.wanding.xingpos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.x;

public class BaseFragment extends Fragment{

    protected final String TAG = getClass().getSimpleName();
    protected BaseActivity activity;
    protected App myApp;


    protected LayoutInflater inflater;
    protected View rootView;
    private boolean injected = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BaseActivity) getActivity();
        myApp = (App) activity.getApplication();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;
        injected = true;
        rootView = x.view().inject(this,inflater,container);
        return rootView;
    }
}
