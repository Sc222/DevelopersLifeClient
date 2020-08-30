package ru.sc222.devslife.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

import ru.sc222.devslife.utils.SimpleEntry;
import ru.sc222.devslife.utils.SimpleLinkedList;
import ru.sc222.devslife.utils.SimpleNode;

public class RandomFragmentViewModel extends ViewModel {

    private MutableLiveData<Boolean> isCurrentEntryImageLoaded=new MutableLiveData<>(false);
    private MutableLiveData<Boolean> canLoadPrevious=new MutableLiveData<>(false);
    private MutableLiveData<Boolean> canLoadNext = new MutableLiveData<>(false);
    private MutableLiveData<SimpleEntry> currentEntry=new MutableLiveData<>(null);
    private MutableLiveData<ErrorInfo> error = new MutableLiveData<>(new ErrorInfo(ErrorInfo.Error.NO_ERRORS));

    //todo control linked list max size
    private SimpleLinkedList<SimpleEntry> entries= new SimpleLinkedList<>();
    private SimpleNode<SimpleEntry> currentEntryListNode=null;


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

    public void setCanLoadPrevious(boolean canLoadPrevious) {
        this.canLoadPrevious.setValue(canLoadPrevious);
    }

    public void updateCanLoadPrevious() {
        this.canLoadPrevious.setValue(currentEntryListNode.getPrevious()!=null);
    }

    public MutableLiveData<Boolean> getCanLoadNext() {
        return canLoadNext;
    }

    public void setCanLoadNext(boolean canLoadNext) {
        this.canLoadNext.setValue(canLoadNext);
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

    public boolean switchToNextEntry() {
        if (currentEntryListNode != null && currentEntryListNode.getNext() != null) {
            currentEntryListNode = currentEntryListNode.getNext();
            updateCanLoadPrevious();
            currentEntry.setValue(currentEntryListNode.getItem());
            return true;
        }
        return false;
    }

    public boolean switchToPreviousEntry() {
        if (currentEntryListNode != null && currentEntryListNode.getPrevious() != null) {
            currentEntryListNode = currentEntryListNode.getPrevious();
            updateCanLoadPrevious();
            currentEntry.setValue(currentEntryListNode.getItem());
            return true;
        }
        return false;
    }

    public void updateEntryColors(int defaultBgColor, int defaultTitleColor, int defaultSubtitleColor) {
        SimpleEntry entry = getCurrentEntry().getValue();
        assert entry != null;
        entry.setColors(defaultBgColor,defaultTitleColor,defaultSubtitleColor);
        currentEntry.setValue(entry);
    }

    public void fixError(ErrorInfo.Error errorToBeFixed) {
        if(Objects.requireNonNull(error.getValue()).getError()==errorToBeFixed)
            error.setValue(new ErrorInfo(ErrorInfo.Error.NO_ERRORS));
    }
}
