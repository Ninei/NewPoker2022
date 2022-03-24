package poker.io.service.play;

import io.ninei.pool.DefaultObjectPool;
import lombok.extern.log4j.Log4j2;
import poker.io.tool.PokerTool;

import java.util.ArrayList;
import java.util.Collections;

@Log4j2
public class Dealer implements PlayContext {

    public PokerUser getWinner(PokerUser[] userList) throws Exception {
        ArrayList<RankSignature> rankSignatureList = pooledRankSignature.getObject();
        for (int i = 0; i < userList.length; i++) {
            if (userList[i] != null && userList[i].isPlaying()) {
                if(dealerRoom.getRoomStatus() == Room_Status.ROOM_PLAY_END) {
                    rankSignatureList.add(exploreRank(userList[i], userList[i].getAllRankCard(),
                        userList[i].getAllNumberGroup(), userList[i].getRankSignature()));
                } else {
                    rankSignatureList.add(exploreRank(userList[i], userList[i].getOpenedRankCard(),
                        userList[i].getOpenedNumberGroup(), userList[i].getRankSignature()));
                }
                Dealer.TraceCard(userList[i], "Dealer GetWinner()");
            }
        }
        Collections.sort(rankSignatureList);
        log.info("Winner: " + rankSignatureList.get(0).getUser().getDisplayName());
        PokerUser winner = rankSignatureList.get(0).getUser();
        pooledRankSignature.releaseObject(rankSignatureList);
        return winner;
    }

    public RankSignature getMyRankSignature(PokerUser user) throws Exception {
        return exploreRank(user, user.getAllRankCard(), user.getAllNumberGroup(), user.getSelfRankSignature());
    }

    protected RankSignature exploreRank(PokerUser user, Card[] target, short[] numberGroup, RankSignature rankSignature) throws Exception {
        ArrayList<Card> markCard = pooledMarkCard.getObject();
        ArrayList<Card> flushCard = pooledMarkCard.getObject();

        boolean isStraightFlush;
        boolean isFlush;
        boolean isStraight;
        short flushSuit = NONE;
        Rank_Type rankType = Rank_Type.NoPair;

        if (target.length > 4) {
            isStraightFlush = false;
            isFlush = false;
            isStraight = false;
            flushSuit = NONE;

            // 플러시 체크
            if ((flushSuit = user.getSameSuit()) != NONE) {
                isFlush = true;
                for (int i = 0; i < target.length; i++) if (target[i].getSuit() == flushSuit) flushCard.add(target[i]);
                // 스트레이트 & 플러시 체크
                isStraightFlush = checkStraight(flushCard.toArray(PlayContext.cardType), markCard);
                pooledMarkCard.releaseObject(flushCard);
            } else {
                // 스트레이트 체크
                isStraight = checkStraight(target, markCard);
            }
            // 스트레이트 & 플러시 정리
            if (isStraightFlush) { // 중복 숫자 없이 같은무늬의 연속된 숫자가 정렬된 상태
                switch (checkStraightRankType(markCard)) {
                    case Mountain:
                        rankType = Rank_Type.RoyalStraightFlush;
                        break;
                    case BackStraight:
                        rankType = Rank_Type.BackStraightFlush;
                        break;
                    case Straight:
                        rankType = Rank_Type.StraightFlush;
                        break;
                }
            } else if (isStraight) {
                // 후순위 중복제거
                for (int i = 1; i < markCard.size(); i++) {
                    if (markCard.get(i).getNumber() == markCard.get(i - 1).getNumber()) {
                        markCard.remove(i);
                    }
                }
                rankType = checkStraightRankType(markCard);
            } else if (isFlush) {
                markCard.clear();
                for (int i = 0; i < target.length; i++) {
                    if (target[i].getSuit() == flushSuit) {
                        markCard.add(target[i]);
                    }
                }
                // 플러시 정렬
                Collections.sort(markCard);
                rankType = Rank_Type.Flush;
            } else { // OnePair, TwoPair, Triple, FullHouse, FourCard
                rankType = exploreRank(target, numberGroup, markCard);
            }
        } else if (target.length > 2) { // OnePair, TwoPair, Triple, FullHouse, FourCard
            rankType = exploreRank(target, numberGroup, markCard);
        } else if (target.length == 1) { // 히든카드 오픈시 사용
            rankType = Rank_Type.NoPair;
            markCard.add(target[0]);
        } else {
            throw new Exception("Exploring Winner is Not Controlled!!" + target.length);
        }

        rankSignature.setRankType(user, rankType, markCard.toArray(cardType));
        pooledMarkCard.releaseObject(markCard);
        return rankSignature;
    }

