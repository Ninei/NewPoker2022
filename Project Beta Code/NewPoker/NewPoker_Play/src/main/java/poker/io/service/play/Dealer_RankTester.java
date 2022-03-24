package poker.io.service.play;

import java.util.ArrayList;

public class Dealer_RankTester extends Dealer {

    private void setHandRankTest() {
        if(rankTestType == null) return;

        dealerCardDeck.clear();
        for(int i=0; i<handRankTest.length; i++) handRankTest[i] = new ArrayList<>();
        switch (rankTestType) {
            case RoyalStraightFlush:
                // RoyalStraightFlush, BackStraightFlush, StraightFlush, Mountain
                for (int i = 0; i < Card_Suit.Length.getValue(); i++) {
                    for (int j = 0; j < Card_Number.Length.getValue(); j++) {
                        Card card = new Card(Card_Suit.values()[i], Card_Number.values()[j]);
                        // 족보 테스트 세트
                        if(i==Card_Suit.Spade.getIndex()) { // 로얄스트레이트플러시 생성
                            if(j==Card_Number.Ace.getIndex() || j==Card_Number.King.getIndex() ||
                                j==Card_Number.Queen.getIndex() || j==Card_Number.Jack.getIndex() || j==Card_Number.Ten.getIndex() ||
                                j==Card_Number.Nine.getIndex() || j==Card_Number.Eight.getIndex() || j==Card_Number.Seven.getIndex()) {
                                handRankTest[0].add(card);
                            }
                        } else if(i==Card_Suit.Clover.getIndex()) { // 백스트레이트 플러시
                            if(j==Card_Number.Ace.getIndex() || j==Card_Number.Two.getIndex() || j==Card_Number.Three.getIndex() ||
                                j==Card_Number.Four.getIndex() || j==Card_Number.Five.getIndex() ||
                                j==Card_Number.Six.getIndex() || j==Card_Number.Seven.getIndex() || j==Card_Number.Eight.getIndex()) {
                                handRankTest[1].add(card);
                            } else if(j==Card_Number.Nine.getIndex() || j==Card_Number.Ten.getIndex() ) {
                                handRankTest[4].add(card); // 9, 10
                            }
                        } else if(i==Card_Suit.Heart.getIndex()) { // 스트레이트 플러시
                            if(j==Card_Number.Two.getIndex() || j==Card_Number.Three.getIndex() ||
                                j==Card_Number.Four.getIndex() || j==Card_Number.Five.getIndex() || j==Card_Number.Six.getIndex() ||
                                j==Card_Number.Seven.getIndex() || j==Card_Number.Eight.getIndex() ||  j==Card_Number.Nine.getIndex()) {
                                handRankTest[2].add(card);
                            } else if(j==Card_Number.Ace.getIndex() || j==Card_Number.Jack.getIndex() ||
                                j==Card_Number.Queen.getIndex() || j==Card_Number.King.getIndex()) {
                                handRankTest[4].add(card); // A, 11, 12, 13
                            }
                        } else if(i==Card_Suit.Diamond.getIndex()) {
                            if(j==Card_Number.Ace.getIndex() || j==Card_Number.King.getIndex() ||
                                j==Card_Number.Queen.getIndex() || j==Card_Number.Jack.getIndex() || j==Card_Number.Ten.getIndex() ||
                                j==Card_Number.Nine.getIndex() || j==Card_Number.Eight.getIndex() || j==Card_Number.Seven.getIndex()) {
                                handRankTest[3].add(card);
                            } else if(j==Card_Number.Six.getIndex() || j==Card_Number.Five.getIndex()){
                                handRankTest[4].add(card); // 6 => 마운틴
                            }
                        }
                    }
                }
                break;
            case FourCard:
                // Ace Four Card, King FourCard, Flush, FullHouse
                for (int i = 0; i < Card_Suit.Length.getValue(); i++) {
                    for (int j = 0; j < Card_Number.Length.getValue(); j++) {
                        Card card = new Card(Card_Suit.values()[i], Card_Number.values()[j]);
                        if(j == Card_Number.Ace.getIndex()) handRankTest[0].add(card);
                        if(j == Card_Number.King.getIndex()) handRankTest[1].add(card);
                        if(j==Card_Number.Nine.getIndex() || j==Card_Number.Ten.getIndex()) {
                            handRankTest[3].add(card); // Full House
                        }

                        if(i==Card_Suit.Spade.getIndex()) { // 플러시
                            if(j==Card_Number.Queen.getIndex() || j==Card_Number.Jack.getIndex() ||
                                j==Card_Number.Three.getIndex() || j==Card_Number.Eight.getIndex() || j==Card_Number.Seven.getIndex() ||
                                j==Card_Number.Five.getIndex() || j==Card_Number.Four.getIndex() || j==Card_Number.Two.getIndex() ) {
                                handRankTest[2].add(card);
                            } else if(j==Card_Number.Six.getIndex()) {
                                handRankTest[4].add(card); // 6
                            }
                        } else if(i==Card_Suit.Clover.getIndex()) {
                            if(j==Card_Number.Two.getIndex() || j==Card_Number.Three.getIndex() ||
                                j==Card_Number.Jack.getIndex() || j==Card_Number.Six.getIndex()) {
                                handRankTest[0].add(card);
                            } else if(j==Card_Number.Eight.getIndex() || j==Card_Number.Seven.getIndex() ||
                                j==Card_Number.Five.getIndex() || j==Card_Number.Four.getIndex()) {
                                handRankTest[4].add(card); // 8, 7, 5, 4
                            }
                        } else if(i==Card_Suit.Heart.getIndex()) {
                            if(j==Card_Number.Seven.getIndex() || j==Card_Number.Eight.getIndex() ||
                                j==Card_Number.Nine.getIndex() || j==Card_Number.Jack.getIndex()) {
                                handRankTest[1].add(card);
                            }
                        } else if(i==Card_Suit.Diamond.getIndex()) {
                            if(j==Card_Number.Seven.getIndex() || j==Card_Number.Three.getIndex() || j==Card_Number.Two.getIndex()) {
                                handRankTest[4].add(card); // 7, 3, 2
                            }
                        }
                    }
                }
                break;
            case Straight:
                // Ace Four Card, King FourCard, Flush, FullHouse
                for (int i = 0; i < Card_Suit.Length.getValue(); i++) {
                    for (int j = 0; j < Card_Number.Length.getValue(); j++) {
                        Card card = new Card(Card_Suit.values()[i], Card_Number.values()[j]);
                        if(j == Card_Number.King.getIndex() || j == Card_Number.Queen.getIndex() ||
                            j == Card_Number.Jack.getIndex()) handRankTest[2].add(card);

                        if(j == Card_Number.Ten.getIndex() || j == Card_Number.Nine.getIndex() ||
                            j == Card_Number.Eight.getIndex()) handRankTest[3].add(card);

                        if(i==Card_Suit.Spade.getIndex()) {
                            if(j==Card_Number.Ace.getIndex() || j==Card_Number.Six.getIndex() || j==Card_Number.Five.getIndex() ||
                                j==Card_Number.Four.getIndex()) {
                                handRankTest[0].add(card); // A, 6, 5, 4
                            }
                        } else if(i==Card_Suit.Clover.getIndex()) {
                            if(j==Card_Number.Four.getIndex()) {
                                handRankTest[0].add(card); // 4
                            }
                            if(j==Card_Number.Ace.getIndex()|| j==Card_Number.Six.getIndex() || j==Card_Number.Five.getIndex() ||
                                j==Card_Number.Four.getIndex()) {
                                handRankTest[1].add(card); // A, 6, 5, 4
                            }
                            if(j==Card_Number.Seven.getIndex()|| j==Card_Number.Two.getIndex()) {
                                handRankTest[4].add(card); // 7, 2
                            }
                        } else if(i==Card_Suit.Heart.getIndex()) {
                            if(j==Card_Number.Three.getIndex() || j==Card_Number.Two.getIndex() ) {
                                handRankTest[0].add(card); // 3,2
                            }
                            if(j==Card_Number.Six.getIndex() || j==Card_Number.Five.getIndex()) {
                                handRankTest[1].add(card); // 6,5
                            }
                            if(j==Card_Number.Seven.getIndex() || j==Card_Number.Four.getIndex()) {
                                handRankTest[4].add(card); // 7, 4
                            }
                        } else if(i==Card_Suit.Diamond.getIndex()) {
                            if(j==Card_Number.Two.getIndex() ) {
                                handRankTest[0].add(card); // 2
                            }
                            if(j==Card_Number.Three.getIndex() || j==Card_Number.Seven.getIndex()) {
                                handRankTest[1].add(card); // 3, 7
                            }
                            if(j==Card_Number.Six.getIndex() || j==Card_Number.Five.getIndex() ||
                                j==Card_Number.Four.getIndex() || j==Card_Number.Ace.getIndex()) {
                                handRankTest[4].add(card); // 6, 5, 4, 1
                            }
                        }
                    }
                }
                break;
        }

        for(int i=0; i<5; i++) dealerCardDeck.add(handRankTest[i].get(6));
        for(int i=0; i<5; i++) dealerCardDeck.add(handRankTest[i].get(5));
        for(int i=0; i<5; i++) {
            dealerCardDeck.add(handRankTest[i].get(4));
            dealerCardDeck.add(handRankTest[i].get(3));
        }
        for(int i=0; i<5; i++) {
            dealerCardDeck.add(handRankTest[i].get(2));
            dealerCardDeck.add(handRankTest[i].get(1));
            dealerCardDeck.add(handRankTest[i].get(0));
            dealerCardDeck.add(handRankTest[i].get(7));
        }
    }

    public void resetCardDeck() {
        dealerCardDeckIdx = 40;
    }

    public Dealer_RankTester(PokerRoom pokerRoom) {
        super(pokerRoom);
        setHandRankTest();
    }

    private ArrayList<Card>[] handRankTest = new ArrayList[5];
}
