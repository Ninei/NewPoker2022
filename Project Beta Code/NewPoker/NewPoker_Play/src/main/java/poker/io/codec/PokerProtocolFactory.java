package poker.io.codec;

import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.buffer.Unpooled;
import io.ninei.pool.DefaultObjectPool;
import org.springframework.stereotype.Component;
import poker.io.codec.protocol.*;
import poker.io.service.play.Card;
import poker.io.service.play.PlayContext;
import poker.io.service.play.PokerRoom;
import poker.io.service.play.PokerUser;

@Component
public class PokerProtocolFactory implements PlayContext {

    public PROC_Poker Proc_Login(PokerUser user) throws Exception {
        FlatBufferBuilder fbb = pooledProtocol.getPooledFBB();
        int userOffset = createUser(fbb, user);
        PROC_Login.startPROC_Login(fbb);
        PROC_Login.addUser(fbb, userOffset);
        int endLoginOffset = PROC_Login.endPROC_Login(fbb);
        return createPokerProc(fbb, PROC_UnionTable.PROC_Login, endLoginOffset);
    }

    public PROC_Poker Proc_RoomEnter(PokerUser enterUser, PokerRoom room, PokerUser[] userList) throws Exception {
        FlatBufferBuilder fbb = pooledProtocol.getPooledFBB();

        int userOffset = createUser(fbb, enterUser);
        int roomEndOffset = createRoomInfo(fbb, room, userList);

        PROC_Room_Enter.startPROC_Room_Enter(fbb);
        PROC_Room_Enter.addEnterUser(fbb, userOffset);
        PROC_Room_Enter.addRoom(fbb, roomEndOffset);

        int endRoomEnterOffset = PROC_Room_Enter.endPROC_Room_Enter(fbb);
        return createPokerProc(fbb, PROC_UnionTable.PROC_Room_Enter, endRoomEnterOffset);
    }

    public PROC_Poker Proc_RoomExit(PokerUser exitUser, PokerUser headUser, PokerRoom room, PokerUser[] userList) throws Exception {
        FlatBufferBuilder fbb = pooledProtocol.getPooledFBB();

        int userOffset = createUser(fbb, exitUser);
        int headUserOffset = headUser == null ? NONE : createNextUserMenuInfo(fbb, headUser);
        int roomEndOffset = createRoomInfo(fbb, room, userList);

        PROC_Room_Exit.startPROC_Room_Exit(fbb);
        PROC_Room_Exit.addExitUser(fbb, userOffset);
        PROC_Room_Exit.addHeadUser(fbb, headUserOffset);
        PROC_Room_Exit.addRoom(fbb, roomEndOffset);

        int endRoomExitOffset = PROC_Room_Exit.endPROC_Room_Exit(fbb);
        return createPokerProc(fbb, PROC_UnionTable.PROC_Room_Exit, endRoomExitOffset);
    }

    public PROC_Poker Proc_GameStart(PokerUser[] userList) throws Exception {
        FlatBufferBuilder fbb = pooledProtocol.getPooledFBB();

        int[] userCardListOffset = createUserCardInfo(fbb, userList);

        int endCardListOffset = PROC_PushCard.createCardListVector(fbb, userCardListOffset);
        PROC_GAME_START.startPROC_GAME_START(fbb);
        PROC_GAME_START.addCardList(fbb, endCardListOffset);
        int endGameStartOffset = PROC_GAME_START.endPROC_GAME_START(fbb);
        return createPokerProc(fbb, PROC_UnionTable.PROC_GAME_START, endGameStartOffset);
    }

    public PROC_Poker Proc_GameEnd(PokerUser winner, PokerUser[] userList) throws Exception {
        FlatBufferBuilder fbb = pooledProtocol.getPooledFBB();

        byte handRankType = winner.getRankSignature().getRankType().getHandRankType();
        int winnerOffset = createUser(fbb, winner);

        int[] userCardListOffset = new int[userList.length];
        for(int i=0; i<userCardListOffset.length; i++) {
            if(userList[i] != null && userList[i].isPlayEnd()) {
                userCardListOffset[i] = createCardInfo(fbb, userList[i], userList[i].getRankSignature().getMarkCard());
            }
        }
        int endCardListOffset = PROC_GAME_END.createMarkCardListVector(fbb, userCardListOffset);

        PROC_GAME_END.startPROC_GAME_END(fbb);
        PROC_GAME_END.addWinner(fbb, winnerOffset);
        PROC_GAME_END.addHandRank(fbb, handRankType);
        PROC_GAME_END.addMarkCardList(fbb, endCardListOffset);
        int endGameEndOffset = PROC_GAME_END.endPROC_GAME_END(fbb);
        return createPokerProc(fbb, PROC_UnionTable.PROC_GAME_END, endGameEndOffset);
    }

