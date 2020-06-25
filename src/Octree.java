import java.awt.*;

/*
    The Octree is a recursive data structure that divides space into 8 octants.
    To accomplish this effectively, each subtree has its own "Bounding Box", which
    determines the octant, in which data (in this case body) can be inserted based on the position vector.

    Each index in the children array has a specific octant position
    The position is easily accessed through bitwise or operation because we can setup the
    indices to be in an increasing bit addition order

    - -> less than center, + -> greater than center
    child:	0 1 2 3 4 5 6 7
    x:      - - - - + + + +
    y:      - - + + - - + +
    z:      - + - + - + - +
 */
public class Octree {

    private BoundingBox3D boundingBox;      // Bounding box representing the bounds in which the body can be placed in
    private Octree[] children;              // Represents the subtrees. If null then this node is a leaf
    private CelestialBody body;             // The body represented in the node
    private int depth = 0;                  // Depth represents how deep this subtree is in the whole tree
    private CelestialBody pseudoBody;       // The pseudobody represents the center mass and total mass of all children
    private double theta = 1;               // Theta is used for the Barnes Hut Algorithm. 1 is a standard value

    // Constructor
    public Octree(BoundingBox3D boundingBox) {
        this.boundingBox = boundingBox;
    }

    // Inserts a body b into the correct node
    public void insert(CelestialBody b) {
        // Node position is outside of the bounding box!
        if (!b.in(this.boundingBox)) {
            System.out.println("new node is not in bounds");
            return;
        }
        if (isLeaf()) {
            if (this.body == null) {
                this.pseudoBody = b;
                this.body = b;
                return;
            } else {
                // We're at a leaf, but there's already something here
                // Split this node so that it has 8 children
                // Then insert the old and new node into the correct octants

                // Save the old node for re-insertion later
                CelestialBody oldBody = this.body;
                this.body = null;
                this.children = new Octree[8];

                // Set pseudo body
                this.pseudoBody = b.pseudoBody(oldBody);

                // Compute new bounding boxes for children
                this.computeNewOctrees();

                // Re-insert the old point, and insert this new node
                this.children[oldBody.octPositionIn(this.boundingBox)].insert(oldBody);
                this.children[b.octPositionIn(this.boundingBox)].insert(b);
            }
        } else {
            // Set pseudo body
            this.pseudoBody = b.pseudoBody(this.pseudoBody);

            // Since this is not a leaf, there are still subtrees
            // We need to insert the node at the correct octant position
            int pos = b.octPositionIn(this.boundingBox);
            this.children[pos].insert(b);
        }
    }

    // Calculates new octrees and therefore bounding boxes for each child
    // The bounding boxes always get smaller for increasing depth
    private void computeNewOctrees() {
        for (int i = 0; i < this.children.length; i++) {
            this.children[i] = new Octree(this.boundingBox.subdivide(i));
            this.children[i].depth = this.depth + 1;
        }
    }

    // Returns true if the body is in the bounds of the bounding box
    public boolean inBoundingBox(CelestialBody body) {
        return body.in(this.boundingBox);
    }

    // A leaf is a node which has no children
    // Returns true if this node is a leaf
    private boolean isLeaf() { return this.children == null; }

    // Updates the force applied on the given body b based on the Barnes Hut Algorithm
    // It approximates the force calculation based on the pseudo body, if the conditions are met
    // In the worst case, the algorithm goes to the leafs to calculate forces directly with the body
    public void updateForce(CelestialBody b) {
        if (this.isLeaf()) {
            if (this.body !=b && this.body != null) b.calculateForce(this.body);
        }
        else if ((b.distanceTo(this.pseudoBody) / this.boundingBox.getLength()) > this.theta) {
            b.calculateForce(this.pseudoBody);
        } else {
            if (!Simulation.enableZCoordinate) {
                // Only check the front coordinates if z coordinates are not enabled
                if (this.children[0] != null) this.children[0].updateForce(b);
                if (this.children[2] != null) this.children[2].updateForce(b);
                if (this.children[4] != null) this.children[4].updateForce(b);
                if (this.children[6] != null) this.children[6].updateForce(b);
            } else {
                if (this.children[0] != null) this.children[0].updateForce(b);
                if (this.children[1] != null) this.children[1].updateForce(b);
                if (this.children[2] != null) this.children[2].updateForce(b);
                if (this.children[3] != null) this.children[3].updateForce(b);
                if (this.children[4] != null) this.children[4].updateForce(b);
                if (this.children[5] != null) this.children[5].updateForce(b);
                if (this.children[6] != null) this.children[6].updateForce(b);
                if (this.children[7] != null) this.children[7].updateForce(b);
            }
        }
    }

