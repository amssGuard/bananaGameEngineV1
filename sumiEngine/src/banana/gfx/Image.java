package banana.gfx;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Image {
    private int w,h;
    private int[] p;
    private boolean alpha = false;

    public Image(String path) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(Image.class.getResourceAsStream(path));
        }catch(Exception e){
            e.printStackTrace();
        }

        w = image.getWidth();
        h = image.getHeight();
        p = image.getRGB(0,0,w,h,null,0,w);

        image.flush();
    }

    public Image(int []p,int w,int h){
        this.p=p;
        this.h=h;
        this.w=w;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int[] getP() {
        return p;
    }

    public void setP(int[] p) {
        this.p = p;
    }

    public void setAlpha(boolean alpha) {
        this.alpha = alpha;
    }
    public boolean isAlpha(){
        return alpha;
    }

}
