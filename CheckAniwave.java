import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class CheckAniwave {
    public static void main(String[] args) throws Exception {
        File inputFile = new File("file_00000000c1e8821195997f949abe88b0.png");
        BufferedImage img = ImageIO.read(inputFile);
        System.out.println("Size: " + img.getWidth() + "x" + img.getHeight());
    }
}
