package net.mikc.evolution.creatures;

import com.jme3.math.FastMath;
import net.mikc.evolution.demos.CarRacerDemo;
import net.mikc.evolution.gfx.*;
import net.mikc.evolution.neuralnets.NeuralNetwork;

import java.util.Random;
import java.util.UUID;

public class Racer implements ICreature {
    private static final float MAX_VELOCITY = 0.2f;
    private final NeuralNetwork brain;
    private float x, y, z;
    private float velocity;
    private float angle;
    private String id;
    private boolean dead;
    private float distanceTravelled;
    private boolean sensorsDrawn = false;
    private float sensors[];
    private float age;
    private static Random rnd = new Random();

    public Racer(NeuralNetwork brain) {
        this.brain = brain;
        this.id = UUID.randomUUID().toString();
        this.x = 0;
        this.z = 0;
        this.y = 0;
        this.y = 0;
        this.velocity = 0.1f;
        this.dead = false;
        this.distanceTravelled = 0;
        this.sensors = new float[9];
//        this.angle = rnd.nextFloat();
        age = 0;
    }

    public Racer setAngle(float angle) {
        this.angle = angle;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public void kill() {
        this.dead = true;
    }

    @Override
    public NeuralNetwork getBrain() {
        return brain;
    }

    public void accelerate() {
        velocity+=0.05f;
        if(velocity>MAX_VELOCITY) velocity=MAX_VELOCITY;
    }

    public void decelerate() {
        velocity-=0.05f;
        if(velocity<0) velocity=0;
    }

    public void steerRight() {
        if (velocity > 0) angle += 0.1f;
    }

    public void steerLeft() {
        if (velocity > 0) angle -= 0.1f;
    }

    @Override
    public void doAction(int action) {
        if (dead) {
            return;
        }
        switch (action) {
            case 0:
                steerRight();
                break;
            case 1:
                steerLeft();
                break;
            case 2:
                accelerate();
                break;
            case 3:
                decelerate();
                break;
        }
        float vx = velocity * (float) Math.cos(angle);
        float vz = velocity * (float) Math.sin(angle);
        x += vx;
        z -= vz;
        distanceTravelled += Math.sqrt(vx * vx + vz * vz);
//        distanceTravelled = (float)Math.sqrt(x*x + z*z);
    }

    @Override
    public ICreature mutate(ICreature other) {
        NeuralNetwork newBrain = brain.mutate(other.getBrain());
        return new Racer(newBrain);
    }

    @Override
    public float[] getSensorInputs() {
        int j=0;
        for (float i = angle - FastMath.PI / 4; i < angle + FastMath.PI / 4; i += FastMath.PI / 16) {
            float tx = x + 10 * (float) Math.cos(i);
            float tz = z - 10 * (float) Math.sin(i);
            RayCollision collision = GfxInternals.collisionWithRayDistance(CarRacerDemo.mesh, x, y+0.1f, z,tx-x, 0f, tz - z );
            float dist = collision.getDistance();
            if(dist>0 && dist<.55f) {
                kill();
            }
            sensors[j++] = collision.isCollision() ? dist : 100.0f;
        }
       return sensors;
    }

    @Override
    public void setAge(float age) {
        if(!dead) {
            this.age = age;
        }
    }

    @Override
    public float getAge() {
        return age;
    }

    @Override
    public float getFitness() {
        return distanceTravelled;
    }

    public float getDistanceTravelled() {
        return distanceTravelled;
    }

    @Override
    public void draw(GfxInternals gfx, boolean drawBrain) {
        MaterialBuilder porscheMat = new MaterialBuilder()
                .transparent(true)
                .useLight(true)
                .shininess(128f)
                .specular(new ColorRGBA(128, 128, 128, 255))
                .ambient(ColorRGBA.fromFloats(0.577273f, 0.577273f, 0.577273f, 1.0f))
                .diffuseTexture("assets/Models/911/_PORSCHE.png");
        gfx.drawModel(id, "assets/Models/911/911.obj", x, y, z, 0.1f, porscheMat);
        if(drawBrain) {
            drawSensors(gfx);
        }

        gfx.rotateAroundY(id, angle);
    }

    private void drawSensors(GfxInternals gfx) {
        for (float i = angle - FastMath.PI / 4; i < angle + FastMath.PI / 4; i += FastMath.PI / 16) {
            float tx = x + 10 * (float) Math.cos(i);
            float tz = z - 10 * (float) Math.sin(i);
            RayCollision collision = GfxInternals.collisionWithRayDistance(CarRacerDemo.mesh, x, y+0.1f, z, tx-x, 0.0f, tz-z );
            Vec3f collisionPoint = collision.getPoint();
            float dist = collision.getDistance();
//            System.out.println("dist="+dist);
            if(dist<0) dist = 100.0f;
            if(dist>0 && dist<0.55f) {
//                System.out.println("KILL");
                gfx.line(x, y+0.1f, z,tx, y+0.1f, tz, 255, 0, 0, 255);
            }
            else {
                gfx.line(x, y+0.1f, z,tx, y+0.1f, tz, 0, 255, 0, 255);
            }
            gfx.text(dist, 1, tx, y+1f, tz);
            if(collision.isCollision()) {
                gfx.cube(collisionPoint.getX(), collisionPoint.getY(), collisionPoint.getZ(), 0.2f, 0, 0, 255, 255);
            }
//            System.out.println("j="+j);
//            sensors[j++] = collision.isCollision() ? getDistanceForSensors(dist) : 1000.0f;
        }
    }
}