    protected Rank_Type exploreRank(Card[] target, short[] numberGroup, ArrayList<Card> markCard) {
        markCard.clear();
        int pairCnt = 0;
        short tripleNumber = NONE;
        short fourCardNumber = NONE;
        short[] pairNumber = new short[3];
        Rank_Type rankType;

        for (int i = 0; i < numberGroup.length; i++) {
            log.debug("NumberIndex: " + Card_Number.valueOf(i) + " - " + numberGroup[i] + "");
            switch (numberGroup[i]) {
                case 2: // 3개이상 존재 가능
                    pairNumber[pairCnt++] = Card_Number.valueOf(i);
                    break;
                case 3: // 2개이상 존재 가능
                    if (tripleNumber == NONE) {
                        tripleNumber = Card_Number.valueOf(i);
                    } else {
                        if (Card_Number.valueOf(i) > tripleNumber) {
                            pairNumber[pairCnt++] = tripleNumber;
                            tripleNumber = Card_Number.valueOf(i);
                        } else {
                            pairNumber[pairCnt++] = Card_Number.valueOf(i);
                        }
                    }
                    break;
                case 4:
                    fourCardNumber = Card_Number.valueOf(i);
                    break;
            }
        }
        if (fourCardNumber != NONE) {
            for (int i = 0; i < target.length; i++) {
                if (target[i].getNumber() == fourCardNumber) markCard.add(target[i]);
            }
            rankType = Rank_Type.FourCard;
        } else {
            if (tripleNumber != NONE) {
                if (pairCnt > 0) {
                    for (int i = 0; i < target.length; i++) {
                        if (target[i].getNumber() == tripleNumber) markCard.add(target[i]);
                    }
                    for (int i = 0; i < target.length; i++) {
                        if (target[i].getNumber() == pairNumber[0]) markCard.add(target[i]);
                    }
                    rankType = Rank_Type.FullHouse;
                } else {
                    for (int i = 0; i < target.length; i++) {
                        if (target[i].getNumber() == tripleNumber) markCard.add(target[i]);
                    }
                    rankType = Rank_Type.Triple;
                }
            } else if (pairCnt > 1) {
                for (int i = 0; i < target.length; i++) {
                    if (target[i].getNumber() == pairNumber[0]) markCard.add(target[i]);
                    if (target[i].getNumber() == pairNumber[1]) markCard.add(target[i]);
                }
                rankType = Rank_Type.TwoPair;
            } else if (pairCnt == 1) {
                for (int i = 0; i < target.length; i++) {
                    if (target[i].getNumber() == pairNumber[0]) markCard.add(target[i]);
                }
                rankType = Rank_Type.OnePair;
            } else {
                for (int i = 0; i < target.length; i++) {
                    markCard.add(target[i]);
                }
                rankType = Rank_Type.NoPair;
            }
        }
        return rankType;
    }

    protected boolean checkStraight(Card[] cards, ArrayList<Card> markCard) {
        int nextIdx = 0;
        int checkStraightCnt = 0;
        markCard.clear();
        for (int i = 0; i < cards.length; i++) {
            nextIdx = PokerTool.getNextIndex(i + 1, cards.length);
            if (cards[i].getNumber() - cards[nextIdx].getNumber() == 1 ||
                cards[i].getNumber() - cards[nextIdx].getNumber() == Card_Number.Two.getValue() - Card_Number.Ace.getValue()) {
                if (!markCard.contains(cards[i])) {
                    markCard.add(cards[i]);
                }
                if (!markCard.contains(cards[nextIdx])) {
                    markCard.add(cards[nextIdx]);
                }
                checkStraightCnt++;
            } else if (cards[i].getNumber() != cards[nextIdx].getNumber()) {
                if (checkStraightCnt < 4) {
                    markCard.clear();
                    checkStraightCnt = 0;
                }
            }
        }

        Collections.sort(markCard);
        return checkStraightCnt > 3;
    }

