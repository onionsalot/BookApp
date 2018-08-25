package com.example.superonion.bookapp;

public class Books {
    public final String mTitle;
    public final String mSubtitle;
    public final String mDescription;
    public final String mPublishedDate;
    public final String mAuthors;
    public final String mPicture;


    public Books(String title, String subtitle, String description, String publishedDate, String authors,String picture) {
        mTitle = title;
        mSubtitle = subtitle;
        mAuthors = authors;
        mDescription = description;
        mPublishedDate = publishedDate;
        mPicture = picture;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getPublishedDate() {
        return mPublishedDate;
    }

    public String getPicture() {
        return mPicture;
    }
}
