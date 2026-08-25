import java.io.File;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ProcessImage {
    public static void main(String[] args) throws Exception {
        File inputFile = new File("file_000000008ccc82118ad463f37fef4d27.png");
        BufferedImage img = ImageIO.read(inputFile);
        
        int width = img.getWidth();
        int height = img.getHeight();
        
        BufferedImage outImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                outImg.setRGB(x, y, img.getRGB(x, y));
            }
        }
        
        floodFill(outImg, 0, 0, width, height);
        floodFill(outImg, width-1, 0, width, height);
        floodFill(outImg, 0, height-1, width, height);
        floodFill(outImg, width-1, height-1, width, height);
        
        // Also do some antialiasing cleanup around the edges
        cleanupEdges(outImg, width, height);
        
        File dir = new File("app/src/main/res/drawable");
        dir.mkdirs();
        File outputFile = new File(dir, "kissanime_logo.png");
        ImageIO.write(outImg, "PNG", outputFile);
        System.out.println("Image processed and saved to " + outputFile.getAbsolutePath());
    }
    
    static void floodFill(BufferedImage img, int startX, int startY, int w, int h) {
        int targetRGB = img.getRGB(startX, startY);
        Color c = new Color(targetRGB, true);
        if (c.getRed() > 30 || c.getGreen() > 30 || c.getBlue() > 30) {
            return;
        }
        
        int transparent = new Color(0, 0, 0, 0).getRGB();
        
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
                    if(nc.getRed() < 40 && nc.getGreen() < 40 && nc.getBlue() < 40 && nc.getAlpha() > 200) {
                        visited[nx][ny] = true;
                        q.add(new int[]{nx, ny});
                    }
                }
            }
        }
    }
    
    static void cleanupEdges(BufferedImage img, int w, int h) {
        // smooth out any harsh dark pixels near transparent pixels
        for(int y=1; y<h-1; y++) {
            for(int x=1; x<w-1; x++) {
                Color c = new Color(img.getRGB(x, y), true);
                if (c.getAlpha() > 0) {
                    boolean nearTransparent = false;
                    int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};
                    for(int[] d : dirs) {
                        Color nc = new Color(img.getRGB(x+d[0], y+d[1]), true);
                        if(nc.getAlpha() == 0) nearTransparent = true;
                    }
                    if (nearTransparent && c.getRed() < 50 && c.getGreen() < 50 && c.getBlue() < 50) {
                        img.setRGB(x, y, new Color(0, 0, 0, 0).getRGB());
                    }
                }
            }
        }
    }
}
