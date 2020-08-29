package ru.sc222.devslife.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.sc222.devslife.core.SimpleEntry;
import ru.sc222.devslife.core.SimpleLinkedList;
import ru.sc222.devslife.core.SimpleNode;

public class RandomFragmentViewModel extends ViewModel {

    private MutableLiveData<Boolean> isCurrentEntryImageLoaded;
    private MutableLiveData<Boolean> canLoadPrevious;
    private MutableLiveData<SimpleEntry> currentEntry;

    //todo control linked list max size
    private SimpleLinkedList<SimpleEntry> entries;
    private SimpleNode<SimpleEntry> currentEntryListNode;


    public RandomFragmentViewModel() {
        isCurrentEntryImageLoaded = new MutableLiveData<>();
        isCurrentEntryImageLoaded.setValue(false);

        canLoadPrevious = new MutableLiveData<>();
        canLoadPrevious.setValue(false);

        currentEntry = new MutableLiveData<>();
        currentEntry.setValue(null);

        entries = new SimpleLinkedList<>();
        currentEntryListNode = null;
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

    public LiveData<SimpleEntry> getCurrentEntry() {
        return currentEntry;
    }

    public void addEntry(SimpleEntry entry) {
        currentEntry.setValue(entry);
        entries.addLast(entry);
        currentEntryListNode = entries.getLast();
        setCanLoadPrevious(currentEntryListNode.getPrevious()!=null);
    }

    public boolean switchToNextEntry() {
        if (currentEntryListNode != null && currentEntryListNode.getNext() != null) {
            currentEntryListNode = currentEntryListNode.getNext();
            currentEntry.setValue(currentEntryListNode.getItem());
            return true;
        }
        return false;
    }

    public boolean switchToPreviousEntry() {
        if (currentEntryListNode != null && currentEntryListNode.getPrevious() != null) {
            currentEntryListNode = currentEntryListNode.getPrevious();
            setCanLoadPrevious(currentEntryListNode.getPrevious()!=null);
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

}
