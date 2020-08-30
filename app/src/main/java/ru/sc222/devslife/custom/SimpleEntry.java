package ru.sc222.devslife.custom;

public class SimpleEntry {
    private final Integer id;
    private final String description;
    private final Integer votes;
    private final String author;
    private final String gifURL;
    private UiColorSet colorSet;

    public SimpleEntry(Integer id, String description, Integer votes, String author,String gifURL) {
        this.id=id;
        this.description=description;
        this.votes=votes;
        this.author=author;
        this.gifURL=gifURL;
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Integer getVotes() {
        return votes;
    }

    public String getAuthor() {
        return author;
    }

    public String getGifURL() {
        return gifURL;
    }

    public UiColorSet getColorSet() {
        return colorSet;
    }

    public void setColorSet(UiColorSet colorSet) {
        this.colorSet = colorSet;
    }
}
