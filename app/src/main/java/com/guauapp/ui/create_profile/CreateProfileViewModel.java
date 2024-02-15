package com.guauapp.ui.create_profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CreateProfileViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}