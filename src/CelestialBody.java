import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

// This class represents celestial bodies like stars, planets, asteroids, etc..
public class CelestialBody {

    private Vector3 position;       // The center coordinates
    private Vector3 velocity;       // The current velocity of this body
    private double mass;            // Mass of the body
    private double radius;          // Radius of the body used for drawing
    private Color color;            // Color for the drawing
    private Vector3 force;          // The force applied on this body

    // Main constructor for body
    public CelestialBody(Vector3 position, Vector3 velocity, double mass, double radius, Color color) {
        this.position = position;
        this.velocity = velocity;
        this.mass   = mass;
        this.radius = radius;
        this.color  = color;
        this.force = new Vector3();
    }

    // Alternative constructor
    public CelestialBody(double px, double py, double pz, double vx, double vy, double vz, double mass, double radius, Color color) {
        this(new Vector3(px, py, pz), new Vector3(vx, vy, vz), mass, radius, color);
    }

    // Calculate the force applied on this body by body b and add it to the force vector
    public void calculateForce(CelestialBody b) {
        Vector3 direction = b.position.minus(this.position);
        double r = direction.length();
        direction.normalize();
        double F = Simulation.G * b.mass * this.mass / (r * r);
        this.force = direction.times(F).plus(force);
    }

    // Returns the euclidean distance from this body to body b
    public double distanceTo(CelestialBody b) {
        return this.position.distanceTo(b.position);
    }

    // Resets the force to 0 because we need to calculate the force fresh when the body moved
    public void resetForces() {
        this.force.reset();
    }

    // Returns true if the body is in the bounds of the bounding box
    public boolean in(BoundingBox3D box) {
        return box.contains(this.position);
    }

    // Returns the octant position index of the body in the bounding box
    public int octPositionIn(BoundingBox3D box) {
        return box.getOctPosition(this.position);
    }

    // delta t is the time quantum used to accelerate or deccelerate the simulation
    // This is based on the "leapfrog" method
    public void update(double dt) {
        this.velocity = this.force.times(dt / this.mass).plus(this.velocity);   // vx += dt * fx / mass...
        this.position = this.velocity.times(dt).plus(this.position);            // vx += dt * vx...
    }

    // Combine two bodies together to create a pseudo body, ie update center of mass and total mass of body
    // Velocity and radius are pretty much ignored since we only create a pseudobody for center mass and total mass calculation
    public CelestialBody pseudoBody(CelestialBody b) {
        CelestialBody a = this;
        double combinedMass = a.mass + b.mass;
        Vector3 combinedPosition = a.position.times(a.mass).plus(b.position.times(b.mass)).divided(combinedMass);
        CelestialBody combinedBody = new CelestialBody(combinedPosition, a.velocity, combinedMass, a.radius, a.color);
        return combinedBody;
    }

    // Draws the celestial body as a single point. Radius is predetermined by StdDraw
    public void drawAsPoint() {
        this.position.drawAsPoint(this.color);
    }

    // Draws the celestial body to the current StdDraw canvas as a dot using 'color' of this body.
    // The radius of the dot is in relation to the radius of the celestial body
    public void drawWithRadius() {
        this.position.drawAsDot(this.radius, this.color);
    }

    // Mainly used for displaying the center masses
    public void drawWithDepthRatio(int depth) {
        this.position.drawAsDot((avgBodyToUniverseRadius() * 10 / (depth + 1)), Color.pink);
    }


    // ****************** //
    //  Static methods    //
    // ****************** //

    // Generates a cluster at a specified position. Bodies are positioned within a certain radius inside the cluster
    // Implemented with a reference to an ArrayList for dynamic sizing
    public static void generateRandomCluster(Vector3 position, int numberOfBodies, double radius, ArrayList<CelestialBody> bodies) {
        for (int i = 0; i < numberOfBodies; i++) {
            double velocityRange = Simulation.radiusWindowRatio() * 4;
            double vx = Helper.getRandomNumberInRange(-velocityRange, velocityRange);
            double vy = Helper.getRandomNumberInRange(-velocityRange, velocityRange);
            double vz = Helper.getRandomNumberInRange(-velocityRange, velocityRange);
            double px = Helper.getRandomNumberInRange(position.getX() - radius, position.getX() + radius);
            double py = Helper.getRandomNumberInRange(position.getY() - radius, position.getY() + radius);
            double pz = Helper.getRandomNumberInRange(position.getZ() - radius, position.getZ() + radius);
            double randMass = Helper.getRandomNumberInRange(1.989e10, 1.00000E19);
            Color color = Helper.getRandomBrightColor();
            if (i == 0) {
                randMass = 1.989e+20;
                px = position.getX();
                py = position.getY();
            }

            bodies.add(new CelestialBody(px, py, Simulation.enableZCoordinate ? pz : 0, vx, vy, Simulation.enableZCoordinate ? vz : 0, randMass, avgBodyToUniverseRadius(), color));
        }
    }

    // Generates an array of random celestial bodies of size n
    // Implemented with a reference to an ArrayList for dynamic sizing
    public static void generateRandom(int n, double radius, ArrayList<CelestialBody> bodies) {
        for (int i = 0; i < n; i++) {
            double velocityRange = Simulation.radiusWindowRatio() * 4;
            double vx = Helper.getRandomNumberInRange(-velocityRange, velocityRange);
            double vy = Helper.getRandomNumberInRange(-velocityRange, velocityRange);
            double vz = Helper.getRandomNumberInRange(-velocityRange, velocityRange);
            double px = Helper.getRandomNumberInRange(-radius, radius);
            double py = Helper.getRandomNumberInRange(-radius, radius);
            double pz = Helper.getRandomNumberInRange(-radius, radius);
            double randMass = Helper.getRandomNumberInRange(1.989e10, 1.00000E19);
            Color color = Helper.getRandomBrightColor();
            bodies.add(new CelestialBody(px, py, Simulation.enableZCoordinate ? pz : 0, vx, vy, Simulation.enableZCoordinate ? vz : 0, randMass, avgBodyToUniverseRadius(), color));
        }
    }

    // Reads a galaxy from the "samples" folder and returns the bodies
    public static CelestialBody[] readGalaxy(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        int n = 0;
        int i = 0;
        int j = 0;
        CelestialBody[] bodies = new CelestialBody[n];
        while (sc.hasNextLine()) {
            if (i == 0) { n = Integer.parseInt(sc.nextLine()); bodies = new CelestialBody[n];}
            else if (i == 1) Simulation.RADIUS = Double.parseDouble(sc.nextLine());
            else {
                String[] split = sc.nextLine().split(" ");
                double px = Double.parseDouble(split[0]);
                double py = Double.parseDouble(split[1]);
                double vx = Double.parseDouble(split[2]);
                double vy = Double.parseDouble(split[3]);
                double mass = Double.parseDouble(split[4]);
                // index 7 is a whitespace
                int red     = Integer.parseInt(split[6]);
                int green   = Integer.parseInt(split[7]);
                int blue    = Integer.parseInt(split[8]);
                Color color = new Color(red, green, blue);
                bodies[j]   = new CelestialBody(px, py, 0, vx, vy, 0, mass, avgBodyToUniverseRadius(), color);
                j += 1;
            }
            i += 1;
        }
        return bodies;
    }

    // Calculate the avg body radius based on the universe radius to windows ratio
    public static double avgBodyToUniverseRadius() {
        return Simulation.radiusWindowRatio() * 2;
    }
}

