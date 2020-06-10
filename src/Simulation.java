import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class Simulation {

    // Size for the window, we assume it is a square
    public static final int WINDOWSIZE = 700;

    // one astronomical unit (AU) is the average distance of earth to the sun.
    public static final double AU = 150e9;

    // gravitational constant
    public static final double G = 6.6743e-11;

    // Radius of the universe
    public static double RADIUS = 2.83800E06;

    // Increase this to accelerate simulation speed
    public static double dt = 0.1;

    // Default is false so we can optimise the drawing of the quads/octants
    // by reducing the number of possible trees to check
    public static boolean enableZCoordinate = false;

    // Restarts the simulation if set to true
    private static boolean restartSimulation = true;

    public static void main(String[] args) {
        while (true) {
            if (restartSimulation) {
                restartSimulation = false;
                startSimulation();
            }
        }
    }

    private static void startSimulation() {
        CelestialBody[] bodies;

        System.out.print("Enter number of bodies to be generated: ");
        int numberOfBodies = StdIn.readInt();

        // Reads galaxies from the values of txt - has to be inserted manually in console
        bodies = CelestialBody.readGalaxy(numberOfBodies);

        // Setup the canvas/window including scaling
        setupWindow();

        // Number of bodies to be generated
        //bodies = CelestialBody.generateRandom(numberOfBodies);

        // Create bounding box for the tree
        Vector3 upper = new Vector3(RADIUS, RADIUS, enableZCoordinate ? RADIUS : 0);
        Vector3 lower = new Vector3(-RADIUS, -RADIUS, enableZCoordinate ? -RADIUS : 0);
        BoundingBox3D boundingBox = new BoundingBox3D(upper, lower);

        boolean showCompleteQuads = false,
                showLeafQuads = false,
                showCenterMasses = false,
                drawAsPoint = true,
                pause = false;

        while(true) {
            if (pause) {
                if (StdDraw.hasNextKeyTyped()) {
                    char key = StdDraw.nextKeyTyped();
                    if (key == 'p') pause = false;
                }
                continue;
            }
            Octree octree = new Octree(boundingBox);
            for (CelestialBody body : bodies) {
                if (octree.inBoundingBox(body)) {
                    octree.insert(body);
                }
            }

            for (int i = 0; i < bodies.length; i++) {
                bodies[i].resetForces();
                if (bodies[i].in(boundingBox)) {
                    octree.updateForce(bodies[i]);
                    bodies[i].update(dt);
                }
            }

            StdDraw.clear(StdDraw.BLACK);

            // To efficiently draw the quads, make sure enableZCoordinates is set to false!
            // It reduces the number of trees being checked
            if (showCompleteQuads) octree.drawCompleteQuads();
            if (showLeafQuads) octree.drawLeafQuads();
            if (showCenterMasses) octree.drawCenterMasses();

            for (int i = 0; i < bodies.length; i++) {
                if (drawAsPoint) {
                    bodies[i].drawAsPoint();
                } else {
                    bodies[i].drawWithRadius();
                }
            }

            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'd') showCompleteQuads = !showCompleteQuads;
                if (key == 'l') showLeafQuads = !showLeafQuads;
                if (key == 'm') showCenterMasses = !showCenterMasses;
                if (key == 'f') drawAsPoint = !drawAsPoint;
                if (key == 'r') { restartSimulation = true; break; }
                if (key == 'p') pause = true;
                if (key == '+') dt += 0.1;
                if (key == '-') dt -= 0.1;
                if (key == 'q') System.exit(0);
            }
        }
    }

    private static void setupWindow() {
        StdDraw.setCanvasSize(WINDOWSIZE, WINDOWSIZE);
        StdDraw.setXscale(-RADIUS, RADIUS);
        StdDraw.setYscale(-RADIUS, RADIUS);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);
    }

    public static double radiusWindowRatio() {
        return Simulation.RADIUS / Simulation.WINDOWSIZE;
    }
}
