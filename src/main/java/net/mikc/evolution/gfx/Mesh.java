package net.mikc.evolution.gfx;

import com.jme3.scene.Spatial;

public class Mesh {
    private final Spatial model;

    public Mesh(final Spatial model) {
        this.model = model;
    }

    public Spatial getModel() {
        return model;
    }
}
