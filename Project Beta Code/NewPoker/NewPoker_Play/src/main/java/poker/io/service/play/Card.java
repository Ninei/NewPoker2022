package poker.io.service.play;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;

@Log4j2
public final class Card implements PlayContext, Comparable<Card> {

    @Override
    public int compareTo(Card card) {
        if(number > card.number) return -1;
        else if(number < card.number) return 1;
        // same number
        else if(suit > card.suit) return -1;
        else if(suit < card.suit) return 1;
        else { // NOTE: 발생시 이슈, 절대 생기면 안되는 경우...
            log.warn(number+" vs "+card.number+" | "+suit +" vs "+card.suit);
            new Exception("Card Compare is same suit & same number").printStackTrace();
        }
        return 0;
    }

    public short getSuit() { return suit; }
    public short getNumber() { return number; }
    public String getCodeName() { return codeName; }

    public Card(short s, short n, String m) { suit=s; number=n; codeName=m; }

    public Card(Card_Suit suit, Card_Number number) {
        this(suit.getValue(), number.getValue(), suit.getName()+number.getName());
    }

    private final short suit, number;
    private final String codeName;
}
