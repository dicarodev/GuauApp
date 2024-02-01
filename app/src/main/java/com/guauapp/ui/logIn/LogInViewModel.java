package com.guauapp.ui.logIn;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LogInViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LogInViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}
