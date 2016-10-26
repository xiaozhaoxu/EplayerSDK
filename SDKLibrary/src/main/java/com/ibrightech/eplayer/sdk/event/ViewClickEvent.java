package com.ibrightech.eplayer.sdk.event;

import com.ibrightech.eplayer.sdk.event.Enum.ViewState;

/**
 * Created by zhaoxu2014 on 16/9/13.
 */
public class ViewClickEvent {
    public ViewState viewState;

    public ViewClickEvent(ViewState viewState) {
        this.viewState = viewState;
    }
}
