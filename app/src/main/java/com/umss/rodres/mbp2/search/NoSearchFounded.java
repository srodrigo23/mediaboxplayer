package com.umss.rodres.mbp2.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umss.rodres.mbp2.R;


public class NoSearchFounded extends Fragment {

    public NoSearchFounded() { }

    public static NoSearchFounded newInstance() {
        NoSearchFounded fragment = new NoSearchFounded();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_search_founded, container, false);
    }
}
