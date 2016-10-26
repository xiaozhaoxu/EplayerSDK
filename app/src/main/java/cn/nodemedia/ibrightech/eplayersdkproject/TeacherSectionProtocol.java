package cn.nodemedia.ibrightech.eplayersdkproject;

import android.util.Base64;

import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.lzy.okhttputils.model.HttpHeaders;
import com.ibrightech.eplayer.sdk.teacher.Entity.LoginInfEntity;
import com.ibrightech.eplayer.sdk.teacher.Entity.TeacherSectionEntity;
import com.ibrightech.eplayer.sdk.teacher.net.HttpJsonBaseProtocol;
import com.ibrightech.eplayer.sdk.teacher.net.UrlConstant;

import org.json.JSONObject;

/**
 * Created by zhaoxu2014 on 16/8/27.
 */
public class TeacherSectionProtocol  extends HttpJsonBaseProtocol {
    long section_id;
    LoginInfEntity loginInfEntity;
    public TeacherSectionProtocol(long section_id,LoginInfEntity loginInfEntity) {
        this.section_id = section_id;
        this.loginInfEntity=loginInfEntity;

    }
    @Override
    protected HttpHeaders getHeaderMap() {
        HttpHeaders map=new HttpHeaders();
        map.put("platform", "android");
        map.put("apiversion", "1");
        try {
            if (!CheckUtil.isEmpty(loginInfEntity)) {

                String token = loginInfEntity.getUser_id() + "=" + loginInfEntity.getToken();
                String strBase64 = new String(Base64.encode(token.getBytes(), Base64.NO_WRAP));
                //  LogUtil.d(token + "AsyncBaseProtocol --->" + "protocol strBase64", strBase64);
                map.put("Authorization", strBase64);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    @Override
    protected String getUrl() {
        return UrlConstant.getUrlInBusinessHost(UrlConstant.TEACHER_SECTION);
    }

    @Override
    protected Object getParams() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("section_id", section_id);
        return jo;
    }



    @Override
    protected boolean isGetMode() {
        return false;
    }


    @Override
    protected Object handleJSON(JSONObject js) throws Exception {
        if(CheckUtil.isEmpty(js)){
            return null;
        }

        JSONObject jsonObject1 = js.optJSONObject("data");
        if (null == jsonObject1) {
            return null;
        }

        return TeacherSectionEntity.fromJSON(jsonObject1);

    }


}
