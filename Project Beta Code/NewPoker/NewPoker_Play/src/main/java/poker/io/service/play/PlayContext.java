package poker.io.service.play;

import lombok.Getter;
import poker.PokerContext;
import poker.io.codec.protocol.PROC_BATTING_TYPE;
import poker.io.codec.protocol.PROC_HAND_RANK_TYPE;

public interface PlayContext extends PokerContext {

    public static final int PLAYER_MAX = 5;
    public static final int CARD_MAX = 7;
    public static final Card[] cardType = new Card[0];
    public static final String EmptyMsg = "";
    public static final String ZeroCoinMsg = "0원";
    public static final String DieMsg = "기 권 패";
    public static final String DieWinnerMsg = "기 권 승";
    // Test Type: RoyalStraightFlush, FourCard, Straight
    public static final Rank_Type rankTestType = Rank_Type.RoyalStraightFlush;

    public static final int AlertBattingCount = 5;
    public static final int ValidReadyUserCount = 2;

    public static final long WHEEL_DURATION_PLAY_START = 5*1000;
    public static final long WHEEL_DURATION_CHOICE = 13*1000;
    public static final long WHEEL_TICK_BATTING = 1*1000;
    public static final int MAX_BATTING_TIMEOUT_COUNT = 10;
    public static final int MAX_CHOICE_TIMEOUT_COUNT = 10;

    public static final long POOLING_NORMAL_EXP_TIME = 3000;

    enum Play_Type {
        PLAY_USER(0, "[User]"),
        PLAY_SYS(1, "[System]"),
        PLAY_AI(2, "[AI]");

        Play_Type(int i, String m) { type = i; msg = m; }
        private int type;
        private String msg;
    }

    @Getter
    enum Room_Status {
        ROOM_INIT(0, "Play Init"),
        ROOM_PLAY_READY(1, "Play Ready"),
        ROOM_PLAY_START(2, "Play Start"),
        ROOM_CARD_CHOICE(3, "Card Choice", 4, 0, 4),
        ROOM_CHOICE_COMPLETE(4, "Choice Complete", 3, 1, 3),
        ROOM_CARD_PUSH_SECOND(5, "Push Second", 2, 3, 5),
        ROOM_CARD_PUSH_THIRD(6, "Push Third", 1, 4, 6),
        ROOM_CARD_PUSH_HIDDEN(7, "Push Hidden", 1, 4, 7),
        ROOM_PLAYER_BATTING(8, "Player Batting"),
        ROOM_PLAY_END(9, "Play End");

        Room_Status(int i, String n) { this(i, n, NONE, NONE, NONE); }
        Room_Status(int i, String n, int pc, int oc, int tc) { idx = i; pushCardCnt = pc; openedCardCnt = oc; totalCardCnt = tc; name = n; }

        private int idx, pushCardCnt, openedCardCnt, totalCardCnt;
        private String name;
    }
    @Getter
    enum Player_Status {
        PLAYER_INIT(0, "Player Init"),
        PLAYER_READY(1, "Player Ready"),
        PLAYER_WAITING(2, "Player Waiting"),
        PLAYER_DIE(3, "Player Die"),
        PLAYER_ALL_IN(4, "Player All-IN"),
        PLAYER_PLAYING(5, "Player Playing"),
        PLAYER_END(6, "Player Play End"),
        PLAYER_EXIT(7, "Player Exit");

        Player_Status(int i, String s) { idx = (byte)i; name = s; }
        private byte idx;
        private String name;
    }

    @Getter
    enum Card_Suit {
        Spade(0,400, "s"), Clover(1,300, "c"), Heart(2,200, "h"), Diamond(3,100, "d"),
        Length(4, 4, "");
        public static short valueOf(int index) { return values()[index].value; }
        public static int indexOf(short value) {
            switch (value) { case 400: return 0; case 300: return 1; case 200: return 2; case 100: return 3; }
            return NONE;
        }
        Card_Suit(int i, int v, String s) { index = (short)i; value = (short)v; name = s; }
        private short index, value;
        private String name;
    }
    @Getter
    enum Card_Number {
        Ace(0, (short)14, "A"),
        King(1, (short)13, "K"), Queen(2, (short)12, "Q"), Jack(3, (short)11, "J"),
        Ten(4, (short)10, "10"), Nine(5, (short)9, "9"), Eight(6, (short)8, "8"),
        Seven(7, (short)7, "7"), Six(8, (short)6, "6"), Five(9, (short)5, "5"),
        Four(10, (short)4, "4"), Three(11, (short)3, "3"), Two(12, (short)2, "2"),
        Length(13, (short)13, "");
        public static int indexOf(short value) { return Ace.value - value; }
        public static short valueOf(int index) { return values()[index].value; }
        public static String codeMsg(int index) { return values()[index].name;}
        public static String codeMsg(short value) { return values()[indexOf(value)].name; }
        Card_Number(int i, short v, String n) { index = i; value = v; name = n; }
        private int index;
        private short value;
        private String name;
    }

