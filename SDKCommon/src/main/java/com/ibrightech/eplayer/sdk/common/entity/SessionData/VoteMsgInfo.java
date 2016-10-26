package com.ibrightech.eplayer.sdk.common.entity.SessionData;

import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerVoteType;

import org.json.JSONObject;

/**
 * Created by junhai on 14-8-13.
 */
public class VoteMsgInfo {


    public EplayerVoteType voteType;
    public String voteKey;
    public boolean action; //boolean true:发起问答   false:取消问答




    public static VoteMsgInfo fromJson(JSONObject jsonObject) {
        VoteMsgInfo msg = new VoteMsgInfo();
        msg.voteKey=jsonObject.optString("voteKey");
        msg.action=jsonObject.optBoolean("action");
        int type=jsonObject.optInt("voteType");

        msg.voteType=EplayerVoteType.getEnumBykey(type);

        return msg;
    }


}
