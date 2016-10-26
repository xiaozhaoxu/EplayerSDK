package com.ibrightech.eplayer.sdk.event;


public class DrawPadPageChangeEvent {
    private  int width;
    private  int height;

    public DrawPadPageChangeEvent(int width,int height){
        this.width=width;
        this.height=height;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
