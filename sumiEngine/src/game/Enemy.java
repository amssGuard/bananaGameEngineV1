package game;

import banana.GameContainer;
import banana.Renderer;

import java.awt.event.KeyEvent;

import static game.GameManager.TS;

public class Enemy extends GameObject{
    int bblue;
    public Enemy(int posX,int posY){
        this.tag = "enemy";
        this.posX = posX * TS;
        this.posY = posY * TS;
        bblue = posX;
        //this.tileX
        this.width = TS;
        this.height = TS;
    }
    @Override
    public void update(GameContainer gc, GameManager gm, float dt) {
        //TODO: Adding enemy movement and damage thingy thing to our player

            if(gc.getInput().isKey(KeyEvent.VK_RIGHT))
                posX += dt * 100;
            if(gc.getInput().isKey(KeyEvent.VK_LEFT))
                posX -= dt * 100;
        //System.out.println(posX);
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawFillRect((int)posX,(int)posY,height,width,0xff00ff00);
    }
}
