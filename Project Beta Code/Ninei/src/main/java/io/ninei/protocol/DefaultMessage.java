package io.ninei.protocol;

import io.ninei.global.DefaultContext;

public abstract class DefaultMessage implements DefaultContext {

    public abstract String makeMsg(String... args);

    String getMsg() { return msg; }

    public DefaultMessage(String nMsg) {
        msg = nMsg;
    }

    protected String msg;
}
