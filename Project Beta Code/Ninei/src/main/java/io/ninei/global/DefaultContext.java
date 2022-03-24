package io.ninei.global;

import java.text.SimpleDateFormat;

public interface DefaultContext {

    String LOG_NET_HEAD = "{} >> {}";
    String LOG_NET_HEAD_EXT = "{} >> {}{}";
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd a HH:mm:ss");

    String HTTP_REQUEST_NAME_URL = "URL";
    String UTF8 = "UTF-8";

    int NONE = -1;
    String LINE_ENTER = "\r\n";
}
