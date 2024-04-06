package com.example.curlycurl.ui.new_product_post;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewProductPostViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NewProductPostViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}