package poker.io.service.play;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.ninei.service.DefaultUser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import poker.io.codec.protocol.PROC_BATTING_TYPE;
import poker.io.codec.protocol.PROC_Card;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

@Log4j2
@Getter
@Setter
public class PokerUser extends DefaultUser implements PlayContext {

    public void setBatting(byte type) {
        if(type == PROC_BATTING_TYPE.DIE) setUserDie();
        else battingType = type;
    }

    public boolean isBattingFinish() {
        return isPlaying() || battingType == PROC_BATTING_TYPE.CALL || battingType == PROC_BATTING_TYPE.DIE;
    }

    public void resetBatting() {
        pushCard.clear();
        battingType = NONE;
    }

    public void resetCardDeck() {
        for(int i=0; i<suitGroup.length; i++) suitGroup[i] = 0;
        for(int i=0; i<allNumberGroup.length; i++) { allNumberGroup[i] = 0; openedNumberGroup[i] = 0; }
        cardRankMsg = EmptyMsg;
        startIdx = 0; lastIdx = 0;
        isChoiceFinish = false;
        sameSuit = NONE;
        resetBatting();
        cards.clear();
        rankCard = null;
        cardRankMsg = EmptyMsg;
    }

    public void choiceCardToUserCard(PROC_Card choiceCard) throws Exception {
        if(choiceCard.cardListLength() > 3) {
            setUserDie();
        } else {
            cards.clear();
            for(int i=0; i<choiceCard.cardListLength(); i++) {
                addCard(pushCard.get(Integer.parseInt(choiceCard.cardList(i))));
                log.info("Add Choice Card >> {}", choiceCard.cardList(i));
            }
            resetBatting();
            cards.forEach((card)->{pushCard.add(card);});
            checkMyRank();
            isChoiceFinish = true;
        }
    }

    public void pushCard(int count) throws Exception {
        resetBatting();
        for(int i=0; i<count; i++) {
            pushCard.add(room.getPushCard());
            addCard(pushCard.get(i));
        }
        checkMyRank();
    }

    public void pushChoiceCard() throws Exception {
        resetBatting();
        for(int i = 0; i< Room_Status.ROOM_CARD_CHOICE.getPushCardCnt(); i++) {
            pushCard.add(room.getPushCard());
        }
    }

    public String getBattingCoin(byte battingType) throws Exception {
        switch (battingType) {
            case PROC_BATTING_TYPE.CHECK: case PROC_BATTING_TYPE.DIE:
                return ZeroCoinMsg;
            case PROC_BATTING_TYPE.BBING:
                return "삥";
            case PROC_BATTING_TYPE.HALF:
                return "하프";
            case PROC_BATTING_TYPE.FULL:
                return "풀";
            case PROC_BATTING_TYPE.CALL:
                return "콜";
            case PROC_BATTING_TYPE.DDADANG:
                return "따당";
            default:
                return EmptyMsg;
        }
    }

    private void checkMyRank() throws Exception { // 자기 패 족보 체크, rankSignature string
        cardRankMsg = isPlaying() ? room.getMyRankSignature(this).getDisplayName() : DieMsg;
        Dealer.TraceCard(this, "CheckMyRank()");
        log.info("My Rank Msg - {}", cardRankMsg);
    }

    private void addCard(Card newCard) throws Exception {
        cards.add(newCard);

        suitGroup[Card_Suit.indexOf(newCard.getSuit())]++;
        if(suitGroup[Card_Suit.indexOf(newCard.getSuit())] > 4) sameSuit = newCard.getSuit();
        allNumberGroup[Card_Number.indexOf(newCard.getNumber())]++;
        if(cards.size() > 2 && cards.size() < CARD_MAX) {
            openedNumberGroup[Card_Number.indexOf(newCard.getNumber())]++;
        }
    }

    public Card[] getPushCard() { return pushCard.toArray(cardType); }

    public short[] getOpenedNumberGroup() {
        if(cards.size() >= CARD_MAX && room.getRoomStatus() == Room_Status.ROOM_PLAY_END) {
            return allNumberGroup;
        } else return openedNumberGroup;
    }

