package poker.io.service.play;

import io.ninei.global.DefaultSignature;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

@Log4j2
@Getter
public class RankSignature extends DefaultSignature implements PlayContext, Comparable<RankSignature> {

    @Override
    public int compareTo(RankSignature other) {
        Rank_Type rank_type = other.rankType;
        if(rank_type.getIdx() > rankType.getIdx()) return 1;
        else if(rank_type.getIdx() < rankType.getIdx()) return -1;
        else return compareTo(other.markCard);
    }

    private int compareTo(Card[] targetCards) {
        if(targetCards.length > 1) {
            Arrays.sort(markCard);
            Arrays.sort(targetCards);
            for(int i=0; i<targetCards.length; i++) {
                if(compareTo(markCard[i].getNumber(), targetCards[i].getNumber()) != 0) {
                    return compareTo(markCard[i].getNumber(), targetCards[i].getNumber());
                }
            }
            // NOTE: 모든 숫자가 동일한 경우, 첫번째 카드 무늬
            return markCard[0].compareTo(targetCards[0]);
        } else {
            return compareTo(markCard[0].getNumber(), targetCards[0].getNumber());
        }
    }

    private int compareTo(short mark, short target) {
        return mark == target ? 0 : mark > target ? -1 : 1;
    }

    public void setAllDieRankType() { rankType = Rank_Type.AllDie; displayName = rankType.getName(); trace(); }
    public void setRankType(PokerUser pokerUser, Rank_Type rank_type, Card[] cards) {
        user = pokerUser;
        rankType = rank_type;
        markCard = cards;
        if(markCard.length > 5) {
            markCard = Arrays.copyOfRange(markCard, 0, 5);
        }
        sub.setLength(0);
        switch (rankType) {
            case NoPair: case OnePair: case Triple: case FourCard:
                sub.append(Card_Number.codeMsg(markCard[0].getNumber()));
                break;
            case TwoPair:
                sub.append(Card_Number.codeMsg(markCard[0].getNumber()));
                sub.append(",");
                sub.append(Card_Number.codeMsg(markCard[2].getNumber()));
                break;
            case FullHouse:
                sub.append(Card_Number.codeMsg(markCard[0].getNumber()));
                sub.append(",");
                sub.append(Card_Number.codeMsg(markCard[3].getNumber()));
                break;
            case BackStraight: case BackStraightFlush: case Mountain: case RoyalStraightFlush:
                sub.append(markCard[0].getSuit());
                break;
            default:
                sub.append(markCard[0].getSuit());
                for(int i=0; i<markCard.length; i++) {
                    sub.append(",");
                    sub.append(Card_Number.codeMsg(markCard[i].getNumber()));
                }
                break;
        }
        sub.append("[");
        sub.append(sub.substring(0, sub.length()-1));
        sub.append("] ");
        sub.append(rankType.getName());
        displayName = sub.toString();
        trace();
    }

    private void trace() { log.info("RankSignature: " + user.getDisplayName() + " Rank: " + getDisplayName()); }

    public String getDisplayName() { return displayName; }

    public Card[] getMarkCard() {
        switch (rankType) {
            case NoPair: return Arrays.copyOfRange(markCard, 0, 1);
            default: return markCard;
        }
    }

    public RankSignature(String id, String name) { super(id, name); }

    private PokerUser user;
    private Rank_Type rankType;
    private Card[] markCard;
    private String displayName;
    private StringBuffer sub = new StringBuffer(); // Multi Thread Safety, No StringBuilder!!
}
