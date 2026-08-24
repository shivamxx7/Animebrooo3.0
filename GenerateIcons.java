import java.io.File;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class GenerateIcons {
    public static void main(String[] args) throws Exception {
        String srcPath = "25645c54235013738fff1a89b08cbb5af18fb18afa3a9115783037abc6e00d6b-2.png";
        File srcFile = new File(srcPath);
        if (!srcFile.exists()) {
            srcFile = new File("25645c54235013738fff1a89b08cbb5af18fb18afa3a9115783037abc6e00d6b.png");
        }
        
        System.out.println("Reading source image: " + srcFile.getAbsolutePath());
        BufferedImage img = ImageIO.read(srcFile);
        if (img == null) {
            throw new RuntimeException("Could not read image!");
        }

        String baseDir = "app/src/main/res/";
        
        int[][] densities = {
            {48, 108},   // mdpi
            {72, 162},   // hdpi
            {96, 216},   // xhdpi
            {144, 324},  // xxhdpi
            {192, 432}   // xxxhdpi
        };
        String[] names = {"mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"};

        for (int i = 0; i < names.length; i++) {
            String density = names[i];
            int legacySize = densities[i][0];
            int fgSize = densities[i][1];

            File dir = new File(baseDir + "mipmap-" + density);
            dir.mkdirs();

            // 1. Square Legacy
            BufferedImage square = new BufferedImage(legacySize, legacySize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = square.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, legacySize, legacySize);
            
            double ratio = Math.min((double)legacySize / img.getWidth(), (double)legacySize / img.getHeight());
            int newW = (int)(img.getWidth() * ratio);
            int newH = (int)(img.getHeight() * ratio);
            int x = (legacySize - newW) / 2;
            int y = (legacySize - newH) / 2;
            
            g2d.drawImage(img, x, y, newW, newH, null);
            g2d.dispose();
            File fSq = new File(dir, "ic_launcher.png");
            ImageIO.write(square, "PNG", fSq);

            // 2. Round Legacy
            BufferedImage round = new BufferedImage(legacySize, legacySize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2dr = round.createGraphics();
            g2dr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2dr.setClip(new Ellipse2D.Float(0, 0, legacySize, legacySize));
            g2dr.drawImage(square, 0, 0, null);
            g2dr.dispose();
            File fRd = new File(dir, "ic_launcher_round.png");
            ImageIO.write(round, "PNG", fRd);

            // 3. Foreground
            BufferedImage fg = new BufferedImage(fgSize, fgSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2df = fg.createGraphics();
            g2df.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2df.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2df.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            double ratioFg = Math.min((double)fgSize / img.getWidth(), (double)fgSize / img.getHeight());
            int newWFg = (int)(img.getWidth() * ratioFg);
            int newHFg = (int)(img.getHeight() * ratioFg);
            int xFg = (fgSize - newWFg) / 2;
            int yFg = (fgSize - newHFg) / 2;
            
            g2df.drawImage(img, xFg, yFg, newWFg, newHFg, null);
            g2df.dispose();
            File fFg = new File(dir, "ic_launcher_foreground.png");
            ImageIO.write(fg, "PNG", fFg);
        }

        // Verify
        for (String density : names) {
            String dirPath = baseDir + "mipmap-" + density + "/";
            String[] files = {"ic_launcher.png", "ic_launcher_round.png", "ic_launcher_foreground.png"};
            for (String file : files) {
                File f = new File(dirPath + file);
                System.out.println(f.getAbsolutePath() + ": " + f.length() + " bytes");
                if (f.length() == 0) {
                    throw new RuntimeException("ERROR: " + f.getAbsolutePath() + " is 0 bytes!");
                }
            }
        }
        System.out.println("SUCCESS: All PNGs generated correctly using Java.");
    }
}
