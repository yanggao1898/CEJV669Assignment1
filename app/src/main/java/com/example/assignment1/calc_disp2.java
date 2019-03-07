package com.example.assignment1;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class calc_disp2 extends Fragment {

    private MainViewModel mViewModel;

    private TextView curNumber;
    private TextView opPreview;

    public static calc_disp2 newInstance() {
        return new calc_disp2();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calc_disp2_fragment, container, false)
        curNumber = v.findViewById(R.id.tv_curNumber_2);
        opPreview = v.findViewById(R.id.tv_opPreview_2);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        final Observer<StringBuilder> curNum = new Observer<StringBuilder>() {
            @Override
            public void onChanged(@Nullable StringBuilder sb) {
                curNumber.setText(sb.toString());
            }
        };
    }


}