    public PROC_Poker PROC_UserBatting(PokerUser curUser, PokerUser nextUser, byte type) throws Exception {
        FlatBufferBuilder fbb = pooledProtocol.getPooledFBB();

        int userOffset = createUser(fbb, curUser);
        int nextUserOffset = createNextUserMenuInfo(fbb, nextUser);

        PROC_Batting.startPROC_Batting(fbb);
        PROC_Batting.addCurUser(fbb, userOffset);
        PROC_Batting.addHeadUser(fbb, nextUserOffset);
        PROC_Batting.addBattingType(fbb, type);
        int endBattingOffset = PROC_Batting.endPROC_Batting(fbb);
        return createPokerProc(fbb, PROC_UnionTable.PROC_Batting, endBattingOffset);
    }

    public PROC_Poker PROC_PushCardInfo(PokerUser headUser, PokerUser[] userList) throws Exception {
        FlatBufferBuilder fbb = pooledProtocol.getPooledFBB();

        int headUserOffset = createHeadUserMenuInfo(fbb, headUser);
        int[] userCardListOffset = createUserCardInfo(fbb, userList);
        int endCardListOffset = PROC_PushCard.createCardListVector(fbb, userCardListOffset);

        PROC_PushCard.startPROC_PushCard(fbb);
        PROC_PushCard.addHeadUser(fbb, headUserOffset);
        PROC_PushCard.addCardList(fbb, endCardListOffset);
        int endPushCardOffset = PROC_PushCard.endPROC_PushCard(fbb);
        return createPokerProc(fbb, PROC_UnionTable.PROC_PushCard, endPushCardOffset);
    }

    public PROC_Poker PROC_ChoiceCardInfo(PokerUser headUser, PokerUser[] userList) throws Exception {
        FlatBufferBuilder fbb = pooledProtocol.getPooledFBB();

        int headUserOffset = createHeadUserMenuInfo(fbb, headUser);
        int[] userCardListOffset = createUserCardInfo(fbb, userList);
        int endCardListOffset = PROC_ChoiceCard.createCardListVector(fbb, userCardListOffset);

        PROC_ChoiceCard.startPROC_ChoiceCard(fbb);
        PROC_ChoiceCard.addHeadUser(fbb, headUserOffset);
        PROC_ChoiceCard.addCardList(fbb, endCardListOffset);
        int endPushCardOffset = PROC_ChoiceCard.endPROC_ChoiceCard(fbb);
        return createPokerProc(fbb, PROC_UnionTable.PROC_ChoiceCard, endPushCardOffset);
    }

    private int createUser(FlatBufferBuilder fbb, PokerUser user) throws Exception {
        int idOffSet = fbb.createString(user.getUuid());
        int nameOffset = fbb.createString(user.getDisplayName());
        int pwdOffset = fbb.createString(user.getUuPassword());
        int coinStrOffset = fbb.createString(user.getDisplayCoin());
        return PROC_User.createPROC_User(fbb, idOffSet, nameOffset, pwdOffset, coinStrOffset, user.isWaiting());
    }

    private int createRoomInfo(FlatBufferBuilder fbb, PokerRoom room, PokerUser[] userList) throws Exception {
        int[] userListOffset = new int[userList.length];
        for(int i=0; i<userList.length; i++) {
            if(userList[i] != null) userListOffset[i] = createUser(fbb, userList[i]);
        }
        int createUserListVectorOffset = PROC_Room.createUserListVector(fbb, userListOffset);
        PROC_Room.startPROC_Room(fbb);
        PROC_Room.addUserList(fbb, createUserListVectorOffset);
        PROC_Room.addIsPlaying(fbb, room.isPlaying());

        return PROC_Room.endPROC_Room(fbb);
    }

    private int[] createUserCardInfo(FlatBufferBuilder fbb, PokerUser[] userList) throws Exception {
        int[] userCardListOffset = new int[userList.length];
        for(int i=0; i<userCardListOffset.length; i++) {
            if(userList[i] != null) {
                userCardListOffset[i] = createCardInfo(fbb, userList[i], userList[i].getPushCard());
            }
        }
        return userCardListOffset;
    }

