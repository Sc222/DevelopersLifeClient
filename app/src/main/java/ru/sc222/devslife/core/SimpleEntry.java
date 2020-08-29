package ru.sc222.devslife.core;

public class SimpleEntry {
    private final Integer id;
    private final String description;
    private final Integer votes;
    private final String author;
    private final String gifURL;
    private boolean colorsSet = false;
    private int bgColor;
    private int titleColor;
    private int subtitleColor;

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

    public boolean areColorsSet() {
        return colorsSet;
    }

    public int getBgColor() {
        return bgColor;
    }
    public int getTitleColor() {
        return titleColor;
    }
    public int getSubtitleColor() {
        return subtitleColor;
    }

    public void setColors(int bgColor, int titleColor, int subtitleColor) {
        colorsSet=true;
        this.bgColor=bgColor;
        this.titleColor=titleColor;
        this.subtitleColor=subtitleColor;
    }
}
