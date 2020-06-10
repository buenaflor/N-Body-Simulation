import java.awt.*;

public class BoundingBox3D {
    private Vector3 lower;          // Corner of the bounding box with the smallest values - BottomLeftBack
    private Vector3 upper;          // Corner of the bounding box with the largest values - TopRightFront
    private Vector3 center;
    private double length;

    public BoundingBox3D(Vector3 upper, Vector3 lower) {
        this.lower = lower;
        this.upper = upper;
        this.center = lower.plus(upper).divided(2);
        this.length = lower.squareLength(upper);
    }

    public BoundingBox3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        this(new Vector3(x1, y1, z1), new Vector3(x2, y2, z2));
    }

    public double getLength() {
        return length;
    }

    // Calculates a new bounding box based on the octant position
    // See enum OctPosition to see which number represents a specific octant position
    public BoundingBox3D subdivide(int octPosition) {
        if (octPosition == 0) return new BoundingBox3D(center.getX(), upper.getY(), upper.getZ(), lower.getX(), center.getY(), center.getZ()); // TLF
        if (octPosition == 1) return new BoundingBox3D(center.getX(), upper.getY(), center.getZ(), lower.getX(), center.getY(), lower.getZ()); // TLB
        if (octPosition == 2) return new BoundingBox3D(center.getX(), center.getY(), upper.getZ(), lower.getX(), lower.getY(), center.getZ()); // BLF
        if (octPosition == 3) return new BoundingBox3D(center.getX(), center.getY(), center.getZ(), lower.getX(), lower.getY(), lower.getZ()); // BLB
        if (octPosition == 4) return new BoundingBox3D(upper.getX(), upper.getY(), upper.getZ(), center.getX(), center.getY(), center.getZ()); // TRF
        if (octPosition == 5) return new BoundingBox3D(upper.getX(), upper.getY(), center.getZ(), center.getX(), center.getY(), lower.getZ()); // TRB
        if (octPosition == 6) return new BoundingBox3D(upper.getX(), center.getY(), upper.getZ(), center.getX(), lower.getY(), center.getZ()); // BRF
        if (octPosition == 7) return new BoundingBox3D(upper.getX(), center.getY(), center.getZ(), center.getX(), lower.getY(), lower.getZ()); // BRB
        return null;
    }

    public boolean contains(Vector3 position) {
        return position.greaterOrEqualThan(lower) && position.lessOrEqualThan(upper);
    }

        // Returns the octant position inside this bounding box based on the position of the node
    public int getOctPosition(Vector3 nodePosition) {
        int pos = -1;

        double midx = center.getX();
        double midy = center.getY();
        double midz = center.getZ();

        double x = nodePosition.getX();
        double y = nodePosition.getY();
        double z = nodePosition.getZ();

        if (x <= midx) {
            if (y >= midy) {
                if (z >= midz)
                    pos = OctPosition.TopLeftFront.ordinal();
                else
                    pos = OctPosition.TopLeftBack.ordinal();
            }
            else {
                if (z >= midz)
                    pos = OctPosition.BottomLeftFront.ordinal();
                else {
                    pos = OctPosition.BottomLeftBack.ordinal();
                }
            }
        } else {
            if (y >= midy) {
                if (z >= midz)
                    pos = OctPosition.TopRightFront.ordinal();
                else
                    pos = OctPosition.TopRightBack.ordinal();
            }
            else {
                if (z >= midz)
                    pos = OctPosition.BottomRightFront.ordinal();
                else
                    pos = OctPosition.BottomRightBack.ordinal();
            }
        }
        return pos;
    }

    public void draw(Color color) {
        center.drawAsSquare(length / 2, color);
    }
}