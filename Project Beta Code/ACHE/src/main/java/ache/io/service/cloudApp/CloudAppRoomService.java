package ache.io.service.cloudApp;

import ache.ACHEContext;
import ache.io.config.ACHEConfig;
import io.ninei.service.DefaultRoom;
import io.ninei.service.DefaultRoomService;
import io.ninei.service.DefaultUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Service
public class CloudAppRoomService extends DefaultRoomService<CloudApp> implements ACHEContext {

    @Override
    public synchronized DefaultRoom enterRoom(CloudApp user) throws Exception {
        DefaultRoom room = getMatchingRoom();
        room.enterUser(user, user.getPID());
        log.info("{} Enter Complete ( {} )!! - {}", room.getRoomName(), room.getUserCount(), user.getTraceInfo());
        return room;
    }

    @Override
    public synchronized DefaultRoom exitRoom(CloudApp user) throws Exception {
        DefaultRoom room = user.getRoom();
        if(room == null) {
            log.info("User Room is NONE!!");
            return null;
        }
        room.exitUser(user, user.getPID());
        log.info("{} Exit Complete ( {} )!! - {}", room.getRoomName(), room.getUserCount(), user.getTraceInfo());
        return room;
    }

    @Override
    public synchronized DefaultRoom getMatchingRoom() {
        Map<String, DefaultRoom> roomMap = getRoomMap();
        for (DefaultRoom room : roomMap.values()) {
            if (room.isFull()) continue;
            else return room;
        }

        return createRoom();
    }

    @Override
    protected CloudAppRoom createRoom() {
        Integer cnt = atomic.incrementAndGet();
        String id = cnt + UUID.randomUUID().toString();
        CloudAppRoom cloudAppRoom = new CloudAppRoom(id, "CloudApp Room-"+cnt, ROOM_MAX_USER_COUNT);
        addRoom(cloudAppRoom);
        return cloudAppRoom;
    }

    @Override
    protected void dispose() {
        atomic = null;
    }

    private CloudAppRoomService(ACHEConfig acheConfig) {}

    private AtomicInteger atomic = new AtomicInteger(0);
}