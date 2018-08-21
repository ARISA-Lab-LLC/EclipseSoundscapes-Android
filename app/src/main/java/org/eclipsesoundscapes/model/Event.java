package org.eclipsesoundscapes.model;

public class Event {

    public String name = "";
    public String date = "";
    public String time = "";
    public String alt = "";
    public String azi = "";

    public Event(String name, String date, String time, String alt, String azi){
        this.name = name;
        this.date = date;
        this.time = time;
        this.alt = alt;
        this.azi = azi;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Event){
            Event event = (Event) obj;
            return this.name.equals(event.name);
        }

        return super.equals(obj);
    }
}