import java.awt.Color;

// This class represents vectors in a 3D vector space.
public class Vector3 {

    private double x;
    private double y;
    private double z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Default constructor
    public Vector3() {
        this(0, 0, 0);
    }

    // Returns the Euclidean distance of this vector
    // to the specified vector 'v'.
    public double distanceTo(Vector3 v) {
        double dX = this.x - v.x;
        double dY = this.y - v.y;
        double dZ = this.z - v.z;
        return Math.sqrt(dX*dX+dY*dY+dZ*dZ);
    }

    // Returns the sum of this vector and vector 'v'.
    public Vector3 plus(Vector3 v) {
        return new Vector3(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    // Returns the product of this vector and 'd'.
    public Vector3 times(double d) {
        return new Vector3(x * d, y * d, z * d);
    }

    // Returns the product of this vector and 'd'.
    public Vector3 divided(double d) {
        return new Vector3(x / d, y / d, z / d);
    }

    // Returns the sum of this vector and -1*v.
    public Vector3 minus(Vector3 v) {
        return new Vector3(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    // Returns the length (norm) of this vector.
    public double length() {
        return distanceTo(new Vector3());
    }

    // Returns true if all coordinates are greater or equal than vector v coordinates
    public boolean greaterOrEqualThan(Vector3 v) { return (x >= v.x && y >= v.y && z >= v.z); }

    // Returns true if all coordinates are less or equal than vector v coordinates
    public boolean lessOrEqualThan(Vector3 v) { return (x <= v.x && y <= v.y && z <= v.z); }

    // This returns the length of a side, assuming this vector and vector
    // are upper and lower corner vectors that build a square
    // Otherwise thi will return a false value
    public double squareLength(Vector3 v) {
        return Math.abs(this.getX() - v.getX());    // you can also subtract y or z coordinates
    }

    // Normalizes this vector: changes the length of this vector such that it becomes one.
    // The direction and orientation of the vector is not affected.
    public void normalize() {
        double length = length();
        x /= length;
        y /= length;
        z /= length;
    }

    public void reset() {
        x = 0;
        y = 0;
        z = 0;
    }

    // Draws a filled circle with the center at (x,y) coordinates of this vector
    // in the existing StdDraw canvas. The z-coordinate is not used.
    public void drawAsDot(double radius, Color color) {
        StdDraw.setPenColor(color);
        StdDraw.filledCircle(x, y, radius);
    }

    public void drawAsPoint(Color color) {
        StdDraw.setPenColor(color);
        StdDraw.point(x, y);
    }

    public void drawAsSquare(double halfLength, Color color) {
        StdDraw.setPenColor(color);
        StdDraw.square(x, y, halfLength);
    }

    // Returns the coordinates of this vector in brackets as a string
    // in the form "[x,y,z]", e.g., "[1.48E11,0.0,0.0]".
    public String toString() {
        return String.format("[%s, %s, %s]", x, y, z);
    }

    // Prints the coordinates of this vector in brackets to the console (without newline)
    // in the form [x,y,z], e.g.,
    // [1.48E11,0.0,0.0]
    public void print() {
        System.out.print(toString());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}

