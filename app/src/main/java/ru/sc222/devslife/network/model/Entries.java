package ru.sc222.devslife.network.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Entries {

    public static final int PREFERRED_PAGE_SIZE = 20;
    public static final String PREFERRED_TYPES = "gif";

    @SerializedName("result")
    @Expose
    private List<Entry> entries = null;
    @SerializedName("totalCount")
    @Expose
    private Integer totalCount;

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

}
