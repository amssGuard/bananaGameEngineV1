package game;

import banana.AbstractGame;
import banana.GameContainer;
import banana.Renderer;
import banana.audio.SoundClip;
import banana.gfx.Image;
import banana.gfx.ImageTile;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GameManager extends AbstractGame {
    private Image skyImage = new Image("/nightSkyImage.png");
    private Image lvelImage = new Image("/levelImage.png");
    int flag[]= new int[2];


    public static final int TS = 16;
    private boolean[] collision;
    private int levelW, levelH;
    private ArrayList<GameObject> objects = new ArrayList<>();
    private Camera camera;

    public GameManager(){
        flag[0]=1;
        flag[1]=1;
        skyImage.setAlpha(false);
        lvelImage.setAlpha(false);
        objects.add(new Player(1,3));
        objects.add(new Enemy(2,3));
        loadLevel("/level.png");
        camera = new Camera("player");

    }

    @Override
    public void init(GameContainer gc) {
        //gc.getRenderer().setAmbientColor(-1);
    }

    @Override
    public void update(GameContainer gc, float dt) {
        gc.getRenderer().setAmbientColor(-1);
        if(gc.getInput().isKey(KeyEvent.VK_P)){
            gc.setRunning(false);
        }
        if(gc.getInput().isKey(KeyEvent.VK_R)){
            gc.setRunning(true);
            gc.run();
        }

        for(int i = 0; i < objects.size(); i++){
            objects.get(i).update(gc,this,dt);
//            if(objects.get(i).isDead()){
//                objects.remove(i);
//                i--;
//            }
        }
        camera.update(gc,this,dt);
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        camera.render(r);
        r.setzDepth(0);
        r.drawImage(skyImage,0,0);
        r.setzDepth(1);
        r.drawImage(lvelImage,0,0);
        if(getCollision(0,2)){
            r.drawText("lol",0,13,0xffffffff);
        }else{
            r.drawText(" ",0,13,0xffffffff);
            System.out.println("unlol");
        }
        //Tile based collision graphics..comment this to actually load the images
        for(int y = 0; y < levelH; y++){
            for(int x = 0; x < levelW; x++) {
                if (collision[x + y * levelW]) {
                   r.drawFillRect(x * TS, y * TS, TS, TS, 0xff0f0f0f);
                }else{
                   r.drawFillRect(x * TS, y * TS, TS, TS, 0xfff9f9f9);
                }
            }
        }
        r.setzDepth(2);
        for(GameObject obj : objects){
            obj.render(gc,r);
        }
        //System.out.println(objects.get(0).getPosX()+"<-x, y->" +objects.get(0).getPosY());
        //TODO: make this level/texture loading work better for now this is it
        if(objects.get(0).posX==16.0&&objects.get(0).posY==432.0){
            //System.out.println("meow");
            //loadLevel("/level.png");
            flag[0]=0;
        }
        if(objects.get(0).posX==480.0&&objects.get(0).posY==384.0){
            //System.out.println("meow");
            //loadLevel("/level.png");
            flag[1]=0;
        }
        if(flag[0]==0) {
            r.drawImage(lvelImage, 0, 0);
        }
        if(flag[1]==0){
            r.setzDepth(1);
            r.drawImage(skyImage , 0, 0);
        }
    }

    public void loadLevel(String path){
        Image levelImage = new Image(path);

        levelW = levelImage.getW();
        levelH = levelImage.getH();
        collision = new boolean[levelW * levelH];
        //actual tile based collision
        for(int y = 0; y < levelImage.getH(); y++){
            for(int x = 0; x < levelImage.getW(); x++){
                if(levelImage.getP()[x + y * levelImage.getW()] == 0xff000000){
                    collision[x + y * levelImage.getW()] = true;
                }else{
                    collision[x + y * levelImage.getW()] = false;
                }
            }
        }
    }

    public GameObject getObject(String tag){
        for(int i=0;i<objects.size();i++){
            if(objects.get(i).getTag().equals(tag)){
                return objects.get(i);
            }
        }
        return null;
    }

    public boolean getCollision(int x,int y){
        if(x<0||x>=levelW||y<0||y>=levelH)
            return true;
        return collision[x + y * levelW];
    }

    public int getLevelW() {
        return levelW;
    }

    public int getLevelH() {
        return levelH;
    }

    public static void main(String[] args) throws InterruptedException {
        GameContainer gc = new GameContainer(new GameManager());
        gc.setWidth(320);
        gc.setHeight(240);
        gc.setScale(3f);
        gc.start();
        gc.stop();
    }
}
