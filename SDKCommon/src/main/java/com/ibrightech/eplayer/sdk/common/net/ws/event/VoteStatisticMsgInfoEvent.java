package com.ibrightech.eplayer.sdk.common.net.ws.event;


import com.ibrightech.eplayer.sdk.common.entity.SessionData.VoteStatisticMsgInfo;

/**
 * Created by junhai on 14-8-13.
 */
public class VoteStatisticMsgInfoEvent {
    private VoteStatisticMsgInfo voteStatisticMsgInfo;

    public VoteStatisticMsgInfo getInfoData() {
        return voteStatisticMsgInfo;
    }

    public void setInfoData(VoteStatisticMsgInfo voteStatisticMsgInfo) {
        this.voteStatisticMsgInfo = voteStatisticMsgInfo;
    }

    public VoteStatisticMsgInfoEvent(VoteStatisticMsgInfo infoData) {
        this.voteStatisticMsgInfo = infoData;
    }
}
