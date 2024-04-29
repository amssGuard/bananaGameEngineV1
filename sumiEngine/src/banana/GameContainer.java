package banana;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class GameContainer implements Runnable{
    private Thread thread;
    private Window window;
    private Renderer renderer;
    private Input input;
    private AbstractGame game;

    private boolean running = false;
    private final double UPDATE_CAP=1.0/70.0;
    private int width = 320,height = 240;
    private float scale = 3f;
    private String title = "BananaEngine v1.0";
    public GameContainer(AbstractGame game){
        this.game = game;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void start(){
        window = new Window(this);
        renderer = new Renderer(this);
        input = new Input(this);

        thread = new Thread(this);
        thread.run();
    }
    public void stop() throws InterruptedException {
        thread.join();
    }
    public void run(){
        running = true;
        boolean render = false;
        double firstTime = 0;
        double lastTime = System.nanoTime() / 1000000000.0;
        double passedTime = 0;
        double unprocessedTime = 0;
        //System.out.println(lastTime);
        double frameTime = 0;
        int frames = 0;
        int fps = 0;

        //game.init(this);

        while(running){
            render = false;
            firstTime = System.nanoTime() / 1000000000.0;
            //System.out.println(firstTime);
            passedTime = firstTime - lastTime;
            lastTime = firstTime;

            unprocessedTime += passedTime;
            frameTime += passedTime;

            while (unprocessedTime >= UPDATE_CAP){
                unprocessedTime -= UPDATE_CAP;
                render = true;

                game.update(this,(float)UPDATE_CAP);

                input.update();

                if(frameTime >= 1.0){
                    frameTime = 0;
                    fps = frames;
                    frames = 0;
                }
            }
            if(render){
                renderer.clear();
                game.render(this,renderer);
                renderer.process();
                renderer.setCamX(0);
                renderer.setCamY(0);
                //renderer.drawText("Frames : "+frames,50,0,0xffffffff);
               // renderer.drawText("Unprocessed : " + unprocessedTime+" Cap :"+UPDATE_CAP, 0, 20, 0xff00ffff);
                //renderer.drawText("Frames time : "+frameTime,50,10,0xffffffff);
                renderer.setzDepth(3);
                renderer.drawText("Fps : " + fps, 0, 0, 0xff00ffff);
                window.update();
                frames++;
            }else{
                try {
                    Thread.sleep(1);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        dispose();
    }
    private void dispose(){
        //TODO:Adding a disposing code
        System.out.println("lol");
         running = false;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Window getWindow() {
        return window;
    }

    public Input getInput() {
        return input;
    }

    public Renderer getRenderer() {
        return renderer;
    }
}
