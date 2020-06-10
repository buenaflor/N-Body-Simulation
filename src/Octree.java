import java.awt.*;

public class Octree {

    private BoundingBox3D boundingBox;      // Bounding box representing the bounds in which the body can be placed in
    private Octree[] children;
    private CelestialBody body;
    private int depth = 0;
    private CelestialBody pseudoBody;       // The pseudobody represents the center mass and total mass of all children
    private double theta = 1;

    public Octree(BoundingBox3D boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void insert(CelestialBody b) {
        // Node position is outside of the bounding box!
        if (!b.in(boundingBox)) {
            System.out.println("new node is not in bounds");
            return;
        }
        if (isLeaf()) {
            if (body == null) {
                pseudoBody = b;
                body = b;
                return;
            } else {
                // We're at a leaf, but there's already something here
                // Split this node so that it has 8 children
                // Then insert the old and new node into the correct octants

                // Save the old node for re-insertion later
                CelestialBody oldBody = body;
                body = null;
                children = new Octree[8];

                // Set pseudo body
                pseudoBody = b.pseudoBody(oldBody);

                // Compute new bounding boxes for children
                computeNewOctrees();

                // Re-insert the old point, and insert this new node
                children[oldBody.octPositionIn(boundingBox)].insert(oldBody);
                children[b.octPositionIn(boundingBox)].insert(b);
            }
        } else {
            // Set pseudo body
            pseudoBody = b.pseudoBody(pseudoBody);

            // Since this is not a leaf, there are still subtrees
            // We need to insert the node at the correct octant position
            int pos = b.octPositionIn(boundingBox);
            children[pos].insert(b);
        }
    }

    // Calculates new octrees and therefore bounding boxes for each child
    // The bounding boxes always get smaller for increasing depth
    private void computeNewOctrees() {
        for (int i = 0; i < this.children.length; i++) {
            this.children[i] = new Octree(boundingBox.subdivide(i));
            this.children[i].depth = depth + 1;
        }
    }

    public boolean inBoundingBox(CelestialBody body) {
        return body.in(boundingBox);
    }

    // A leaf is a node which has no children
    // Returns true if this node is a leaf
    private boolean isLeaf() { return children == null; }

    // Updates the force applied on the given body
    public void updateForce(CelestialBody b) {
        if (this.isLeaf()) {
            if (this.body !=b && this.body != null) b.calculateForce(this.body);
        }
        else if ((b.distanceTo(pseudoBody) / this.boundingBox.getLength()) > theta) {
            b.calculateForce(pseudoBody);
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
        if (isLeaf() && body != null) { boundingBox.draw(Color.green); }
        if (!Simulation.enableZCoordinate && (children != null)) {
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