    protected Rank_Type checkStraightRankType(ArrayList<Card> markCard) {
        Rank_Type rankType;
        if (markCard.get(0).getNumber() == Card_Number.Ace.getValue()) {
            if (markCard.get(markCard.size() - 1).getNumber() == Card_Number.Two.getValue()) {
                markCard.removeIf(card -> card.getNumber() != Card_Number.Ace.getValue() &&
                    card.getNumber() > Card_Number.Five.getValue());
                rankType = Rank_Type.BackStraight;
            } else {
                rankType = Rank_Type.Mountain;
            }
        } else {
            rankType = Rank_Type.Straight;
        }
        return rankType;
    }

    public Card getNextCard() throws Exception {
        if (dealerCardDeckIdx < 0) throw new Exception("Card Deck is Empty!!");
        return dealerCardDeck.get(--dealerCardDeckIdx);
    }

    public void resetCardDeck() {
        dealerCardDeckIdx = Card_Suit.Length.getValue() * Card_Number.Length.getValue();
        Collections.shuffle(dealerCardDeck);
    }

    public Dealer(PokerRoom pokerRoom) {
        dealerRoom = pokerRoom;
        for (int i = 0; i < Card_Suit.Length.getValue(); i++) {
            for (int j = 0; j < Card_Number.Length.getValue(); j++) {
                dealerCardDeck.add(new Card(Card_Suit.values()[i], Card_Number.values()[j]));
            }
        }

    }

    // Dealer Variables
    protected int dealerCardDeckIdx;
    protected ArrayList<Card> dealerCardDeck = new ArrayList<>();
    protected PokerRoom dealerRoom;

    // Pooled Variables
    protected PooledRankSignature pooledRankSignature = new PooledRankSignature();
    protected PooledMarkCard pooledMarkCard = new PooledMarkCard();

    protected class PooledRankSignature extends DefaultObjectPool<ArrayList<RankSignature>> {
        @Override
        protected void cleanUp(ArrayList<RankSignature> rankSignatures) {
            rankSignatures.clear();
        }
        public PooledRankSignature() {
            super((Class<ArrayList<RankSignature>>) new ArrayList<RankSignature>().getClass(), POOLING_NORMAL_EXP_TIME);
        }
    }

    protected class PooledMarkCard extends DefaultObjectPool<ArrayList<Card>> {
        @Override
        protected void cleanUp(ArrayList<Card> cards) { cards.clear(); }
        public PooledMarkCard() {
            super((Class<ArrayList<Card>>)new ArrayList<Card>().getClass(), POOLING_NORMAL_EXP_TIME);
        }
    }

    public static void TraceCard(PokerUser user, String msg) throws Exception {
        if (!log.isInfoEnabled()) return;

        String cardString = "##### " + msg + ", User: " + user.getDisplayName() + " - ";
        cardString += "allCard: ";
        Card[] cards = user.getCards().toArray(cardType);
        if (cards.length < 1) cardString += "NULL";
        else for (int i = 0; i < cards.length; i++) cardString += cards[i].getCodeName() + " | ";
        log.info(cardString);

        cardString = "pushCard: ";
        cards = user.getPushCard();
        if (cards.length < 1) cardString += "NULL";
        else for (int i = 0; i < cards.length; i++) cardString += cards[i].getCodeName() + " | ";
        log.info(cardString);

        cardString = "allRankCard: ";
        cards = user.getAllRankCard();
        if (cards.length < 1) cardString += "NULL";
        else for (int i = 0; i < cards.length; i++) cardString += cards[i].getCodeName() + " | ";
        log.info(cardString);

        cardString = "openedRankCard: ";
        cards = user.getOpenedRankCard();
        if (cards.length < 1) cardString += "NULL";
        else for (int i = 0; i < cards.length; i++) cardString += cards[i].getCodeName() + " | ";
        log.info(cardString);

        String numberString = "NumberGroupAll: ";
        short[] numberGroup = user.getAllNumberGroup();
        for (int i = 0; i < numberGroup.length; i++) numberString += numberGroup[i] + " | ";
        log.info(numberString);

        numberString = "NumberGroupRank: ";
        numberGroup = user.getOpenedNumberGroup();
        for (int i = 0; i < numberGroup.length; i++) numberString += numberGroup[i] + " | ";
        log.info(numberString);
    }
}
