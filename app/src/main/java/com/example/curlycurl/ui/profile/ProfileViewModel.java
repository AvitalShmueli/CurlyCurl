package com.example.curlycurl.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mText;




    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("My hair products");

    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getUsername() {
        return mText;
    }
}