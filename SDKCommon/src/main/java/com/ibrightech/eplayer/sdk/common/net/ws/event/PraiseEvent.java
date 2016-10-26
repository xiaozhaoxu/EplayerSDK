package com.ibrightech.eplayer.sdk.common.net.ws.event;


import com.ibrightech.eplayer.sdk.common.entity.Prainse;

/**
 * Created by junhai on 14-8-13.
 */
public class PraiseEvent {
    private Prainse prainse;

    public PraiseEvent(Prainse prainse) {
        this.prainse = prainse;
    }

    public Prainse getPrainse() {
        return prainse;
    }

    public void setPrainse(Prainse prainse) {
        this.prainse = prainse;
    }
}
