package com.ibrightech.eplayer.sdk.common.net.ws.event;


import com.ibrightech.eplayer.sdk.common.entity.SessionData.VoteMsgInfo;

/**
 * Created by junhai on 14-8-13.
 */
public class VoteMsgInfoEvent {
    private VoteMsgInfo voteMsgInfo;

    public VoteMsgInfo getInfoData() {
        return voteMsgInfo;
    }

    public void setInfoData(VoteMsgInfo infoData) {
        this.voteMsgInfo = infoData;
    }

    public VoteMsgInfoEvent(VoteMsgInfo infoData) {
        this.voteMsgInfo = infoData;
    }
}
