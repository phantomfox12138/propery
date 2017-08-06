package com.junjingit.propery.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.junjingit.propery.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment
{
    
    private View mRootView;
    
    private NavigationView mNaviView;
    
    private Dialog dialog;
    
    private ImageView imageView;
    
    private View mModifyProfile;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        if (mRootView == null)
        {
            mRootView = inflater.inflate(R.layout.fragment_me, null);
            initView();
        }
        
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null)
        {
            parent.removeView(mRootView);
        }
        
        final Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setDisplayShowTitleEnabled(false);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) mRootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("");
        return mRootView;
    }
    
    public void initView()
    {
        mModifyProfile = mRootView.findViewById(R.id.profile_modify_profile);


    }
    
    public void onAddPic(View v)
    {
        imageView = (ImageView) v;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.add_pic_dialog,
                null);
        dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setCancelable(true);
        dialog.show();
        dialog.getWindow().setContentView(layout);
    }
    
}
