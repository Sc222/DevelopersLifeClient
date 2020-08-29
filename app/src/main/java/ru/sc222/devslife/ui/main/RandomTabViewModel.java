package ru.sc222.devslife.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RandomTabViewModel extends ViewModel {

    private MutableLiveData<String> entryAuthor;
    private MutableLiveData<String> entryDescription;

    public RandomTabViewModel()
    {
        entryAuthor = new MutableLiveData<>();
        entryAuthor.setValue("Author");

        entryDescription = new MutableLiveData<>();
        entryDescription.setValue("Description");
    }

    public LiveData<String> getEntryAuthor() {
        return entryAuthor;
    }
    public LiveData<String> getEntryDescription() {
        return entryDescription;
    }

    public void setEntryAuthor(String entryAuthor) {
        this.entryAuthor.setValue(entryAuthor);
    }

    public void setEntryDescription(String entryDescription) {
        this.entryDescription.setValue(entryDescription);
    }
}
