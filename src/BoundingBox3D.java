import java.awt.*;

/*
    Represents a bounding box with upper and lower corners
    With upper and lower corners, it's possible to gather all the information needed for important calculations
    This bounding box can be subdivided into 8 different boxes specifically tailored for the octant positions
 */
public class BoundingBox3D {

    private Vector3 lower;          // Corner of the bounding box with the smallest values - BottomLeftBack
    private Vector3 upper;          // Corner of the bounding box with the largest values - TopRightFront
    private Vector3 center;         // Represents the center of the bounding box
    private double length;          // The length of one side of the box (square)

    // Main Constructor
    public BoundingBox3D(Vector3 upper, Vector3 lower) {
        this.lower = lower;
        this.upper = upper;
        this.center = lower.plus(upper).divided(2);
        this.length = lower.squareLength(upper);
    }

    // Alternative Constructor
    public BoundingBox3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        this(new Vector3(x1, y1, z1), new Vector3(x2, y2, z2));
    }

    // Returns the length
    public double getLength() {
        return this.length;
    }

    // Calculates a new bounding box based on the octant position
    // See Octree docs to see which number represents a specific octant position
    public BoundingBox3D subdivide(int octPosition) {
        if (octPosition == 0) return new BoundingBox3D(this.center.getX(), this.center.getY(), this.center.getZ(), this.lower.getX(), this.lower.getY(), this.lower.getZ()); // BLB
        if (octPosition == 1) return new BoundingBox3D(this.center.getX(), this.center.getY(), this.upper.getZ(), this.lower.getX(), this.lower.getY(), this.center.getZ()); // BLF
        if (octPosition == 2) return new BoundingBox3D(this.center.getX(), this.upper.getY(), this.center.getZ(), this.lower.getX(), this.center.getY(), this.lower.getZ()); // TLB
        if (octPosition == 3) return new BoundingBox3D(this.center.getX(), this.upper.getY(), this.upper.getZ(), this.lower.getX(), this.center.getY(), this.center.getZ()); // TLF
        if (octPosition == 4) return new BoundingBox3D(this.upper.getX(), this.center.getY(), this.center.getZ(), this.center.getX(), this.lower.getY(), this.lower.getZ()); // BRB
        if (octPosition == 5) return new BoundingBox3D(this.upper.getX(), this.center.getY(), this.upper.getZ(), this.center.getX(), this.lower.getY(), this.center.getZ()); // BRF
        if (octPosition == 6) return new BoundingBox3D(this.upper.getX(), this.upper.getY(), this.center.getZ(), this.center.getX(), this.center.getY(), this.lower.getZ()); // TRB
        if (octPosition == 7) return new BoundingBox3D(this.upper.getX(), this.upper.getY(), this.upper.getZ(), this.center.getX(), this.center.getY(), this.center.getZ()); // TRF
        return null;
    }

    /*
    Returns the octant position inside this bounding box based on the position of the node
    The position is easily accessed through bitwise or operation because we can setup the
    indices to be in an increasing bit addition order

    child:	0 1 2 3 4 5 6 7
    x:      - - - - + + + +
    y:      - - + + - - + +
    z:      - + - + - + - +

    */
    public int getOctPosition(Vector3 position) {
        double midx = this.center.getX();
        double midy = this.center.getY();
        double midz = this.center.getZ();

        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        int oct = 0;
        if(x >= midx) oct |= 4;
        if(y >= midy) oct |= 2;
        if(Simulation.enableZCoordinate && z >= midz) oct |= 1;
        return oct;
    }

    // Returns true if the position is inside the bounding box
    public boolean contains(Vector3 position) {
        return position.greaterOrEqualThan(this.lower) && position.lessOrEqualThan(this.upper);
    }

    // Draws the bounding box
    public void draw(Color color) {
        this.center.drawAsSquare(this.length / 2, color);
    }
}