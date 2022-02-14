package net.mikc.evolution;

import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.util.SkyFactory;
import net.mikc.evolution.demos.CarRacerDemo;
import net.mikc.evolution.demos.IDemo;
import net.mikc.evolution.gfx.GfxInternals;


public class Main extends SimpleApplication {
    private static final int POPULATION_SZ = 2_000;
    private static final int VIEW_POPULATION_SZ = 1000;
    private static final int GENERATIONS = 50;
    private GfxInternals gfx;
    private final IDemo demo;
    private int fps = 0;
    private boolean firstTime = true;

    public Main() {
        //this.demo = new SillyCreaturesDemo(100000, SillyCreaturesDemo.SelectionCriteria.KILL_OUT_OF_CIRCLE_AROUND_CENTER);
        this.demo = new CarRacerDemo(GENERATIONS, POPULATION_SZ, VIEW_POPULATION_SZ, true);
    }
    public static void main(String []args) {
        new Main().start();
    }

    final private ActionListener actionListener = new ActionListener(){
        @Override
        public void onAction(String name, boolean pressed, float tpf){
            System.out.println(name + " = " + pressed);
        }
    };

    @Override
    public void simpleInitApp() {
        getRootNode().attachChild(SkyFactory.createSky(getAssetManager(), "assets/Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap));
        flyCam.setMoveSpeed(10);
        gfx = new GfxInternals(getAssetManager(), inputManager, rootNode);
        demo.drawPlayground(gfx);
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,0,-2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
        demo.initialize(gfx);
    }


    @Override
    public void update() {
        super.update();
        boolean reset = false;
        fps++;
        if(fps>3000) {
            reset = true;
            fps = 0;
        }
        demo.update(gfx, firstTime, reset);
        firstTime = false;
    }
}
