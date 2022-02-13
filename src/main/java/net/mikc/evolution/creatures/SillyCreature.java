package net.mikc.evolution.creatures;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import net.mikc.evolution.gfx.GfxInternals;
import net.mikc.evolution.neuralnets.NeuralNetwork;
import net.mikc.evolution.utils.MathUtils;

import java.util.UUID;

public class SillyCreature implements ICreature {
    private final NeuralNetwork brain;
    private float x;
    private float y;
    private float age;
    private boolean dead;
    private String uuid;

    public SillyCreature(final NeuralNetwork brain, float x, float y) {
        this.brain = brain;
        this.x = x;
        this.y = y;
        this.dead = false;
        this.uuid = UUID.randomUUID().toString();

    }

    @Override
    public String getId() {
        return uuid;
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
        return 0;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
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

    @Override
    public void doAction(int action) {
        float dx = 0.01f;
        float dy = 0.01f;
        switch (action) {
            case 0:
                x -= dx;
                if (x < -1.0) x = -1.0f;
                break;
            case 1:
                x += dx;
                if (x > 1.0f) x = 1.0f;
                break;
            case 2:
                y -= dy;
                if (y < -1.0f) y = -1.0f;
                break;
            case 3:
                y += dy;
                if (y > 1.0f) y = 1.0f;
                break;
        }
    }

    @Override
    public ICreature mutate(ICreature other) {
        NeuralNetwork newBrain = brain.mutate(other.getBrain());
        return new SillyCreature(newBrain, MathUtils.rnd(), MathUtils.rnd());
    }

    @Override
    public float[] getSensorInputs() {

        return new float[]{x, y, age};
    }

    @Override
    public void draw(GfxInternals gfx, boolean drawBrain) {
        float xx = x*12.0f;
        float yy = 0.0f;
        float zz = y*12.0f;
        Node parent = gfx.getParent();
        Spatial creatureSpatial = parent.getChild(uuid);
        if(creatureSpatial != null) {
            creatureSpatial.setLocalTranslation(xx, yy, zz);
        } else {
            Node creature = new Node();
            creature.setName(uuid);
            GfxInternals creatureGfx = gfx.cloneWithCustomParent(creature);
            creatureGfx.cube(xx, yy, zz, 0.1f, 0, 0, 255, 255);
            if (drawBrain) {
                Node brainParent = new Node();
                GfxInternals brainGfx = gfx.cloneWithCustomParent(brainParent);
                brain.draw(brainGfx);
                creature.attachChild(brainParent);
                brainParent.move(xx, yy + 0.4f, zz);
                brainParent.scale(0.095f);
            }
            parent.attachChild(creature);
        }
    }
}
