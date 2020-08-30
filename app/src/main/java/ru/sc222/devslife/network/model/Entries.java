package ru.sc222.devslife.network.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Entries {

    public static final int PREFERRED_PAGE_SIZE = 6;
    public static final String PREFERRED_TYPES = "gif";

    @SerializedName("result")
    @Expose
    private List<Entries> entries = null;
    @SerializedName("totalCount")
    @Expose
    private Integer totalCount;

    public List<Entries> getEntries() {
        return entries;
    }

    public void setEntries(List<Entries> entries) {
        this.entries = entries;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

}
