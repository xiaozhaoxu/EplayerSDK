package com.ibrightech.eplayer.sdk.event;

import com.ibrightech.eplayer.sdk.event.Enum.ScrollState;

//scrollview 滚动的广播
public class ScrollChangeEvent {
    private ScrollState state;

    public ScrollChangeEvent(ScrollState state) {
        this.setState(state);
    }


    public ScrollState getState() {
        return state;
    }

    public void setState(ScrollState state) {
        this.state = state;
    }
}
