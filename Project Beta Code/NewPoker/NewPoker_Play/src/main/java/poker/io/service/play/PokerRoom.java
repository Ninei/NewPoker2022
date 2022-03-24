package poker.io.service.play;

import io.ninei.service.DefaultRoom;
import io.ninei.service.DefaultUser;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import poker.io.codec.protocol.PROC_Batting;
import poker.io.codec.protocol.PROC_Card;
import poker.io.codec.protocol.PROC_Poker;
import poker.io.server.RoomPlayTimeTask;
import poker.io.service.PokerChannelGroupService;
import poker.io.service.PokerRoomDispatcher;

@Getter
@Log4j2
public class PokerRoom extends DefaultRoom implements PlayContext {

    PROC_Batting procBatting = new PROC_Batting();
    public void RCV_UserBatting(Play_Type type, PokerUser user, PROC_Poker msg) throws Exception {
        traceLog(type.name() + "RCV_UserBatting - " + user);
        if(user.isDie()) {
            traceLog(", is Not Playing!!");
            return;
        }
        msg.procTable(procBatting);
        traceLog(", Batting Type: " + dispatcher.toBattingString(procBatting.battingType()));
        stopTimer();
        user.setBatting(procBatting.battingType());
        dispatcher.sendUserBattingInfo(user, getNextUser(user), procBatting);
        checkPlayProcess();
    }

    //FIXME: 거의 동시에 카드초이스 정보가 들어오면... 에러가..
    // 이것도 UserBatting 처럼 서버가 보내서 처리하면....
    public synchronized void RCV_UserChoiceCard(Play_Type type, PokerUser user, PROC_Card procCard) throws Exception {
        traceLog(type.name() + "RCV_UserChoiceCard - " + user);
        if(user.isDie()) {
            traceLog(", is Not Playing!!");
            return;
        }
        user.choiceCardToUserCard(procCard);
        checkPlayProcess();
    }

    public void RCV_UserReady(PokerUser user) throws Exception {
        setRoomStatus(Room_Status.ROOM_PLAY_READY);
        user.setStatus(Player_Status.PLAYER_READY);
        startTimer();
    }

    private void checkPlayProcess() throws Exception {
        if (checkPlayingUser() < 2) {
            finishGame();
        } else {
            if (isBattingFinish()) {
                if (isTurnFinish()) {
                    finishGame();
                } else {
                    pushUserCard();
                }
            } else {
                switch (roomStatus) {
                    case ROOM_CARD_CHOICE:
                        pushUserCard();
                        break;
                    case ROOM_CARD_PUSH_SECOND: case ROOM_CARD_PUSH_THIRD: case ROOM_CARD_PUSH_HIDDEN:
                        roomPlayTimeTask.setNewTimeout(Room_Status.ROOM_PLAYER_BATTING);
                        break;
                }
            }
        }
    }

    public void enterPokerUser(PokerUser user) throws Exception {
        enterUser(user);
        user.setSelfIdx(setPokerUser(null, user));
        setRoomStatus(isPlaying() ? getRoomStatus() : Room_Status.ROOM_PLAY_READY);
        // NOTE: 방정보(isPlaying)는 각각의 플레이들의 정보(isPlaying)와 별개
        user.setStatus(isPlaying() ? Player_Status.PLAYER_WAITING : Player_Status.PLAYER_READY);
        dispatcher.sendRoomEnter(user);
        startTimer();
    }

    public void exitPokerUser(PokerUser user) throws Exception {
        user.setStatus(Player_Status.PLAYER_EXIT);
        exitUser(user);
        user.setSelfIdx(setPokerUser(user, null));
        if (getUserCount() < 2) stopTimer();
        if (getUserCount() < 1) {
            setRoomStatus(Room_Status.ROOM_INIT);
        } else {
            if (isPlaying()) {
                checkPlayProcess();
                dispatcher.sendRoomExit(user, playUserCount < 2 ? null : user == nextUser ? getNextUser(user) : nextUser);
            } else {
                dispatcher.sendRoomExit(user, null);
            }
        }
    }

    public RankSignature getMyRankSignature(PokerUser user) throws Exception {
        return dealer.getMyRankSignature(user);
    }

    private PokerUser checkWinner() throws Exception {
        checkPlayingUser();
        if (playUserCount < 1) {
            log.info("Check Winner Target is Null!! - PlayUserCount Count is 0");
            return winner = nextUser = null;
        } else {
            winner = nextUser = dealer.getWinner(userList);
            if (playUserCount < 2) {
                winner.getRankSignature().setAllDieRankType();
                winner.setCardRankMsg(DieWinnerMsg);
            }
            return winner;
        }
    }

