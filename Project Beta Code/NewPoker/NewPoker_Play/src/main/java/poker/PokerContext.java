package poker;


import io.ninei.global.DefaultContext;

import java.text.SimpleDateFormat;

public interface PokerContext extends DefaultContext {

    // Server Driving Mode
    default MODE_TYPE getMode(String name) {
        if(name.trim().equalsIgnoreCase(MODE_TYPE.MODE_LIVE.name))
            return MODE_TYPE.MODE_DEV;
        else if(name.trim().equalsIgnoreCase(MODE_TYPE.MODE_BMT.name))
            return MODE_TYPE.MODE_DEV;
        else if(name.trim().equalsIgnoreCase(MODE_TYPE.MODE_DEV.name))
            return MODE_TYPE.MODE_DEV;
        return MODE_TYPE.MODE_LOCAL;
    }

    enum MODE_TYPE {
        MODE_LIVE(0, "live"),
        MODE_LOCAL(0, "local"),
        MODE_DEV(0, "dev"),
        MODE_BMT(0, "bmt");

        MODE_TYPE(int i, String n) { type = i; name = n; }

        private int type;
        private String name;
    }

    default boolean isLive(String name) { return getMode(name).equals(MODE_TYPE.MODE_LIVE); }

    default boolean isTrue(String v) {
        return v.equalsIgnoreCase("TRUE") || v.equalsIgnoreCase("1");
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd a HH:mm:ss");
}
