import java.io.File;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ProcessAniwave {
    public static void main(String[] args) throws Exception {
        File inputFile = new File("file_00000000c1e8821195997f949abe88b0.png");
        BufferedImage img = ImageIO.read(inputFile);
        
        int cropX = 47;
        int cropY = 47;
        int cropW = 1207 - 47; // 1160
        int cropH = 1207 - 47; // 1160
        
        BufferedImage croppedImg = img.getSubimage(cropX, cropY, cropW, cropH);
        
        // Create a new image to hold the ARGB data (since getSubimage might share raster and not support alpha properly if original is RGB)
        BufferedImage outImg = new BufferedImage(cropW, cropH, BufferedImage.TYPE_INT_ARGB);
        for(int y=0; y<cropH; y++) {
            for(int x=0; x<cropW; x++) {
                outImg.setRGB(x, y, croppedImg.getRGB(x, y));
            }
        }
        
        int transparent = new Color(0, 0, 0, 0).getRGB();
        
        // Strict flood fill from corners
        floodFillStrict(outImg, 0, 0, cropW, cropH, transparent);
        floodFillStrict(outImg, cropW-1, 0, cropW, cropH, transparent);
        floodFillStrict(outImg, 0, cropH-1, cropW, cropH, transparent);
        floodFillStrict(outImg, cropW-1, cropH-1, cropW, cropH, transparent);
        
        File dir = new File("app/src/main/res/drawable");
        dir.mkdirs();
        File outputFile = new File(dir, "aniwave_logo.png");
        ImageIO.write(outImg, "PNG", outputFile);
        System.out.println("Aniwave processed and saved to " + outputFile.getAbsolutePath());
    }
    
    static void floodFillStrict(BufferedImage img, int startX, int startY, int w, int h, int transparent) {
        Color startC = new Color(img.getRGB(startX, startY), true);
        if (startC.getRed() > 15 || startC.getGreen() > 15 || startC.getBlue() > 15) return;
        
        java.util.Queue<int[]> q = new java.util.LinkedList<>();
        q.add(new int[]{startX, startY});
        
        boolean[][] visited = new boolean[w][h];
        visited[startX][startY] = true;
        
        while(!q.isEmpty()) {
            int[] p = q.poll();
            int x = p[0];
            int y = p[1];
            
            img.setRGB(x, y, transparent);
            
            int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};
            for(int[] d : dirs) {
                int nx = x + d[0];
                int ny = y + d[1];
                if(nx >= 0 && nx < w && ny >= 0 && ny < h && !visited[nx][ny]) {
                    Color nc = new Color(img.getRGB(nx, ny), true);
                    // Only fill if it's very dark (almost pure black)
                    if(nc.getRed() <= 10 && nc.getGreen() <= 10 && nc.getBlue() <= 10 && nc.getAlpha() > 200) {
                        visited[nx][ny] = true;
                        q.add(new int[]{nx, ny});
                    }
                }
            }
        }
    }
}
