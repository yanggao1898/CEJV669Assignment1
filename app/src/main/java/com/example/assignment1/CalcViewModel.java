package com.example.assignment1;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class CalcViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<StringBuilder> CurrentNumber = new MutableLiveData<>();
    private MutableLiveData<StringBuilder> OpPreview = new MutableLiveData<>();
    private MutableLiveData<StringBuilder> History = new MutableLiveData<>();

    public MutableLiveData<StringBuilder> getCurrentNumber() {
        return CurrentNumber;
    }

    public void updateCN() {
        CurrentNumber.setValue(CurrentNumber.getValue());
    }



    public void setCurrentNumber(String cn) {
        CurrentNumber.setValue(cn);
    }

    public MutableLiveData<String> getOpPreview() {
        return OpPreview;
    }

    public

    public MutableLiveData<StringBuilder> getHistory() {
        return History;
    }
}