    private void pushUserCard() throws Exception {
        switch (roomStatus) {
            case ROOM_CARD_CHOICE:
                if (isChoiceFinish()) {
                    setRoomStatus(Room_Status.ROOM_CHOICE_COMPLETE);
                    stopTimer();
                    pushUserCard();
                }
                return;
            case ROOM_CHOICE_COMPLETE:
                dispatcher.sendUserChoiceCard(checkWinner());
                setRoomStatus(Room_Status.ROOM_CARD_PUSH_SECOND); // 푸시 2, 히든 2, 오픈 3, 총 5
                break;
            case ROOM_CARD_PUSH_SECOND: // 푸시 1, 히든 2, 오픈 4, 총 6
                setRoomStatus(Room_Status.ROOM_CARD_PUSH_THIRD);
                break;
            case ROOM_CARD_PUSH_THIRD: // 푸시 1, 히든 2+1, 오픈 4, 총 7
                setRoomStatus(Room_Status.ROOM_CARD_PUSH_HIDDEN);
                break;
            default:
                traceLog("Not Controlled pushUserCard!!");
                throw new Exception("Not Controlled pushUserCard!!");
        }
        pushUserCard(roomStatus.getPushCardCnt());
        roomPlayTimeTask.setNewTimeout(Room_Status.ROOM_PLAYER_BATTING);
    }

    private void pushUserCard(int cardCount) throws Exception {
        battingCount = 0;
        for (int i = 0; i < userList.length; i++) {
            if (userList[i] != null && userList[i].isPlaying()) {
                userList[i].pushCard(cardCount);
            }
        }
        totalTurnCount++;
        dispatcher.sendUserPushCard(checkWinner());
    }

    public void startGame() throws Exception {
        setRoomStatus(Room_Status.ROOM_PLAY_START);
        winner = null;
        nextUser = null;
        nextIndex = NONE;
        totalTurnCount = 0;
        dealer.resetCardDeck();
        setRoomStatus(Room_Status.ROOM_CARD_CHOICE);
        for (int i = 0; i < userList.length; i++) {
            if (userList[i] != null && userList[i].isReady()) {
                userList[i].resetCardDeck();
                userList[i].pushChoiceCard();
                userList[i].setStatus(Player_Status.PLAYER_PLAYING);
            }
        }
        dispatcher.sendStartGame();
        startTimer();
    }

    private void finishGame() throws Exception {
        setRoomStatus(Room_Status.ROOM_PLAY_END);
        stopTimer();
        if (checkWinner() == null) {
            String msg = "Finish Game!! But Winner is NULL!!!, So Exit All User";
            log.warn(this + msg);
            throw new Exception(msg);
        } else {
            for (int i = 0; i < userList.length; i++) {
                if (userList[i] != null && userList[i].isPlaying()) {
                    userList[i].setStatus(Player_Status.PLAYER_END);
                }
            }
            dispatcher.sendFinishGame(winner);
            traceLog("Finish Game Complete!! Winner is " + winner + "RankType: " +
                winner.getRankSignature().getDisplayName());
        }
        winner = nextUser = null;
    }

    private void startTimer() throws Exception {
        switch (roomStatus) {
            case ROOM_PLAY_READY: case ROOM_PLAY_END:
                if(checkReadyUser() >= ValidReadyUserCount) {
                    if(startTimer) return;
                    startTimer = true;
                    roomPlayTimeTask.setNewTimeout(Room_Status.ROOM_PLAY_START);
                }
                break;
            case ROOM_CARD_CHOICE:
                roomPlayTimeTask.setNewTimeout(Room_Status.ROOM_CARD_CHOICE);
                break;
        }
    }

    private void stopTimer() {
        startTimer = false;
        startTimeoutCount = 0;
        battingTimeoutCount = 0;
        choiceTimeoutCount = 0;
        roomPlayTimeTask.cancel();
    }

    private boolean isChoiceFinish() {
        for (int i = 0; i < userList.length; i++) {
            if (userList[i] != null && userList[i].isPlaying()) {
                if (!userList[i].isChoiceFinish()) return false;
            }
        }
        traceLog("User Choice Finish!!");
        return true;
    }

    private boolean isTurnFinish() {
        if (totalTurnCount == 3) {
            traceLog("Room Turn Finish!!");
            return true;
        }
        return false;
    }

    public boolean checkChoiceTimeout() throws Exception {
        choiceTimeoutCount++;
        if(choiceTimeoutCount > MAX_CHOICE_TIMEOUT_COUNT) {
            for (int i = 0; i < userList.length; i++) {
                if (userList[i] != null && userList[i].isPlaying() && !userList[i].isChoiceFinish()) {
                    dispatcher.SYS_SendUserDie(userList[i], nextUser, " is Choice Timeout!!");
                }
            }
            choiceTimeoutCount = 0;
            checkPlayProcess();
            return true;
        } else {
            if (choiceTimeoutCount > AlertBattingCount) {
                // Send Batting Timeout
                traceLog("ChoiceTimeoutCount: " + choiceTimeoutCount);
            }
            return false;
        }
    }

