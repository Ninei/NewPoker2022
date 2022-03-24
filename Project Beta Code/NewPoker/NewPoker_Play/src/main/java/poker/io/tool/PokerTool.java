package poker.io.tool;

public abstract class PokerTool<T> {

    public static int getNextIndex(int idx, int max) {
        return idx > max-1 ? 0 : idx < 0 ? max-1 : idx;
    }
}
