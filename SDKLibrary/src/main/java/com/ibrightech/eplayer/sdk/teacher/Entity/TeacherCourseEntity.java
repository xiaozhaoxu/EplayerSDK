package com.ibrightech.eplayer.sdk.teacher.Entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.ibrightech.eplayer.sdk.common.util.JsonUtil;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class TeacherCourseEntity extends SDKBaseEntity implements Parcelable {


    long teacher_id;//": 312,
    String mobile_support;//": "Y",
    int zhibo_state;//": 3,
    String teacher_name;//": "董悦006",
    String data_type;//": "ZHIBO",
    String section_title;///": "3",
    String url;//": "http:\/\/imagetest.unixue.com\/unixue\/1976\/1463189471279.JPG",
    long start_time;//": 1463986800,
    String title;//": "美术",
    long org_id;//": 1976,
    long updated_at;//": 1463970316,
    String is_free;//": "N",
    double price;//": 200,
    long end_time;//": 1464001200,
    int sold_num;//": 4,
    long course_id;//": 2858,
    double sale_price;//": 0,
    String created_at;//": "2016-05-16T14:06:34",
    int stocks;//": 9999,
    long id;//": 1421,
    String section_desc;//": "3"
    String org_logo;
    String domain;
    String course_desc;
    String shop_name;
    String course_ad;

    public static List<TeacherCourseEntity> getListFromJson(JSONArray js) {
        if (null == js) {
            return null;
        }
        return JsonUtil.json2List(js.toString(), TeacherCourseEntity.class);
    }

    public String getSectionTime() {
        if (start_time <= 0 || end_time <= 0) {
            return "";
        }
        Date beginDate = new Date(start_time * 1000);
        Date endDate = new Date(end_time * 1000);

        DateFormat FORMATOR_HOUR_MINUTE = new SimpleDateFormat("HH:mm");
        String tempEndTime = FORMATOR_HOUR_MINUTE.format(endDate);

        DateFormat FORMATE_TO_HOUR = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        return FORMATE_TO_HOUR.format(beginDate) + "-" + tempEndTime;

    }


    public long getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(long teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getMobile_support() {
        return mobile_support;
    }

    public void setMobile_support(String mobile_support) {
        this.mobile_support = mobile_support;
    }

    public int getZhibo_state() {
        return zhibo_state;
    }

    public void setZhibo_state(int zhibo_state) {
        this.zhibo_state = zhibo_state;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getSection_title() {
        return section_title;
    }

    public void setSection_title(String section_title) {
        this.section_title = section_title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(long org_id) {
        this.org_id = org_id;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public String getIs_free() {
        return is_free;
    }

    public void setIs_free(String is_free) {
        this.is_free = is_free;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public int getSold_num() {
        return sold_num;
    }

    public void setSold_num(int sold_num) {
        this.sold_num = sold_num;
    }

    public long getCourse_id() {
        return course_id;
    }

    public void setCourse_id(long course_id) {
        this.course_id = course_id;
    }

    public double getSale_price() {
        return sale_price;
    }

    public void setSale_price(double sale_price) {
        this.sale_price = sale_price;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getStocks() {
        return stocks;
    }

    public void setStocks(int stocks) {
        this.stocks = stocks;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSection_desc() {
        return section_desc;
    }

    public void setSection_desc(String section_desc) {
        this.section_desc = section_desc;
    }

    public String getOrg_logo() {
        return org_logo;
    }

    public void setOrg_logo(String org_logo) {
        this.org_logo = org_logo;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCourse_desc() {
        return course_desc;
    }

    public void setCourse_desc(String course_desc) {
        this.course_desc = course_desc;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getCourse_ad() {
        return course_ad;
    }

    public void setCourse_ad(String course_ad) {
        this.course_ad = course_ad;
    }

    public String getCourseDesc() {
        StringBuilder stringBuilder = new StringBuilder();
        String[] values =course_desc.split("]");
        int startIndex;
        for (String value : values) {
            startIndex = value.lastIndexOf(":") + 1;
            if (!value.contains("img")) {
                stringBuilder.append(value.substring(startIndex, value.length()));
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(teacher_id);
        out.writeString(mobile_support);
        out.writeInt(zhibo_state);
        out.writeString(teacher_name);
        out.writeString(data_type);
        out.writeString(section_title);
        out.writeString(url);
        out.writeLong(start_time);
        out.writeString(title);
        out.writeLong(org_id);
        out.writeLong(updated_at);
        out.writeString(is_free);
        out.writeDouble(price);
        out.writeLong(end_time);
        out.writeInt(sold_num);
        out.writeLong(course_id);
        out.writeDouble(sale_price);
        out.writeString(created_at);
        out.writeInt(stocks);
        out.writeLong(id);
        out.writeString(section_desc);
        out.writeString(org_logo);
        out.writeString(domain);
        out.writeString(course_desc);
        out.writeString(shop_name);
        out.writeString(course_ad);
    }

    public static final Parcelable.Creator<TeacherCourseEntity> CREATOR = new Parcelable.Creator<TeacherCourseEntity>() {
        public TeacherCourseEntity createFromParcel(Parcel out) {
            TeacherCourseEntity teacherCourseEntity = new TeacherCourseEntity();
            teacherCourseEntity.teacher_id = out.readLong();
            teacherCourseEntity.mobile_support = out.readString();
            teacherCourseEntity.zhibo_state = out.readInt();
            teacherCourseEntity.teacher_name = out.readString();
            teacherCourseEntity.data_type = out.readString();
            teacherCourseEntity.section_title = out.readString();
            teacherCourseEntity.url = out.readString();
            teacherCourseEntity.start_time = out.readLong();
            teacherCourseEntity.title = out.readString();
            teacherCourseEntity.org_id = out.readLong();
            teacherCourseEntity.updated_at = out.readLong();
            teacherCourseEntity.is_free = out.readString();
            teacherCourseEntity.price = out.readDouble();
            teacherCourseEntity.end_time = out.readLong();
            teacherCourseEntity.sold_num = out.readInt();
            teacherCourseEntity.course_id = out.readLong();
            teacherCourseEntity.sale_price = out.readDouble();
            teacherCourseEntity.created_at = out.readString();
            teacherCourseEntity.stocks = out.readInt();
            teacherCourseEntity.id = out.readLong();
            teacherCourseEntity.section_desc = out.readString();
            teacherCourseEntity.org_logo = out.readString();
            teacherCourseEntity.domain = out.readString();
            teacherCourseEntity.course_desc = out.readString();
            teacherCourseEntity.shop_name = out.readString();
            teacherCourseEntity.course_ad = out.readString();

            return teacherCourseEntity;
        }

        public TeacherCourseEntity[] newArray(int size) {
            return new TeacherCourseEntity[size];
        }
    };
}
