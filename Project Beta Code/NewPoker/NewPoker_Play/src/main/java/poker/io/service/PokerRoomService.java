package poker.io.service;

import io.ninei.service.DefaultRoom;
import io.ninei.service.DefaultRoomService;
import io.ninei.service.DefaultUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.io.service.play.PlayContext;
import poker.io.service.play.PokerRoom;
import poker.io.service.play.PokerUser;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Service
public final class PokerRoomService extends DefaultRoomService<PokerUser> implements PlayContext {

    public PokerRoom enterRoom(Play_Type playType, PokerUser user) throws Exception {
        log.info("{} Try Room Enter - {}",playType.name(),user);
        PokerRoom room = (PokerRoom)getMatchingRoom();
        room.enterPokerUser(user);
        log.info("Complete Room Enter - {}", user);
        room.traceAllInfo();
        return room;
    }

    @Override
    public DefaultRoom enterRoom(PokerUser user) throws Exception {
        throw new Exception("Not Implementation!!");
    }

    @Override
    public PokerRoom exitRoom(PokerUser user) throws Exception {
        PokerRoom room = (PokerRoom) user.getRoom();
        if(room == null) {
            log.info("User Room is NONE!!");
            return null;
        }
        log.info("Try Room Exit - {}", user);
        room.exitPokerUser(user);
        log.info("Complete Room Exit - {}", user);
        room.traceAllInfo();
        return room;
    }

    @Override
    protected PokerRoom createRoom() {
        Integer cnt = atomic.incrementAndGet();
        String id = cnt + UUID.randomUUID().toString();
        PokerRoom room = new PokerRoom(id, "Room-"+cnt, PLAYER_MAX);
        addRoom(room);
        return room;
    }

    @Override
    public synchronized PokerRoom getMatchingRoom() {
        Map<String, DefaultRoom> roomMap = getRoomMap();
        PokerRoom[] pokerRooms = (PokerRoom[]) roomMap.values().toArray(TYPE_ROOM);
        for (int i = 0; i < pokerRooms.length; i++) {
            if (pokerRooms[i].isFull()) continue;
            else return pokerRooms[i];
        }


        return createRoom();
    }

    @Override
    protected void dispose() {
        atomic = null;
    }

    private final static PokerRoom[] TYPE_ROOM = new PokerRoom[0];
    private AtomicInteger atomic = new AtomicInteger(0);
}
