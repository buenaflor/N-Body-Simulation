import java.awt.*;
import java.util.Random;

// This class represents celestial bodies like stars, planets, asteroids, etc..
public class CelestialBody {

    Vector3 position;               // The center coordinates
    private Vector3 velocity;       // The current velocity of this body
    private double mass;
    private double radius;
    private Color color;
    private Vector3 force;          // The force applied on this body

    public CelestialBody(Vector3 position, Vector3 velocity, double mass, double radius, Color color) {
        this.position = position;
        this.velocity = velocity;
        this.mass   = mass;
        this.radius = radius;
        this.color  = color;
        this.force = new Vector3();
    }

    public CelestialBody(double px, double py, double pz, double vx, double vy, double vz, double mass, double radius, Color color) {
        this(new Vector3(px, py, pz), new Vector3(vx, vy, vz), mass, radius, color);
    }

    public void calculateForce(CelestialBody b) {
        Vector3 direction = b.position.minus(this.position);
        double r = direction.length();
        direction.normalize();
        double F = Simulation.G * b.mass * this.mass / (r * r);
        this.force = direction.times(F).plus(force);
    }

    public double distanceTo(CelestialBody b) {
        return this.position.distanceTo(b.position);
    }

    public void resetForces() {
        force.reset();
    }

    public boolean in(BoundingBox3D box) {
        return box.contains(position);
    }

    public int octPositionIn(BoundingBox3D box) {
        return box.getOctPosition(position);
    }

    public void update(double dt) {
        velocity = force.times(dt / mass).plus(velocity);   // vx += dt * fx / mass...
        position = velocity.times(dt).plus(position);       // vx += dt * vx...
    }

    // Combine two bodies together to create a pseudo body, ie update center of mass and total mass of body, a is target body
    // Velocity and radius are pretty much ignored since we only create a pseudobody for center mass and total mass calculation
    public CelestialBody pseudoBody(CelestialBody b) {
        CelestialBody a = this;
        double combinedMass = a.mass + b.mass;
        Vector3 combinedPosition = a.position.times(a.mass).plus(b.position.times(b.mass)).divided(combinedMass);
        CelestialBody combinedBody = new CelestialBody(combinedPosition, a.velocity, combinedMass, a.radius, a.color);
        return combinedBody;
    }

    public void drawAsPoint() {
        position.drawAsPoint(color);
    }

    // Draws the celestial body to the current StdDraw canvas as a dot using 'color' of this body.
    // The radius of the dot is in relation to the radius of the celestial body
    public void drawWithRadius() {
        position.drawAsDot(radius, color);
    }

    // Mainly used for displaying the center masses
    public void drawWithDepthRatio(int depth) {
        position.drawAsDot((avgRadius() * 10 / (depth + 1)), Color.pink);
    }


    // ****************** //
    //  Static methods    //
    // ****************** //

    // Generates an array of random celestial bodies of size n
    public static CelestialBody[] generateRandom(int n) {
        CelestialBody[] bodies = new CelestialBody[n];
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            double vx = Helper.getRandomNumberInRange(-9.05766E04, 9.10552E03);
            double vy = Helper.getRandomNumberInRange(-9.05766E04, 9.10552E03);
            double rx = Helper.getRandomNumberInRange(-Simulation.RADIUS / 2, Simulation.RADIUS / 2);
            double ry = Helper.getRandomNumberInRange(-Simulation.RADIUS / 2, Simulation.RADIUS / 2);
            double randMass = Helper.getRandomNumberInRange(1.989e10, 1.00000E19);
            Color color = Helper.getRandomBrightColor();
            bodies[i] = new CelestialBody(rx, ry, 0, vx, vy, 0, randMass, 10, color);
        }
        return bodies;
    }

    public static CelestialBody[] readGalaxy(int n) {
        Simulation.RADIUS = StdIn.readDouble();
        CelestialBody[] bodies = new CelestialBody[n];
        for (int i = 0; i < n; i++) {
            double px   = StdIn.readDouble();
            double py   = StdIn.readDouble();
            double vx   = StdIn.readDouble();
            double vy   = StdIn.readDouble();
            double mass = StdIn.readDouble();
            int red     = StdIn.readInt();
            int green   = StdIn.readInt();
            int blue    = StdIn.readInt();
            Color color = new Color(red, green, blue);
            bodies[i]   = new CelestialBody(px, py, 0, vx, vy, 0, mass, avgRadius(), color);
        }
        return bodies;
    }

    public static double avgRadius() {
        return Simulation.radiusWindowRatio() * 2;
    }
}

