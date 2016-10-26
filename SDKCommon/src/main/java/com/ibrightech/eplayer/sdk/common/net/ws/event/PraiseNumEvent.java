package com.ibrightech.eplayer.sdk.common.net.ws.event;


import com.ibrightech.eplayer.sdk.common.entity.Prainse;

/**
 * Created by zhaoxu2014 on 16/5/28.
 */
public class PraiseNumEvent {
    private Prainse prainse;

    public PraiseNumEvent(Prainse prainse) {
        this.prainse = prainse;
    }

    public Prainse getPrainse() {
        return prainse;
    }

    public void setPrainse(Prainse prainse) {
        this.prainse = prainse;
    }
}
