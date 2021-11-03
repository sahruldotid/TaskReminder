package com.syahrul.taskreminder;

public class Category {
    private String name;
    private int color;
    private boolean show;

    public Category(String name, int color){
        this.name = name;
        this.color = color;
        show = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean getShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }


}
