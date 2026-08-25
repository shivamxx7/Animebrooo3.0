import java.io.File;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class CheckPixels {
    public static void main(String[] args) throws Exception {
        File inputFile = new File("file_000000008ccc82118ad463f37fef4d27.png");
        BufferedImage img = ImageIO.read(inputFile);
        
        int width = img.getWidth();
        int height = img.getHeight();
        System.out.println("Size: " + width + "x" + height);
        
        for (int i = 0; i < 100; i++) {
            Color c = new Color(img.getRGB(i, i), true);
            System.out.println("Pixel at " + i + "," + i + ": " + c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        }
    }
}
