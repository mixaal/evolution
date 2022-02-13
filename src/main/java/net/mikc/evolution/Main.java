package net.mikc.evolution;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.util.SkyFactory;
import net.mikc.evolution.creatures.ICreature;
import net.mikc.evolution.demos.CarRacerDemo;
import net.mikc.evolution.demos.IDemo;
import net.mikc.evolution.gfx.GfxInternals;


public class Main extends SimpleApplication {
    private static final int POPULATION_SZ = 2_000;
    private static final int VIEW_POPULATION_SZ = 1000;
    private static final int GENERATIONS = 10;
    private GfxInternals gfx;
    private final IDemo demo;
    private int fps = 0;

    public Main() {
        //this.demo = new SillyCreaturesDemo(100000, SillyCreaturesDemo.SelectionCriteria.KILL_OUT_OF_CIRCLE_AROUND_CENTER);
        this.demo = new CarRacerDemo(POPULATION_SZ);
    }
    public static void main(String []args) {
        new Main().start();
    }

    @Override
    public void simpleInitApp() {
        getRootNode().attachChild(SkyFactory.createSky(getAssetManager(), "assets/Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap));
        flyCam.setMoveSpeed(10);
        gfx = new GfxInternals(getAssetManager(), rootNode);
        demo.drawPlayground(gfx);
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,0,-2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);


        long start = System.currentTimeMillis();
        for(int i=0; i<GENERATIONS; i++) {
            demo.generation();
            System.out.println("Generation #"+i);
        }
        long end = System.currentTimeMillis();
        long ms = end - start;
        System.out.println("Total time: "+ms/1000);

//        new Racer(null).setAngle(-1.1f).draw(gfx, false);
    }


    @Override
    public void update() {
        super.update();

        if(fps==0) demo.selectNewPopulation(VIEW_POPULATION_SZ);
        if(fps>3000) {
            gfx.clear();
            demo.drawPlayground(gfx);
            fps=0;
            demo.selectNewPopulation(VIEW_POPULATION_SZ);
        }
        fps++;

        demo.oneStep(0.5f);
        int i = 0;
        for(ICreature creature: demo.getPopulation()) {
            i++;
            creature.draw(gfx, (i%100)==0);
        }
//        System.out.println("i="+i);

    }
}
