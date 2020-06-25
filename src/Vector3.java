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
        return new Vector3(this.x * d, this.y * d, this.z * d);
    }

    // Returns the product of this vector and 'd'.
    public Vector3 divided(double d) {
        return new Vector3(this.x / d, this.y / d, this.z / d);
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
    public boolean greaterOrEqualThan(Vector3 v) { return (this.x >= v.x && this.y >= v.y && this.z >= v.z); }

    // Returns true if all coordinates are less or equal than vector v coordinates
    public boolean lessOrEqualThan(Vector3 v) { return (this.x <= v.x && this.y <= v.y && this.z <= v.z); }

    // This returns the length of a side, assuming this vector and vector
    // are upper and lower corner vectors that build a square
    // Otherwise thi will return a false value
    public double squareLength(Vector3 v) {
        return Math.abs(this.x - v.x);    // you can also subtract y or z coordinates
    }

    // Normalizes this vector: changes the length of this vector such that it becomes one.
    // The direction and orientation of the vector is not affected.
    public void normalize() {
        double length = length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
    }

    // Resets all coordinates to 0
    public void reset() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    // Draws a filled circle with the center at (x,y) coordinates of this vector
    // in the existing StdDraw canvas. The z-coordinate is not used.
    public void drawAsDot(double radius, Color color) {
        StdDraw.setPenColor(color);
        StdDraw.filledCircle(this.x, this.y, radius);
    }

    // Draws a point with the x, y coordinates.
    // The radius is predetermined by StdDraw
    public void drawAsPoint(Color color) {
        StdDraw.setPenColor(color);
        StdDraw.point(this.x, this.y);
    }

    // Draws a square of the specified size, centered at (x, y).
    public void drawAsSquare(double halfLength, Color color) {
        StdDraw.setPenColor(color);
        StdDraw.square(this.x, this.y, halfLength);
    }

    // Returns x
    public double getX() {
        return this.x;
    }

    // Returns y
    public double getY() {
        return this.y;
    }

    // Returns z
    public double getZ() {
        return this.z;
    }

}

