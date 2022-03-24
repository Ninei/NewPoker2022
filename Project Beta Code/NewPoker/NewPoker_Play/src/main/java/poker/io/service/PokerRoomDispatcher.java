package poker.io.service;

import io.netty.channel.group.ChannelGroupFutureListener;
import lombok.extern.log4j.Log4j2;
import poker.io.codec.PokerProtocolFactory;
import poker.io.codec.protocol.PROC_BATTING_TYPE;
import poker.io.codec.protocol.PROC_Batting;
import poker.io.codec.protocol.PROC_Poker;
import poker.io.service.play.PlayContext;
import poker.io.service.play.PokerRoom;
import poker.io.service.play.PokerUser;

@Log4j2
public class PokerRoomDispatcher implements PlayContext {

    /********************************************
     * Self Broadcasting
     ********************************************/
    public void SYS_SendUserBattingInfo(PokerUser user, PokerUser nextUser, byte battingType) throws Exception {
//        log.info(user.getDisplayName() + msg);
        room.RCV_UserBatting(PlayContext.Play_Type.PLAY_SYS, user, pf.PROC_UserBatting(user, nextUser, battingType));
    }
    public void SYS_SendUserDie(PokerUser user, PokerUser nextUser, String msg) throws Exception {
        SYS_SendUserBattingInfo(user, nextUser, PROC_BATTING_TYPE.DIE);
    }

    /********************************************
     * Broadcasting
     ********************************************/
    public void sendUserBattingInfo(PokerUser user, PokerUser nextUser, PROC_Batting battingInfo) throws Exception {
        broadcasting(pf.PROC_UserBatting(user, nextUser, battingInfo.battingType()), "Send Batting - " +
            user + " Batting: " + toBattingString(battingInfo.battingType()));
    }

    public void sendUserPushCard(PokerUser headUser) throws Exception {
        broadcasting(pf.PROC_PushCardInfo(headUser, room.getUserList()),"Push Card, HeadUser - " + headUser);
    }

    public void sendUserChoiceCard(PokerUser headUser) throws Exception {
        broadcasting(pf.PROC_ChoiceCardInfo(headUser, room.getUserList()),"Push Choice Card, HeadUser - " + headUser);
    }

    public void sendFinishGame(PokerUser winner) throws Exception {
        broadcasting(pf.Proc_GameEnd(winner, room.getUserList()), "Send Play Finish, Winner - " + winner);
    }

    public void sendStartGame() throws Exception {
        broadcasting(pf.Proc_GameStart(room.getUserList()), "Send Play Start");
    }

    public void sendRoomEnter(PokerUser user) throws Exception {
        broadcasting(pf.Proc_RoomEnter(user, room, room.getUserList()), "Send Room Enter");
    }

    public void sendRoomExit(PokerUser exitUser, PokerUser headUser) throws Exception {
        broadcasting(pf.Proc_RoomExit(exitUser, headUser, room, room.getUserList()), "Send Room Exit");
    }

    private void broadcasting(PROC_Poker sendMsg, String traceMsg) throws Exception {
        room.broadcast(sendMsg).addListener( // NOTE: operationComplete())
            (ChannelGroupFutureListener) channelFutures -> {
                if(channelFutures.isSuccess()) {
                    log.info(room.toString() + traceMsg + " - " + channelFutures.isSuccess());
                } else {
                    // FIXME: 여기서 메모리 릭 발생 중...
                    log.warn(room.toString() + traceMsg + " - " + channelFutures.isSuccess());
//                    for (ChannelFuture cf : channelFutures) {
//                        if (!cf.isSuccess()) {
//                            log.warn("Cause of failure for {} is {}", cf.channel(), cf.cause());
//                        }
//                    }
                }
                pf.release(sendMsg);
            }
        );
    }

    public PokerRoomDispatcher(PokerRoom selfRoom) {
        room = selfRoom;
        room.traceAllInfo();
    }

    private PokerRoom room;
    private PokerProtocolFactory pf = new PokerProtocolFactory();
}
