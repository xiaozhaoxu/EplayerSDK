package com.ibrightech.eplayer.sdk.common.net.ws.event;


import com.ibrightech.eplayer.sdk.common.entity.SessionData.SocketMessage;

import java.util.List;

/**
 * Created by junhai on 14-8-13.
 */
public class QAEvent {
    private List<SocketMessage> datas;

    public List<SocketMessage> getDatas() {
        return datas;
    }

    public void setDatas(List<SocketMessage> datas) {
        this.datas = datas;
    }
}
