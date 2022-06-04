package pt.up.fe.mobilecardriving.util;

public class Position extends Pair<Integer, Integer> {
    public Position(int x, int y) {
        super(x, y);
    }

    public int getX() {
        return super.first;
    }

    public int getY() {
        return super.second;
    }
}
