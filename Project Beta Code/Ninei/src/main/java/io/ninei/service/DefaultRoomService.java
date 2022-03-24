package io.ninei.service;

import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public abstract class DefaultRoomService<T_User> implements DefaultService {

    public abstract DefaultRoom enterRoom(T_User user) throws Exception;
    public abstract DefaultRoom exitRoom(T_User user) throws Exception;

    protected abstract <T> T createRoom() throws Exception;
    protected abstract <T> T getMatchingRoom() throws Exception;

    protected void addRoom(DefaultRoom room) {
        roomMap.put(room.getRoomID(), room);
    }

    protected Map getRoomMap() { return roomMap; }

    protected abstract void dispose();

    public void destroy() {
        dispose();
        if(roomMap != null) {
            roomMap.forEach( (s, room) -> {
                room.destroy();
            });
            roomMap.clear();
        }
        roomMap = null;
    }

    private Map<String, DefaultRoom> roomMap = new ConcurrentHashMap<>();
}
