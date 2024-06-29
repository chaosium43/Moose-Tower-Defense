package ISU;

public class Triple <E, F, G> {
    private E left;
    private F middle;
    private G right;

    public E getLeft() {
        return left;
    }

    public F getMiddle() {
        return middle;
    }

    public G getRight() {
        return right;
    }

    public Triple(E a, F b, G c) {
        left = a;
        middle = b;
        right = c;
    }

    public String toString() {
        return String.format("%s %s %s", left, middle, right);
    }
}
