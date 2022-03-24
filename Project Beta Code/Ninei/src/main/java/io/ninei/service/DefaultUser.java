package io.ninei.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.ninei.global.DefaultContext;
import io.ninei.tool.Tool;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.math.BigInteger;

@Log4j2
@Getter
public abstract class DefaultUser implements DefaultContext {

    public void sendMessage(Object msg) throws Exception {
        if(isActive) uuChannel.writeAndFlush(msg);
        else {
            log.warn("NoActive User!!, Don't Send Message - {}", msg == null ? "NULL" : msg.toString());
        }
    }

    public void setRoom(DefaultRoom enterRoom) { room = enterRoom; }

    public void logout() throws Exception {
        this.isActive = false;
    }

    public abstract void login() throws Exception;

    protected void login(String displayId, String displayName, String password) {
        this.displayId = displayId; this.displayName = displayName; uuPassword = password;
        traceInfo = "[User ID: "+uuid+", Name: " + displayName+"] ";
        isActive = true;
    }

    // 코인 설정이 필요한 경우만 사용
    protected void login(String displayId, String displayName, String password, BigInteger userCoin) {
        login(displayId, displayName, password);
        setCoin(userCoin);
    }

    public BigInteger subtractCoin(long bCoin) {
        return uuCoin.subtract(BigInteger.valueOf(bCoin));
    }

    public BigInteger addCoin(long bCoin) {
        return uuCoin.add(BigInteger.valueOf(bCoin));
    }

    public void setCoin(BigInteger userCoin) {
        uuCoin = userCoin;
        displayCoin = Tool.convertHangul(uuCoin); // FIXME: 메모리 낭비, 수정해야 함....
    }

    public void setDisplayName(String[] head, String[] name) throws Exception {
        displayName = "";
        for(int i=0; i<head.length; i++) {
            displayName += head[i] + ": " + name[i] + " ";
        }
        traceInfo = "[User ID: "+uuid+", "+ displayName+"] ";
    }

    public void setDisplayName(String name) {
        displayName = name;
        traceInfo = "[User ID: "+uuid+", Name: " + displayName+"] ";
    }

    public abstract void fireUserException(Throwable e);
    protected abstract void dispose() throws Exception;
    public void destroy() throws Exception {
        logout();
        dispose();
        uuid = null;
        uuPassword = null;
        uuChannel = null;
        uuCoin = null;

        displayId = null;
        displayName = null;
        displayCoin = null;

        uuContext = null;
        traceInfo = null;
        room = null;
    }

    public DefaultUser(ChannelHandlerContext context) {
        uuid = context.channel().id().toString(); uuContext = context; uuChannel = uuContext.channel();
        traceInfo = "[User ID: " + uuid + ", Address: " + uuChannel.remoteAddress()+"]";
        isActive = true;
    }

    private String uuid; // User Unique ID
    private String uuPassword;
    private ChannelHandlerContext uuContext;
    private Channel uuChannel;
    private BigInteger uuCoin;

    private String displayId;
    private String displayName;
    private String displayCoin;
    private String traceInfo = "";

    private boolean isActive;
    protected DefaultRoom room;
}
