package ISU;

// custom data type dedicated to information on a moose send
public class RoundSend {
    private int variant;
    private int type;
    private double delay;

    // getters
    public int getVariant() {
        return variant;
    }

    public int getType() {
        return type;
    }

    public double getDelay() {
        return delay;
    }

    public String toString() {
        return String.format("%d %d %.2f", variant, type, delay);
    }

    public RoundSend(int i1, int i2, double d) {
        variant = i1;
        type = i2;
        delay = d;
    }
}
