import java.awt.*;
import java.util.Random;


// This is a helper class that contain static methods that are useful in many other instances throughout the project
public class Helper {

    private static Random rand = new Random();

    // Generates a random bright color
    public static Color getRandomBrightColor() {
        float h = rand.nextFloat();
        float s = rand.nextFloat();
        float b = 0.8f + ((1f - 0.8f) * rand.nextFloat());
        Color color = Color.getHSBColor(h, s, b);
        return color;
    }

    // Generates a random double number in range [min, max]
    public static double getRandomNumberInRange(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }
}
