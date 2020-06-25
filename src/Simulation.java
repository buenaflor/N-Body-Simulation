import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

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

    // Starts the simulation
    private static void startSimulation() {
        CelestialBody[] bodies = new CelestialBody[0];
        Scanner sc = new Scanner(System.in);

        int choice = 0;

        while (choice != 1 && choice != 2) {
            System.out.println("Press 1: Read galaxy from files ");
            System.out.println("Press 2: Generate random bodies ");
            System.out.print("Choice: ");
            choice = sc.nextInt();

            if (choice == 1) {
                File f = new File("./src/samples");

                // Get all the names of the files present in the given directory
                File[] files = f.listFiles();

                // Prevent loading of the files if files is null
                if (files == null) {
                    System.out.println("Files could not be found");
                    choice = 0;
                    continue;
                }

                // Display the names of the files
                for (int i = 0; i < files.length; i++) {
                    System.out.println("Sample " + i + ": " + files[i].getName());
                }

                System.out.print("Which galaxy do you want to choose: ");
                int sampleChoice = sc.nextInt();
                try {
                    bodies = CelestialBody.readGalaxy(files[sampleChoice]);
                } catch (FileNotFoundException e) {
                    System.out.println("File not found: " + e);
                }
            } else if (choice == 2) {
                System.out.print("Enter number of bodies to be generated: ");
                int numberOfBodies = sc.nextInt();

                // k is the number of clusters. Clusters are within 3 and ln(numberOfBodies) to avoid too many clusters
                int k = (int) Helper.getRandomNumberInRange(3, Math.log(numberOfBodies));

                ArrayList<CelestialBody> arrList = new ArrayList<>();
                // Generates k - 1 random clusters of bodies with n/k bodies inside each cluster
                // The last portion is filled with random celestial bodies: see below
                for (int i = 1; i < k; i++) {
                    double positionOfClusterX = Helper.getRandomNumberInRange(-RADIUS, RADIUS);
                    double positionOfClusterY = Helper.getRandomNumberInRange(-RADIUS, RADIUS);
                    Vector3 clusterPosition = new Vector3(positionOfClusterX, positionOfClusterY, 0);
                    int numberOfBodiesInCluster = numberOfBodies / k;
                    double randomRadius = Helper.getRandomNumberInRange(RADIUS / 9, RADIUS / 5);
                    CelestialBody.generateRandomCluster(clusterPosition, numberOfBodiesInCluster, randomRadius, arrList);
                }

                // Generates random bodies throughout the map for the last portion of the array
                CelestialBody.generateRandom(numberOfBodies / k, RADIUS, arrList);

                // Convert the ArrayList to an Array
                bodies = arrList.toArray(bodies);
            } else {
                bodies = new CelestialBody[0];
            }
        }

        // Setup the canvas/window including scaling
        setupWindow();

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
                if (key == 'r') { restartSimulation = true; dt = 0.1; break; }
                if (key == 'p') pause = true;
                if (key == '+') dt += 0.1;
                if (key == '-') dt -= 0.1;
                if (key == 'q') System.exit(0);
            }
        }
    }

    // Sets up the window and canvas scaling
    private static void setupWindow() {
        StdDraw.setCanvasSize(WINDOWSIZE, WINDOWSIZE);
        StdDraw.setXscale(-RADIUS, RADIUS);
        StdDraw.setYscale(-RADIUS, RADIUS);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);
    }

    // Returns the universe radius to window size ratio
    public static double radiusWindowRatio() {
        return Simulation.RADIUS / Simulation.WINDOWSIZE;
    }
}