    @Getter
    enum Rank_Type {
        NoPair(0, PROC_HAND_RANK_TYPE.NO_PAIR, "탑"), // 같은 숫자, 모양, 연속된 숫자가 아닌 경우
        OnePair(1, PROC_HAND_RANK_TYPE.ONE_PAIR,"원페어"), // 같은 숫자의 카드 2장이 1쌍
        TwoPair(2, PROC_HAND_RANK_TYPE.TWO_PAIR,"투페어"), // 같은 숫자의 카드 2장이 2쌍
        Triple(3, PROC_HAND_RANK_TYPE.TRIPLE,"트리플"), // 같은 숫자의 카드가 3장
        Straight(4, PROC_HAND_RANK_TYPE.STRAIGHT,"스트레이트"), // 5장의 카드가 연속된 숫자
        BackStraight(5, PROC_HAND_RANK_TYPE.BACK_STRAIGHT,"백스트레이트"),  // A, 2, 3, 4, 5
        Mountain(6, PROC_HAND_RANK_TYPE.MOUNTAIN,"마운틴"), // A, K, Q, J, 10
        Flush(7, PROC_HAND_RANK_TYPE.FLUSH,"플러시"), // 5장의 카드가 모두 같은 무늬
        FullHouse(8, PROC_HAND_RANK_TYPE.FULL_HOUSE,"풀하우스"), // 같은 숫자 3장 + 같은 숫자 2장

        FourCard(9, PROC_HAND_RANK_TYPE.FOUR_CARD,"포카드"), // 4장 모두 같은 숫자
        StraightFlush(10, PROC_HAND_RANK_TYPE.STRAIGHT_FLUSH,"스트레이트 플러시"), // 5장의 무늬가 같고, 연속된 숫자
        BackStraightFlush(11, PROC_HAND_RANK_TYPE.BACK_STRAIGHT_FLUSH,"백스트레이트 플러시"), // 5장의 무늬가 같고, A, 2, 3, 4, 5
        RoyalStraightFlush(12, PROC_HAND_RANK_TYPE.ROYAL_STRAIGHT_FLUSH,"로얄 스트레이트 플러시"), // 5장의 무늬가 같고, A, K, Q, J, 10
        AllDie(13, PROC_HAND_RANK_TYPE.ALL_DIE, "기권승");
        Rank_Type(int i, byte h, String n) { idx = i; handRankType =h; name = n; }
        private int idx;
        private byte handRankType;
        private String name;
    }

    enum User_Menu_Type {
        First(new byte[]{PROC_BATTING_TYPE.CHECK, PROC_BATTING_TYPE.BBING, PROC_BATTING_TYPE.HALF, PROC_BATTING_TYPE.FULL, NONE, NONE, PROC_BATTING_TYPE.DIE}),
        NORMAL(new byte[]{NONE, PROC_BATTING_TYPE.BBING, PROC_BATTING_TYPE.HALF, PROC_BATTING_TYPE.FULL, PROC_BATTING_TYPE.CALL, PROC_BATTING_TYPE.DDADANG, PROC_BATTING_TYPE.DIE}),
        ONLY_CALL(new byte[]{NONE, NONE, NONE, NONE, PROC_BATTING_TYPE.CALL, NONE, PROC_BATTING_TYPE.DIE}),
        ALLIN(new byte[]{PROC_BATTING_TYPE.CHECK, NONE, NONE, NONE, PROC_BATTING_TYPE.CALL, NONE, NONE});

        public byte getValue(int index) { return isValid[index]; }
        public int length() { return isValid.length; }
        User_Menu_Type(byte[] bytes) { isValid = bytes; }

        private byte[] isValid;
    }

    default String toBattingString(byte type) {
        return PROC_BATTING_TYPE.names[type];
    }
}
