package ru.sc222.devslife.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewsfeedFragmentViewModel extends ViewModel {

    private MutableLiveData<String> type = new MutableLiveData<>(null);

    public MutableLiveData<String> getType() {
        return type;
    }

    public void setType(String type) {
        this.type.setValue(type);
    }
}