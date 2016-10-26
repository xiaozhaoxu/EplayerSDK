package com.ibrightech.eplayer.sdk.common.entity.SessionData;

import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerVoteType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by junhai on 14-8-13.
 */
public class VoteStatisticMsgInfo {


    public EplayerVoteType voteType;
    public String voteKey;
    public String sumInfo;
    public List<Integer> percentList=new ArrayList<Integer>();



    public static VoteStatisticMsgInfo fromJson(JSONObject jsonObject) {
        VoteStatisticMsgInfo msg = new VoteStatisticMsgInfo();
        msg.voteKey=jsonObject.optString("voteKey");
        int type=jsonObject.optInt("voteType");
        msg.voteType=EplayerVoteType.getEnumBykey(type);

        JSONObject js= jsonObject.optJSONObject("votestatistic");
        String sumInfo=js.optString("sumInfo");
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(sumInfo);
        msg.sumInfo=m.replaceAll("").trim();

        JSONArray jsonArray= js.optJSONArray("percentage");
        List<Integer> percentList=new ArrayList<Integer>();
        try {
            for(int i=0;i<jsonArray.length();i++){
                percentList.add((Integer) jsonArray.get(i));
            }
            msg.percentList=percentList;
        }catch (Exception e){
            e.printStackTrace();
        }

        return msg;
    }


}
