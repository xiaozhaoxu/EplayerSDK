package cn.nodemedia.ibrightech.eplayersdkproject.entity;

/**
 * Created by zhaoxu2014 on 16/9/29.
 */
public class RoomBean {
    String name;
    String roomid;

    public RoomBean(String name, String roomid) {
        this.name = name;
        this.roomid = roomid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }
}
