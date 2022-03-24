package ache;

import io.ninei.global.DefaultContext;

public interface ACHEContext extends DefaultContext {

    String NET_SEND = "[SEND]";
    String NET_RECV = "[RECV]";
    String NET_PROXY = "[PROXY]";

    String NET_RECV_CLOUD_APP_READ = NET_RECV +" CloudApp: {} - {}";
    String NET_RECV_VIEWER_READ = NET_RECV +" Viewer: {} - {}";
    String NET_SEND_MSG_TO_VIEWER = NET_SEND +" MessageToViewer: {} - from {}to {}";
    String NET_SEND_MSG_TO_CLOUD = NET_SEND +" MessageToCloudApp: {} - from {}to {}";
    String NET_SEND_IMAGE_LIST = NET_SEND +" ImageList: {} - {}";
    String NET_SEND_INIT_IMAGE = NET_SEND +" InitImage: {} - {}";
    String NET_SEND_LOGIN = NET_SEND +" Login: {} - {}";

    int ROOM_MAX_USER_COUNT = 1000;
    int CPU_AVERAGE_LIMIT = 95; // CPU 사용 알림 기준치
    // FIXME: 향후 90프로 이상으로 log 분석을 통해 알람이 설정되어야 함...

    int CROP_DEFAULT_ROW_COUNT = 4;
    int CROP_DEFAULT_COL_COUNT = 5;

    long NETTY_WRITE_TIMEOUT = 600;
    long POOLING_NORMAL_EXP_TIME = 10000;

    // Server Driving Mode
    default MODE_TYPE getMode(String name) {
        if(name.trim().equalsIgnoreCase(MODE_TYPE.MODE_LIVE.name))
            return MODE_TYPE.MODE_LIVE;
        else if(name.trim().equalsIgnoreCase(MODE_TYPE.MODE_DEV.name))
            return MODE_TYPE.MODE_DEV;
        else if(name.trim().equalsIgnoreCase(MODE_TYPE.MODE_CLOUD.name))
            return MODE_TYPE.MODE_CLOUD;
        return MODE_TYPE.MODE_LOCAL;
    }

    default boolean isLive(String name) { return getMode(name).equals(MODE_TYPE.MODE_LIVE); }

    enum MODE_TYPE {
        MODE_LIVE(0, "live"),
        MODE_LOCAL(0, "local"),
        MODE_DEV(0, "dev"),
        MODE_CLOUD(0, "cloud");

        MODE_TYPE(int i, String n) { type = i; name = n; }

        private int type;
        private String name;
    }
}
