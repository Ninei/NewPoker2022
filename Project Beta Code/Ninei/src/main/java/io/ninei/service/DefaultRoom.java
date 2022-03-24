package io.ninei.service;

import io.netty.channel.group.ChannelGroupFuture;
import io.ninei.global.DefaultContext;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Getter
public abstract class DefaultRoom implements DefaultContext {

    public ChannelGroupFuture broadcast(Object msg) throws Exception {
        return channelGroupService.broadcastChannelGroup(msg);
    }

    public int getUserCount() { return userMap.size(); }

    public synchronized void exitUser(DefaultUser user, String id) throws Exception {
        if( !userMap.containsKey(id) ) throw new Exception(traceInfo + "User None!!");

        channelGroupService.exitChannelGroup(user.getUuChannel());
        userMap.remove(id);
        user.setRoom(null);
    }

    public synchronized void exitUser(DefaultUser user) throws Exception {
        exitUser(user, user.getUuid());
    }

    public synchronized void enterUser(DefaultUser user) throws Exception {
        enterUser(user, user.getUuid());
    }

    public synchronized void enterUser(DefaultUser user, String id) throws Exception {
        if( userMap.containsKey(id) ) throw new Exception(traceInfo + "Already Exist User!!");

        channelGroupService.enterChannelGroup(user.getUuChannel());
        userMap.put(id, user);
        user.setRoom(this);
    }

    public boolean isFull() { return userMap.size()>= maxCount; }

    public void setRoomName(String name) { this.roomName = name; setTraceInfo(); }

    public void traceAllInfo() {
        log.info("{}Size: {}", traceInfo, userMap.size());
        userMap.forEach((id, user)-> log.info(user.getTraceInfo()));
    }

    private void setTraceInfo() {
        traceInfo = "[Room ID: " + roomID.substring(0,9) + ", Name: " + roomName + "] ";
    }

    public void trace() {
        log.info(traceInfo);
    }
    public void traceLog(String msg) {
        log.info(traceInfo + msg);
    }

    public void destroy() {
        dispose();
        roomID = null;
        roomName = null;
        traceInfo = null;
        if(channelGroupService != null) channelGroupService.closeChannelGroup(); channelGroupService = null;
        if(userMap != null) userMap.clear(); userMap = null;
    }

    protected abstract void dispose();

    public DefaultRoom(String id, String name, int max, DefaultChannelGroupService groupService) {
        roomID = id;
        roomName = name;
        maxCount = max;
        channelGroupService = groupService;
        traceInfo = "[Room ID: " + roomID + ", Name: " + roomName + "] ";
    }

    private String roomID;
    private String roomName;
    private int maxCount;
    private String traceInfo;

    private Map<String, DefaultUser> userMap = new ConcurrentHashMap<>();
    private DefaultChannelGroupService channelGroupService;

}
