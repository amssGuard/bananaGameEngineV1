package banana;

import banana.gfx.*;

import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Renderer {
    private int pW, pH;
    private int[] p;
    private int[] zb;
    private int zDepth = 0;
    private int[] lm;
    private int[] lb;
    private int ambientColor = 0xff232323;
    private boolean processing = false;
    private int camX,camY;
    private Font font = Font.STANDARD;
    private ArrayList<ImageRequest> imageRequest = new ArrayList<>();

    public Renderer(GameContainer gc){
        pW = gc.getWidth();
        pH = gc.getHeight();
        p = ((DataBufferInt)gc.getWindow().getImage().getRaster().getDataBuffer()).getData();
        zb = new int[p.length];
        lm = new int[p.length];
        lb = new int[p.length];
    }

    public void clear(){
        for(int i = 0; i < p.length; i++){
            p[i] = 0;
            zb[i] = 0;
            lm[i] = ambientColor;
        }
    }

    public void process(){
        processing = true;

        Collections.sort(imageRequest, (i0, i1) -> {
            if(i0.zDepth < i1.zDepth) return -1;
            if(i0.zDepth > i1.zDepth) return 1;
            return 0;
        });

        for(int i=0; i < imageRequest.size();i++){
            ImageRequest ir = imageRequest.get(i);
            System.out.println(ir.zDepth);
            setzDepth(ir.zDepth);
            drawImage(ir.image,ir.offX, ir.offY);
        }



        for(int i =0 ;i < p.length; i++){
            float r = (float) ((lm[i] >> 16) & 0xff) /255;
            float g = (float) ((lm[i] >> 8) & 0xff) /255;
            float b = (float) (lm[i] & 0xff) /255;

            p[i] = ((int)(((p[i]>>16)&0xff)*r) << 16 | (int)(((p[i]>>8)&0xff)*g) <<8 | (int)((p[i]&0xff)*b));
        }

        imageRequest.clear();
        processing = false;
    }

    public void setPixel(int x,int y,int value){
        int alpha = ((value>>24) & 0xff);


        if((x < 0 || x >= pW ||y < 0 || y >= pH) || alpha ==0){//value==0xffff00ff
            return;
        }

        int index = x + y * pW;

        if(zb[index] >  zDepth)
            return;

        zb[index] = zDepth;

        if(alpha == 255) {
            p[index] = value;
        }else{
            int pixelColor = p[index];

            int newRed = ((pixelColor >> 16)& 0xff) - (int) ((((pixelColor >> 16)& 0xff) - ((value >> 16)& 0xff)) * (alpha/255f));
            int newGreen = ((pixelColor >> 8)& 0xff) - (int) ((((pixelColor >> 8)& 0xff) - ((value >> 8)& 0xff)) * (alpha/255f));
            int newBlue =  (pixelColor& 0xff) - (int) (((pixelColor & 0xff) - (value & 0xff)) * (alpha/255f));
            p[index] = (newRed << 16 | newGreen << 8 | newBlue);
        }
    }


    public void drawText(String text,int offX,int offY,int color){
        int offset = 0;

        for(int i = 0; i < text.length(); i++){
            int unicode = text.codePointAt(i);

            for(int y = 0; y < font.getFontImage().getH(); y++){
                for(int x = 0; x < font.getWidths()[unicode]; x++){
                    if(font.getFontImage().getP()[(x + font.getOffsets()[unicode]) + y * font.getFontImage().getW()] == 0xffffffff){
                        setPixel(x + offX + offset, y + offY, color);
                    }
                }
            }
            offset+=font.getWidths()[unicode];
        }
    }

    public void drawImage(Image image, int offX, int offY){
        offX -=camX;
        offY -= camY;
        if(image.isAlpha() && !processing){
            imageRequest.add(new ImageRequest(image,zDepth,offX,offY));
            return;
        }

        //Don't render code
        if(offX< -image.getW()) return;
        if(offY< -image.getH()) return;
        if(offX >= pW) return;
        if(offY >= pH) return;

        int newX = 0;
        int newY = 0;
        int newWidth = image.getW();
        int newHeight = image.getH();

        //Clipping code
        if( offX < 0){newX -=offX;}
        if( offY < 0){newY -=offY;}
        if(newWidth + offX>=pW){newWidth -= newWidth + offX - pW;}
        if(newHeight + offY>=pH){newHeight -= newHeight + offY - pH;}

        for(int y = newY; y < newHeight; y++){
            for(int x = newX;x < newWidth; x++){
                setPixel(x +offX, y + offY, image.getP()[x + y * image.getW()]);
                //setLightBlock(x +offX, y + offY,image.getLightBlock());
            }
        }
    }

    public void drawImageTile(ImageTile image,int offX,int offY,int tileX,int tileY){
        offX -=camX;
        offY -= camY;
        if(image.isAlpha() && !processing){
            imageRequest.add(new ImageRequest(image.getTileImage(tileX,tileY),zDepth,offX,offY));
            return;
        }
        //Don't render code
        if(offX< -image.getTileW()) return;
        if(offY< -image.getTileW()) return;
        if(offX >= pW) return;
        if(offY >= pH) return;

        int newX = 0;
        int newY = 0;
        int newWidth = image.getTileW();
        int newHeight = image.getTileH();

        //Clipping code
        if( offX < 0){newX -=offX;}
        if( offY < 0){newY -=offY;}
        if(newWidth + offX>=pW){newWidth -= newWidth + offX - pW;}
        if(newHeight + offY>=pH){newHeight -= newHeight + offY - pH;}

        for(int y = newY; y < newHeight; y++){
            for(int x = newX;x < newWidth; x++){
                setPixel(x +offX, y + offY, image.getP()[(x + tileX * image.getTileW()) + (y + tileY * image.getTileH()) * image.getW()]);
                //setLightBlock(x +offX, y + offY,image.getLightBlock());
            }
        }
    }

    public void drawRect(int offX,int offY,int width,int height,int color){
        offX -=camX;
        offY -= camY;

        for(int y = 0; y < height; y++){
            setPixel(offX,y + offY,color);
            setPixel(offX+ width,y + offY,color);
        }
        for(int x = 0; x < width; x++){
            setPixel(x +offX,offY,color);
            setPixel(x + offX,offY + height,color);
        }
    }

    public void drawFillRect(int offX,int offY,int width,int height,int color){
        offX -=camX;
        offY -= camY;

        //Don't render code
        if(offX< -width) return;
        if(offY< -height) return;
        if(offX >= pW) return;
        if(offY >= pH) return;

        /*int newX = 0;
        int newY = 0;
        int newWidth = width;
        int newHeight = height;

        //Clipping code
        if( offX < 0){newX -=offX;}
        if( offY < 0){newY -=offY;}
        if(newWidth + offX>=pW){newWidth -= newWidth + offX - pW;}
        if(newHeight + offY>=pH){newHeight -= newHeight + offY - pH;}*/

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                setPixel(x + offX, y + offY,color);
            }
        }
    }






    public int getzDepth() {
        return zDepth;
    }

    public void setzDepth(int zDepth) {
        this.zDepth = zDepth;
    }

    public int getAmbientColor() {
        return ambientColor;
    }

    public void setAmbientColor(int ambientColor) {
        this.ambientColor = ambientColor;
    }

    public int getCamX() {
        return camX;
    }

    public void setCamX(int camX) {
        this.camX = camX;
    }

    public int getCamY() {
        return camY;
    }

    public void setCamY(int camY) {
        this.camY = camY;
    }
}
/*
* pixelColor >> 16: This part of the code shifts the bits of the pixelColor integer to the right by 16 positions. This operation effectively moves the bits representing the red component to the least significant bits of the integer.

& 0xff: This part of the code performs a bitwise AND operation with 0xff, which is 11111111 in binary. This operation masks all but the least significant 8 bits of the integer, effectively extracting only the bits representing the red component.

Here's how it works with an example:

Let's say pixelColor is represented in binary as AAAARRRRGGGGBBBB, where:

A represents the alpha component,
R represents the red component,
G represents the green component, and
B represents the blue component.
After shifting pixelColor to the right by 16 bits, we get 00000000RRRRGGGG.

Then, performing a bitwise AND operation with 0xff results in 000000000000RRRR, where only the bits representing the red component remain.

Finally, the extracted red component is stored in the variable newRed.*/