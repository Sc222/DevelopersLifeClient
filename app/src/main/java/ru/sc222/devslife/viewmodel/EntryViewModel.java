package ru.sc222.devslife.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

import ru.sc222.devslife.custom.ErrorInfo;
import ru.sc222.devslife.custom.LoadError;


public class EntryViewModel extends ViewModel {

    private MutableLiveData<Boolean> isCurrentEntryImageLoaded = new MutableLiveData<>(false);
    private MutableLiveData<ErrorInfo> error = new MutableLiveData<>(new ErrorInfo(LoadError.NO_ERRORS));

    public EntryViewModel() {
    }

    public MutableLiveData<ErrorInfo> getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error.setValue(error);
    }

    public MutableLiveData<Boolean> getIsCurrentEntryImageLoaded() {
        return isCurrentEntryImageLoaded;
    }

    public void setIsCurrentEntryImageLoaded(boolean isCurrentEntryImageLoaded) {
        this.isCurrentEntryImageLoaded.setValue(isCurrentEntryImageLoaded);
    }

    public void fixError(LoadError errorToBeFixed) {
        if (Objects.requireNonNull(error.getValue()).getError() == errorToBeFixed)
            error.setValue(new ErrorInfo(LoadError.NO_ERRORS));
    }
}
