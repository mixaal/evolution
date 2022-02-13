package net.mikc.evolution.gfx;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;
import com.jme3.util.TangentBinormalGenerator;

import java.util.ArrayList;
import java.util.List;

public class GfxInternals {
    private final AssetManager assetManager;
    private final Node parent;
    private final List<GfxInternals> linkedGfxInternals;

    public final List<Spatial> registered = new ArrayList<>();

    public GfxInternals(AssetManager assetManager, Node parent) {
        this.assetManager = assetManager;
        this.parent = parent;
        this.linkedGfxInternals = new ArrayList<>();
    }

    public GfxInternals cloneWithCustomParent(Node customParent) {
        GfxInternals childGfx =  new GfxInternals(assetManager, customParent);
        linkedGfxInternals.add(childGfx);
        return childGfx;
    }

    public Node getParent() {
        return parent;
    }

    public void clear() {
        for(Spatial spatial: registered) {
            parent.detachChild(spatial);
        }
        for(GfxInternals child: linkedGfxInternals) {
            child.clear();
        }
    }

    private Material getMaterial(MaterialBuilder material) {
        net.mikc.evolution.gfx.ColorRGBA diffuse = material.getDiffuse();
        net.mikc.evolution.gfx.ColorRGBA specular = material.getSpecular();
        net.mikc.evolution.gfx.ColorRGBA ambient = material.getAmbient();
        float shininess = material.getShininess();
        boolean useLight = material.isUseLight();
        Material m = new Material(assetManager, useLight ? "Common/MatDefs/Light/Lighting.j3md": "Common/MatDefs/Misc/Unshaded.j3md");
        if(material.getDiffuseTexture()!=null) {
            m.setTexture(useLight ? "DiffuseMap" : "ColorMap",
                    assetManager.loadTexture(material.getDiffuseTexture()));
        }
        if(useLight && material.getNormalTexture()!=null) {
            m.setTexture("NormalMap",
                    assetManager.loadTexture(material.getNormalTexture()));
        }
        if(useLight) {
            m.setBoolean("UseMaterialColors", true);
            m.setColor("Ambient", ColorRGBA.fromRGBA255(ambient.r, ambient.g, ambient.b, ambient.a));
            m.setColor("Diffuse", ColorRGBA.fromRGBA255(diffuse.r, diffuse.g, diffuse.b, diffuse.a));
            m.setColor("Specular", ColorRGBA.fromRGBA255(specular.r, specular.g, specular.b, specular.a));
            m.setFloat("Shininess", shininess);
        }
        if(material.isTransparent()) {
            m.setTransparent(true);
            m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        }
        return m;
    }

    public static RayCollision collisionWithRayDistance(Mesh mesh, float x1, float y1, float z1, float x2, float y2, float z2) {
        Spatial spatial = mesh.getModel();
        if( spatial == null) {
            return new RayCollision(false, -1f, null);
        }

        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(new Vector3f(x1, y1, z1), new Vector3f(x2, y2, z2));
        spatial.collideWith(ray, results);
        if(results.size() > 0) {
            Vector3f point = results.getClosestCollision().getContactPoint();
            return new RayCollision(true, results.getClosestCollision().getDistance(), new Vec3f(point.getX(), point.getY(), point.getZ()));
        } else {
            return new RayCollision(false, -1f, null);
        }
    }

    public RayCollision collisionWithRayDistance(String modelId, float x1, float y1, float z1, float x2, float y2, float z2) {
        Spatial spatial = parent.getChild(modelId);
        return collisionWithRayDistance(new Mesh(spatial), x1, y1, z1, x2, y2, z2);
    }

    public Mesh drawModel(String id, String modelName, float x, float y, float z, float scaleFactor, MaterialBuilder material) {
        Spatial model=parent.getChild(id);
        if(model==null) {
            model = assetManager.loadModel(modelName);
            model.setName(id);
            model.setLocalScale(scaleFactor);
            model.setMaterial(getMaterial(material));
//            TangentBinormalGenerator.generate(model);
            parent.attachChild(model);
            registered.add(model);

        }
        model.setLocalTranslation(x, y, z);
        return new Mesh(model);
    }

    public void rotateAroundY(String id, float amount) {
        Quaternion q = new Quaternion();
        q.fromAngleAxis(amount, new Vector3f(0f, 1f, 0f));
        Spatial geom = parent.getChild(id);
        if(geom!=null) {
            geom.setLocalRotation(q);
        }
    }

    public void cylinder(float x, float y, float z, float radius, float height, int r, int g, int b, int a) {
        Cylinder cylinder = new Cylinder(20, 50, radius, height, true);
        Geometry geom = new Geometry("cylinder", cylinder);
        geom.setLocalTranslation(x, y, z);
        Quaternion up = new Quaternion();
        up.fromAngleAxis(FastMath.PI/2, new Vector3f(1f, 0f, 0f));
        geom.setLocalRotation(up);
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.fromRGBA255(r, g, b, a));
        if (a < 255) {
            mat.setTransparent(true);
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
        }
        geom.setMaterial(mat);
        registered.add(geom);
        parent.attachChild(geom);
    }

    public void cube(float x, float y, float z, float xd, float yd, float zd, int r, int g, int b, int a) {
        Box box = new Box(xd, yd, zd);
        Geometry geom = new Geometry("box", box);
        geom.setLocalTranslation(x, y, z);
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.fromRGBA255(r, g, b, a));
        if (a < 255) {
            mat.setTransparent(true);
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
        }
        geom.setMaterial(mat);
        registered.add(geom);
        parent.attachChild(geom);
    }

    public void cube(float x, float y, float z, float dimension, int r, int g, int b, int a) {
        cube(x, y, z, dimension, dimension, dimension, r, g, b, a);
    }

    public void line(float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a) {
        Line line = new Line(new Vector3f(x1, y1, z1), new Vector3f(x2, y2, z2));
        line.setLineWidth(2);
        Geometry geom = new Geometry("line", line);
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.fromRGBA255(r, g, b, a));
        if (a < 255) {
            mat.setTransparent(true);
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
        }
        geom.setMaterial(mat);
        registered.add(geom);
        parent.attachChild(geom);
    }

    public void text(float num, float size, float x, float y, float z) {
        text(String.format("%.2f", num), size, x, y, z);
    }

    public void text(String message, float size, float x, float y, float z) {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText helloText = new BitmapText(guiFont, false);
        helloText.setSize(size);
        helloText.setText(message);
        helloText.setLocalTranslation(x, y, z);
        registered.add(helloText);
        parent.attachChild(helloText);
    }

}
