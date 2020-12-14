package org.eclipsesoundscapes.model;

import android.graphics.drawable.Drawable;

public class Partner {

    private String title;

    private String link;

    private String description;

    private Drawable logo;

    public Partner(final String title, final String description, final String link, final Drawable logo) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public Drawable getLogo() {
        return logo;
    }
}
