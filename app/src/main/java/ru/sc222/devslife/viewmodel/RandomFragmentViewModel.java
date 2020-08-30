package ru.sc222.devslife.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

import ru.sc222.devslife.custom.ErrorInfo;
import ru.sc222.devslife.custom.LoadError;
import ru.sc222.devslife.custom.SimpleEntry;
import ru.sc222.devslife.custom.SimpleLinkedList;
import ru.sc222.devslife.custom.SimpleNode;
import ru.sc222.devslife.custom.UiColorSet;

public class RandomFragmentViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> isCurrentEntryImageLoaded = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> canLoadPrevious = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> canLoadNext = new MutableLiveData<>(false);
    private MutableLiveData<SimpleEntry> currentEntry = new MutableLiveData<>(null);
    private MutableLiveData<ErrorInfo> error = new MutableLiveData<>(new ErrorInfo(LoadError.NO_ERRORS));

    private final UiColorSet defaultColorSet;
    
    //todo control linked list max size
    private SimpleLinkedList<SimpleEntry> entries = new SimpleLinkedList<>();
    private SimpleNode<SimpleEntry> currentEntryListNode = null;

    public RandomFragmentViewModel(@NonNull Application application) {
        super(application);
        defaultColorSet = new UiColorSet(application.getApplicationContext());
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

    public MutableLiveData<Boolean> getCanLoadPrevious() {
        return canLoadPrevious;
    }

    public void updateCanLoadPrevious() {
        boolean hasNetworkErrors = Objects.requireNonNull(this.error.getValue()).hasNetworkErrors();
        this.canLoadPrevious.setValue(!hasNetworkErrors && currentEntryListNode != null && currentEntryListNode.getPrevious() != null);
    }

    public MutableLiveData<Boolean> getCanLoadNext() {
        return canLoadNext;
    }

    public void setCanLoadNext(boolean canLoadNext) {
        this.canLoadNext.setValue(canLoadNext);
    }

    public void setColorSet(UiColorSet colorSet) {
        SimpleEntry entry = this.currentEntry.getValue();
        assert entry != null;
        entry.setColorSet(colorSet);
        this.currentEntry.setValue(entry);
    }

    public LiveData<SimpleEntry> getCurrentEntry() {
        return currentEntry;
    }

    public void addEntry(SimpleEntry entry) {
        currentEntry.setValue(entry);
        entries.addLast(entry);
        currentEntryListNode = entries.getLast();
        updateCanLoadPrevious();
    }

    public boolean switchToNextCachedEntry() {
        if (currentEntryListNode != null && currentEntryListNode.getNext() != null) {
            currentEntryListNode = currentEntryListNode.getNext();
            updateCanLoadPrevious();
            currentEntry.setValue(currentEntryListNode.getItem());
            return true;
        }
        return false;
    }

    public boolean switchToPreviousCachedEntry() {
        if (currentEntryListNode != null && currentEntryListNode.getPrevious() != null) {
            currentEntryListNode = currentEntryListNode.getPrevious();
            updateCanLoadPrevious();
            currentEntry.setValue(currentEntryListNode.getItem());
            return true;
        }
        return false;
    }


    public void fixError(LoadError errorToBeFixed) {
        if (Objects.requireNonNull(error.getValue()).getError() == errorToBeFixed)
            error.setValue(new ErrorInfo(LoadError.NO_ERRORS));
    }

    public UiColorSet getDefaultColorSet() {
        return defaultColorSet;
    }
}
