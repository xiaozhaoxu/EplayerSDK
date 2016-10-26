package com.ibrightech.eplayer.sdk.event;

import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPadInfo;

public class DrawPadPositionChangeEvent {
    private DrawPadInfo padInfo;

    public DrawPadInfo getPadInfo() {
        return padInfo;
    }

    public DrawPadPositionChangeEvent(DrawPadInfo padInfo) {
        this.padInfo = padInfo;
    }

}
