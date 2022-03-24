package ache.io.service.viewer;

import ache.ACHEContext;
import ache.io.tool.MonitoringAgent;
import io.ninei.service.DefaultRoom;
import io.ninei.service.DefaultRoomService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Service
public final class ViewerRoomService extends DefaultRoomService<Viewer> implements ACHEContext {

    @Override
    public synchronized DefaultRoom enterRoom(Viewer user) throws Exception {
        DefaultRoom room = getMatchingRoom();
        room.enterUser(user);
        monitoringAgent.setUserCount(room.getUserCount());
        log.info("{} Enter Complete ( {} )!! - {}", room.getRoomName(), room.getUserCount(), user.getTraceInfo());
        return room;
    }

    @Override
    public synchronized DefaultRoom exitRoom(Viewer user) throws Exception {
        DefaultRoom room = user.getRoom();
        if(room == null) {
            log.warn("User Room is NONE!!");
            return null;
        }
        room.exitUser(user);
        monitoringAgent.setUserCount(room.getUserCount());
        log.info("{} Exit Complete ( {} )!! - {}", room.getRoomName(), room.getUserCount(), user.getTraceInfo());
        return room;
    }

    @Override
    protected synchronized DefaultRoom getMatchingRoom() throws Exception {
        Map<String, DefaultRoom> roomMap = getRoomMap();
        for (DefaultRoom room : roomMap.values()) {
            if (room.isFull()) continue;
            else return room;
        }

        return createRoom();
    }

    @Override
    public ViewerRoom createRoom() {
        Integer cnt = atomic.incrementAndGet();
        String id = cnt + UUID.randomUUID().toString();
        ViewerRoom viewerRoom = new ViewerRoom(id, "View Room-"+cnt, ROOM_MAX_USER_COUNT);
        addRoom(viewerRoom);
        return viewerRoom;
    }

    @Override
    protected void dispose() {
        atomic = null;
        monitoringAgent = null;
    }

    private ViewerRoomService(MonitoringAgent monitoringAgent) {
        this.monitoringAgent = monitoringAgent;
    }

    private MonitoringAgent monitoringAgent;
    private AtomicInteger atomic = new AtomicInteger(0);
}
