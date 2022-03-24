package poker.io.server;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.log4j.Log4j2;
import poker.io.service.play.PlayContext;
import poker.io.service.play.PokerRoom;

import java.util.concurrent.TimeUnit;

@Log4j2
public class RoomPlayTimeTask implements TimerTask, PlayContext {

    public void cancel() {
        if (timeout == null) return;

        timeout.cancel();
        trace("ActionTask [" + actionTask.getName() + "] is Canceled!!");
        timeout = null; actionTask = null;
    }

    public void setNewTimeout(Room_Status status) throws Exception {
        actionTask = status;
        switch (status) {
            case ROOM_PLAY_START:
                taskDelay = WHEEL_DURATION_PLAY_START;
                break;
            case ROOM_CARD_CHOICE:
                taskDelay = WHEEL_DURATION_CHOICE;
                break;
            case ROOM_PLAYER_BATTING:
                taskDelay = WHEEL_TICK_BATTING;
                break;
        }
        timeout = hashedWheelTimer.newTimeout(this, taskDelay, TimeUnit.MILLISECONDS);
        trace("Set New ActionTask: " + actionTask.getName() + " - timout: " + taskDelay);
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        if(actionTask == null) return;
        trace("Timeout Occurred!! - " + actionTask.getName());
        timeout.isExpired();
        switch (actionTask) {
            case ROOM_PLAY_START:
                pokerRoom.startGame();
                break;
            case ROOM_CARD_CHOICE:
                pokerRoom.checkChoiceTimeout();
                break;
            case ROOM_PLAYER_BATTING:
                if(!pokerRoom.checkBattingTimeout()) {
                    setNewTimeout(Room_Status.ROOM_PLAYER_BATTING);
                }
                break;
        }
    }

    public void trace(String msg) {
        log.info(msg);
    }

    public void stopTimer() {
        if(hashedWheelTimer != null) hashedWheelTimer.stop();
        hashedWheelTimer = null;
    }

    public RoomPlayTimeTask(PokerRoom room) {
        pokerRoom = room;
        hashedWheelTimer = new HashedWheelTimer();
        hashedWheelTimer.start();
    }

    private Timeout timeout;
    private long taskDelay;
    private Room_Status actionTask;
    private PokerRoom pokerRoom;
    private HashedWheelTimer hashedWheelTimer;
}