    private int createCardInfo(FlatBufferBuilder fbb, PokerUser user, Card[] cards) throws Exception {
        int userIdOffset = fbb.createString(user.getUuid());
        int cardRankOffset = fbb.createString(user.getCardRankMsg());

        int[] cardOffset = new int[cards.length];
        for(int i=0; i<cardOffset.length; i++) cardOffset[i] = fbb.createString(cards[i].getCodeName());
        int cardNameVectorOffset = PROC_Card.createCardListVector(fbb, cardOffset);

        return PROC_Card.createPROC_Card(fbb, userIdOffset, cardRankOffset, cardNameVectorOffset);
    }

    private int createNextUserMenuInfo(FlatBufferBuilder fbb, PokerUser user) throws Exception {
        return createMenuInfo(fbb, user, user.isAllin() ? User_Menu_Type.ALLIN : User_Menu_Type.NORMAL,
            user.isAllin() ? PROC_MENU_TYPE.ALLIN : PROC_MENU_TYPE.NORMAL);
    }

    private int createHeadUserMenuInfo(FlatBufferBuilder fbb, PokerUser user) throws Exception {
        return createMenuInfo(fbb, user, User_Menu_Type.First, PROC_MENU_TYPE.FIRST);
    }

    private int createMenuInfo(FlatBufferBuilder fbb, PokerUser user, User_Menu_Type user_menu_type, byte proc_menu_type) throws Exception {
        int userIdOffset = fbb.createString(user.getUuid());
        int[] menuItemListOffset = new int[user_menu_type.length()];
        for(int i=0; i<menuItemListOffset.length; i++) {
            menuItemListOffset[i] = PROC_MENU_ITEM.createPROC_MENU_ITEM(fbb, user_menu_type.getValue(i),
                fbb.createString(user.getBattingCoin(user_menu_type.getValue(i))));
        }
        int menuItemListVectorOffset = PROC_HeadUser.createMenuListVector(fbb, menuItemListOffset);
        return PROC_HeadUser.createPROC_HeadUser(fbb, userIdOffset, proc_menu_type, menuItemListVectorOffset);
    }

    private PROC_Poker createPokerProc(FlatBufferBuilder fbb, byte tableType, int offset) throws Exception {
        return createPokerProc(fbb, tableType, offset, pooledProtocol.getPooledPoker());
    }

    private PROC_Poker createPokerProc(FlatBufferBuilder fbb, byte tableType, int offset, PROC_Poker procPoker) throws Exception {
        PROC_Poker.startPROC_Poker(fbb);
        PROC_Poker.addProcTableType(fbb, tableType);
        PROC_Poker.addProcTable(fbb, offset);
        PROC_Poker.finishPROC_PokerBuffer(fbb, PROC_Poker.endPROC_Poker(fbb));
        PROC_Poker.getRootAsPROC_Poker(Unpooled.wrappedBuffer(fbb.sizedByteArray()).nioBuffer(), procPoker);
        pooledProtocol.release(fbb);
        return procPoker;
    }

    public void release(PROC_Poker procPoker) throws Exception {
        pooledProtocol.release(procPoker);
    }

    private class PooledProtocol {
        public PROC_Poker getPooledPoker() throws Exception { return pooledPoker.getObject(); }
        public FlatBufferBuilder getPooledFBB() throws Exception { return pooledFBB.getObject(); }
        public void release(FlatBufferBuilder fbb) throws Exception { pooledFBB.releaseObject(fbb); }
        public void release(PROC_Poker procPoker) throws Exception { pooledPoker.releaseObject(procPoker); }

        private PooledPokerProtocol pooledPoker = new PooledPokerProtocol();
        private PooledFlatBufferBuilder pooledFBB = new PooledFlatBufferBuilder();

        private final class PooledPokerProtocol extends DefaultObjectPool<PROC_Poker> {
            public PooledPokerProtocol() { super(PROC_Poker.class, POOLING_NORMAL_EXP_TIME); }
            @Override
            protected void cleanUp(PROC_Poker poker) {
                if(poker != null && poker.getByteBuffer() != null) poker.getByteBuffer().clear();
            }
        }

        private final class PooledFlatBufferBuilder extends DefaultObjectPool<FlatBufferBuilder> {
            public PooledFlatBufferBuilder() { super(FlatBufferBuilder.class, POOLING_NORMAL_EXP_TIME); }
            @Override
            protected void cleanUp(FlatBufferBuilder fbb) {
                if(fbb != null) fbb.clear();
            }
        }
    }

    private PooledProtocol pooledProtocol = new PooledProtocol();
}
