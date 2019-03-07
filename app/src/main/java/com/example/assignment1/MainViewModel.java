package com.example.assignment1;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<StringBuilder> currentNum;

    public MutableLiveData<StringBuilder> getCurrentNum() {
        if (currentNum == null) {
            currentNum = new MutableLiveData<>();
        }

        return currentNum;
    }


}