    // This function draws leaf quads in 2D, ignoring the Z-Coordinates
    public void drawLeafQuads() {
        if (isLeaf() && this.body != null) { this.boundingBox.draw(Color.green); }
        if (!Simulation.enableZCoordinate && (this.children != null)) {
            // Only check the front coordinates if z coordinates are not enabled
            if (this.children[0] != null) this.children[0].drawLeafQuads();
            if (this.children[2] != null) this.children[2].drawLeafQuads();
            if (this.children[4] != null) this.children[4].drawLeafQuads();
            if (this.children[6] != null) this.children[6].drawLeafQuads();
        } else if (children != null) {
            if (this.children[0] != null) this.children[0].drawLeafQuads();
            if (this.children[1] != null) this.children[1].drawLeafQuads();
            if (this.children[2] != null) this.children[2].drawLeafQuads();
            if (this.children[3] != null) this.children[3].drawLeafQuads();
            if (this.children[4] != null) this.children[4].drawLeafQuads();
            if (this.children[5] != null) this.children[5].drawLeafQuads();
            if (this.children[6] != null) this.children[6].drawLeafQuads();
            if (this.children[7] != null) this.children[7].drawLeafQuads();
        }
    }

    // This function draws all (complete) quads in 2D, ignoring the Z-Coordinates
    public void drawCompleteQuads() {
        boundingBox.draw(Color.green);
        if (!Simulation.enableZCoordinate && (children != null)) {
            // Only check the front coordinates if z coordinates are not enabled
            if (this.children[0] != null) this.children[0].drawCompleteQuads();
            if (this.children[2] != null) this.children[2].drawCompleteQuads();
            if (this.children[4] != null) this.children[4].drawCompleteQuads();
            if (this.children[6] != null) this.children[6].drawCompleteQuads();
        } else if (children != null) {
            if (this.children[0] != null) this.children[0].drawCompleteQuads();
            if (this.children[1] != null) this.children[1].drawCompleteQuads();
            if (this.children[2] != null) this.children[2].drawCompleteQuads();
            if (this.children[3] != null) this.children[3].drawCompleteQuads();
            if (this.children[4] != null) this.children[4].drawCompleteQuads();
            if (this.children[5] != null) this.children[5].drawCompleteQuads();
            if (this.children[6] != null) this.children[6].drawCompleteQuads();
            if (this.children[7] != null) this.children[7].drawCompleteQuads();
        }
    }

    // This function draws the center masses if the node is not a leaf in 2D, ignoring the Z-Coordinates
    public void drawCenterMasses() {
        if (!isLeaf()) pseudoBody.drawWithDepthRatio(depth);
        if (children != null) {
            if (this.children[0] != null) this.children[0].drawCenterMasses();
            if (this.children[1] != null) this.children[1].drawCenterMasses();
            if (this.children[2] != null) this.children[2].drawCenterMasses();
            if (this.children[3] != null) this.children[3].drawCenterMasses();
            if (this.children[4] != null) this.children[4].drawCenterMasses();
            if (this.children[5] != null) this.children[5].drawCenterMasses();
            if (this.children[6] != null) this.children[6].drawCenterMasses();
            if (this.children[7] != null) this.children[7].drawCenterMasses();
        }
    }
}