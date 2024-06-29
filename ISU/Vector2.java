package ISU;

// helper datatype
public class Vector2 {
    double x;
    double y;
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public Vector2 add(Vector2 v) {
        return new Vector2(x + v.x, y + v.y);
    }
    public Vector2 subtract(Vector2 v) {
        return new Vector2(x - v.x, y - v.y);
    }
    public Vector2 multiply(double scalar) {
        return new Vector2(x * scalar, y * scalar);
    }
    public Vector2 divide(double scalar) {
        return new Vector2(x / scalar, y / scalar);
    }
    public Vector2 unit() {
        return divide(magnitude());
    }
    public Vector2 lerp(Vector2 v, double d) {
        return new Vector2(x * (1 - d) + v.x * d, y * (1 - d) + v.y * d);
    }
    public String toString() {
        return String.format("[%f, %f]", x, y);
    }
    public int hashCode() {
        return toString().hashCode();
    }
    public double manhattan() { // returns manhattan distance between origin and vector
        return x + y;
    }
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }
    public boolean equals(Object o) {
        try {
            Vector2 v = (Vector2)o;
            return v.x == x && v.y == y;
        } catch (ClassCastException e) {}
        return false;
    }
    public Vector2(double a, double b) {
        x = a;
        y = b;
    }
}
