package game;

import banana.GameContainer;
import banana.Renderer;
import banana.gfx.ImageTile;

import java.awt.event.KeyEvent;

import static game.GameManager.TS;


public class Player extends GameObject{
    private ImageTile playerImage = new ImageTile("/character.png",16,16);

    private int direction = 0;
    private float anim = 0;
    private int tileX,tileY;
    private float offX,offY;
    private float speed = 100;
    private float fallSpeed = 10;
    private float fallDistance = 0;
    private float jump = -4;
    private boolean ground = false;
    private boolean groundLast = false;
    public Player(int posX,int posY){
        this.tag = "player";
        this.tileX = posX;
        this.tileY = posY;
        this.offX = 0;
        this.offY = 0;
        this.posX = posX * TS;
        this.posY = posY * TS;
        this.width = TS;
        this.height = TS;
    }
    @Override
    public void update(GameContainer gc,GameManager gm, float dt) {
        //Left and Right
        if(gc.getInput().isKey(KeyEvent.VK_D)){

            if(gm.getCollision(tileX+1,tileY)) {
                if(offX > 0) {
                    offX += dt * speed;

                    if(offX < 0){
                        offX = 0;
                    }
                }else {
                    offX = 0;
                }
            }else{
                offX += dt * speed;
            }
        }
        if(gc.getInput().isKey(KeyEvent.VK_A)){
            if(gm.getCollision(tileX-1,tileY)) {
                if(offX < 0) {
                    offX -= dt * speed;
                    if(offX > 0){
                        offX = 0;
                    }
                }else {
                    offX = 0;
                }
            }else{
                offX -= dt * speed;
            }
        }

        //gravity && jump
        fallDistance += dt * fallSpeed;

        //jump
        if(gc.getInput().isKeyDown(KeyEvent.VK_SPACE) && ground){
            fallDistance = jump;
            ground = false;
        }
        //gravity
        offY += fallDistance;

        if(fallDistance < 0){
            if ((gm.getCollision(tileX, tileY - 1) || gm.getCollision(tileX + (int) Math.signum((int) offX), tileY - 1)) && offY < 0) {
                fallDistance = 0;
                offY = 0;
            }
        }

        if(fallDistance > 0) {
            if ((gm.getCollision(tileX, tileY + 1) || gm.getCollision(tileX + (int) Math.signum((int) offX), tileY + 1)) && offY > 0) {
                fallDistance = 0;
                offY = 0;
                ground = true;
            }
        }
        //End of jump and Gravity

        //Final position
        if(offY > TS / 2){
            tileY++;
            offY -= TS;
        }

        if(offY < -TS / 2){
            tileY--;
            offY += TS;
        }

        if(offX > TS / 2){
            tileX++;
            offX -= TS;
        }

        if(offX < -TS / 2){
            tileX--;
            offX += TS;
        }

        posX = tileX *TS+offX;
        posY = tileY *TS+offY;


        if(gc.getInput().isKey(KeyEvent.VK_D)){
            direction = 0;

            anim += dt * 10;
            if(anim >= 4)
                anim = 0;
        } else if (gc.getInput().isKey(KeyEvent.VK_A)) {
            direction = 1;

            anim += dt * 10;
            if(anim >= 4)
                anim = 0;
        }else {
            anim = 0;
        }

        if(fallDistance != 0){
            anim = 1;
        }
        if(ground && !groundLast){
            anim = 2;
        }
        groundLast = ground;
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        //r.drawFillRect((int)posX,(int)posY,width,height,0xff808080);
        r.drawImageTile(playerImage,(int)posX,(int)posY,(int)anim,direction);
    }
}
