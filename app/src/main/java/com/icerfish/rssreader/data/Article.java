package com.icerfish.rssreader.data;

public class Article {

    private String title;
    private String description;
    private String link;
    private String pubDate;
    private String thumbnailUrlSmall;
    private String thumbnailUrlLarge;


    public Article(String title, String description, String link, String pubDate, String thumbnailUrlSmall, String thumbnailUrlLarge) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
        this.thumbnailUrlSmall = thumbnailUrlSmall;
        this.thumbnailUrlLarge = thumbnailUrlLarge;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getPubDate(){
        return pubDate;
    }

    public void setPubDate(String pubDate){
        this.pubDate = pubDate;
    }

    public String getLink(){
        return link;
    }

    public void setLink(String link){
        this.link = link;
    }

    public String getThumbnailUrlSmall(){
        return thumbnailUrlSmall;
    }

    public void setThumbnailUrlSmall(String thumbnailUrlSmall){
        this.thumbnailUrlSmall = thumbnailUrlSmall;
    }

    public String getThumbnailUrlLarge(){
        return thumbnailUrlLarge;
    }

    public void setThumbnailUrlLarge(String thumbnailUrlLarge){
        this.thumbnailUrlLarge = thumbnailUrlLarge;
    }
}
