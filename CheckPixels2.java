import java.io.File;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class CheckPixels2 {
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
        
        int transparent = new Color(0, 0, 0, 0).getRGB();
        
        // Only flood fill pure black or very close to it (e.g. max 5,5,5)
        floodFillStrict(outImg, 0, 0, width, height, transparent);
        floodFillStrict(outImg, width-1, 0, width, height, transparent);
        floodFillStrict(outImg, 0, height-1, width, height, transparent);
        floodFillStrict(outImg, width-1, height-1, width, height, transparent);
        
        File dir = new File("app/src/main/res/drawable");
        dir.mkdirs();
        File outputFile = new File(dir, "kissanime_logo.png");
        ImageIO.write(outImg, "PNG", outputFile);
        System.out.println("Strict flood fill saved to " + outputFile.getAbsolutePath());
    }
    
    static void floodFillStrict(BufferedImage img, int startX, int startY, int w, int h, int transparent) {
        Color startC = new Color(img.getRGB(startX, startY), true);
        if (startC.getRed() > 10 || startC.getGreen() > 10 || startC.getBlue() > 10) return;
        
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
                    if(nc.getRed() <= 5 && nc.getGreen() <= 5 && nc.getBlue() <= 5 && nc.getAlpha() > 200) {
                        visited[nx][ny] = true;
                        q.add(new int[]{nx, ny});
                    }
                }
            }
        }
    }
}
