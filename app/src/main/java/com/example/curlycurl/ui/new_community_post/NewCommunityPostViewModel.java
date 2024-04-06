package com.example.curlycurl.ui.new_community_post;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewCommunityPostViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NewCommunityPostViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}