    public boolean checkBattingTimeout() throws Exception {
        battingTimeoutCount++;
        if (battingTimeoutCount > MAX_BATTING_TIMEOUT_COUNT) {
            if (nextUser != null) {
                if (nextUser.getBattingType() == NONE) {
                    currentUser = nextUser;
                    dispatcher.SYS_SendUserDie(currentUser, getNextUser(currentUser), " is Batting Timeout!!");
                }
            }
            battingTimeoutCount = 0;
            return true;
        } else {
            if (battingTimeoutCount > AlertBattingCount) {
                // Send Batting Timeout
                traceLog("BattingTimeoutCount: " + battingTimeoutCount);
            }
            return false;
        }
    }

    private boolean isBattingFinish() {
        validBattingCount = 0;
        for (int i = 0; i < userList.length; i++) {
            if (userList[i] != null && userList[i].isPlaying()) {
                if (userList[i].getBattingType() == NONE) return false;
                if (userList[i].isBattingFinish()) validBattingCount++;
            }
        }

        // 모든 유저 배팅 완료 상태
        battingCount++;
        for (int i = 0; i < userList.length; i++) {
            if (userList[i] != null && userList[i].isPlaying()) {
                userList[i].resetBatting();
            }
        }

        if (validBattingCount >= getUserCount() - 1 || battingCount > 1) {
            traceLog("User Batting Finish!!");
            return true;
        }
        return false;
    }

    private int checkPlayingUser() {
        playUserCount = 0;
        for (int i = 0; i < userList.length; i++) {
            if (userList[i] != null && userList[i].isPlaying()) {
                playUserCount++;
            }
        }
        traceLog("CheckPlayingUserCount: " + playUserCount);
        return playUserCount;
    }

    private int checkReadyUser() {
        int readyUserCount = 0;
        for(int i=0; i<userList.length; i++) {
            if (userList[i] != null && userList[i].isReady()) {
                readyUserCount++;
            }
        }
        traceLog("CheckReadyUserCount: " + readyUserCount);
        return readyUserCount;
    }

    public boolean isPlaying() {
        switch (roomStatus) {
            case ROOM_INIT: case ROOM_PLAY_READY: case ROOM_PLAY_END:
                return false;
            default:
                return true;
        }
    }

    public Card getPushCard() throws Exception {
        return dealer.getNextCard();
    }

    private void setRoomStatus(Room_Status newStatus) {
        roomStatus = newStatus;
        traceLog("SetStatus: " + roomStatus.getName());
    }

    public Room_Status getRoomStatus() { return roomStatus; }

    @Override
    public void exitUser(DefaultUser user) throws Exception {
        super.exitUser(user);
    }

    @Override
    public void enterUser(DefaultUser user) throws Exception {
        super.enterUser(user);
    }

    // NOTE: NULL -> User or User -> NULL
    private int setPokerUser(PokerUser user, PokerUser replaceUser) {
        for (int i = 0; i < userList.length; i++) {
            if (userList[i] == user) {
                userList[i] = replaceUser;
                return i;
            }
        }
        return NONE;
    }

    public PokerUser getNextUser() throws Exception {
        throw new Exception(this + "Next User is Null!!");
    }

    public PokerUser getNextUser(PokerUser user) throws Exception {
        nextUser = null;
        for (int i = 1; i < userList.length; i++) {
            nextIndex = (user.getSelfIdx() + i) % userList.length;
            if (userList[nextIndex] != null && userList[nextIndex].isPlaying()) {
                nextUser = userList[nextIndex];
                break;
            }
        }
        if (nextUser == null) getNextUser();
        return nextUser;
    }

    @Override
    public void dispose() {
        if(roomPlayTimeTask != null) roomPlayTimeTask.stopTimer(); roomPlayTimeTask = null;
        userList = null;
    }

    public PokerRoom(String id, String name, int max) {
        super(id, name, max, new PokerChannelGroupService());
        userList = new PokerUser[max];
        dealer = rankTestType == null ? new Dealer(this) : new Dealer_RankTester(this);
        dispatcher = new PokerRoomDispatcher(this);
        roomPlayTimeTask = new RoomPlayTimeTask(this);
    }

    private boolean startTimer = false;
    private int totalTurnCount, playUserCount;
    private int validBattingCount, battingCount, battingTimeoutCount;
    private int choiceTimeoutCount, startTimeoutCount;
    private Room_Status roomStatus = Room_Status.ROOM_INIT;
    private PokerUser[] userList;
    private PokerUser nextUser, currentUser;
    private PokerUser winner;
    private int nextIndex;
    private RoomPlayTimeTask roomPlayTimeTask = null;
    private final Dealer dealer;
    private final PokerRoomDispatcher dispatcher;
}