    public Card[] getRankCard() throws Exception { return getAllRankCard(); }
    public Card[] getAllRankCard() throws Exception {
        // NOTE: Check Choice or ChoiceComplete
        if(cards.size() < 1) {
            for(int i=0; i<suitGroup.length; i++) suitGroup[i] = 0;
            for(int i=0; i<allNumberGroup.length; i++) { allNumberGroup[i] = 0; openedNumberGroup[i] = 0; }
            if(pushCard.size() > Room_Status.ROOM_CHOICE_COMPLETE.getTotalCardCnt()) pushCard.remove(0);
            for(int i=0; i<pushCard.size(); i++) addCard(pushCard.get(i));
        }
        // NOTE: Normal
        rankCard = cards.toArray(cardType);
        Arrays.sort(rankCard);
        return rankCard;
    }

    public Card[] getOpenedRankCard() throws Exception {
        switch (cards.size()) {
            case 3: startIdx = 2; lastIdx = 3; break; // 오픈된 1장
            case 5: startIdx = 2; lastIdx = 5; break; // 오픈된 3장
            case 6: startIdx = 2; lastIdx = 6; break; // 오픈된 4장
            case CARD_MAX: // 모든패 7장
                switch (room.getRoomStatus()) {
                    case ROOM_CARD_PUSH_HIDDEN:
                        startIdx = 2; lastIdx = 6; // 오픈된 4장
                        break;
                    case ROOM_PLAY_END:
                        startIdx = 0; lastIdx = CARD_MAX;
                        break;
                }
                break;
            default:
                throw new Exception("Open Card Count Wrong!! - " + cards.size());
        }
        rankCard = cards.subList(startIdx, lastIdx).toArray(cardType);
        Arrays.sort(rankCard);
        return rankCard;
    }

    public void setUserDie() {
        battingType = PROC_BATTING_TYPE.DIE;
        setStatus(Player_Status.PLAYER_DIE);
    }

    public void setStatus(Player_Status pokerUserStatus) {
        status = pokerUserStatus;
        switch (status) {
            case PLAYER_DIE: case PLAYER_EXIT:
                battingType = PROC_BATTING_TYPE.DIE;
                pushCard.clear();
                cardRankMsg = DieMsg;
                break;
        }
        log.info("SetStatus: {}", status.getName());
    }

    public boolean isPlaying() { return getStatus() == Player_Status.PLAYER_PLAYING || isAllin(); }
    public boolean isAllin() { return getStatus() == Player_Status.PLAYER_ALL_IN; }
    public boolean isReady() { return getStatus() == Player_Status.PLAYER_READY; }
    public boolean isWaiting() { return getStatus() == Player_Status.PLAYER_WAITING; }
    public boolean isPlayEnd() { return getStatus() == Player_Status.PLAYER_END; }
    public boolean isDie() { return  getStatus() == Player_Status.PLAYER_DIE; }
    public boolean checkDie() { if(isDie()) log.info("is Died!!"); return isDie(); }

    @Override
    public void login() throws Exception {
        BigInteger coin = new BigInteger("999999999999999999999999999999");
        login(getUuid(), getUuid().substring(0,8), getUuid(), coin);
    }

    @Override
    public void logout() throws Exception {

    }

    @Override
    public void fireUserException(Throwable e) {

    }

    public void dispose() {
        pushCard = null;
        battingType = NONE;
        if(cards != null) cards.clear(); cards = null;
        if(pushCard != null) pushCard.clear(); pushCard = null;
        suitGroup = null;
        allNumberGroup = null;
        openedNumberGroup = null;
        rankSignature = null;
        cardRankMsg = EmptyMsg;
    }

    public PokerUser(ChannelHandlerContext context) {
        super(context);
        rankSignature = new RankSignature(this.getUuid(), this.getDisplayName());
        selfRankSignature = new RankSignature(this.getUuid(), this.getDisplayName());
    }

    private Player_Status status = Player_Status.PLAYER_INIT;
    private int startIdx = 0, lastIdx = 0;
    private byte battingType;
    private int selfIdx = NONE;
    private boolean isChoiceFinish = false;

    private Card[] rankCard = null;
    private ArrayList<Card> pushCard = new ArrayList<>();
    private ArrayList<Card> cards = new ArrayList<>();

    private short sameSuit = NONE;
    private short[] suitGroup = new short[Card_Suit.Length.getValue()];
    private short[] allNumberGroup = new short[Card_Number.Length.getValue()];
    private short[] openedNumberGroup = new short[Card_Number.Length.getValue()];

    private RankSignature rankSignature = null;
    private RankSignature selfRankSignature = null;
    private String cardRankMsg = EmptyMsg;
    private PokerRoom room;
}
