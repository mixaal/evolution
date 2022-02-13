package net.mikc.evolution.gfx;

public class MaterialBuilder {
    private boolean useLight = false;
    private boolean transparent = false;
    private ColorRGBA diffuse = ColorRGBA.WHITE;
    private ColorRGBA specular = ColorRGBA.WHITE;
    private ColorRGBA ambient = new ColorRGBA(20, 20, 20, 255);
    private float shininess = 64f;
    private String diffuseTexture;
    private String normalTexture;

    public MaterialBuilder transparent(boolean transparent) {
        this.transparent = transparent;
        return this;
    }

    public MaterialBuilder diffuseTexture(String diffuseTexture) {
        this.diffuseTexture = diffuseTexture;
        return this;
    }

    public MaterialBuilder normalTexture(String normalTexture) {
        this.normalTexture = normalTexture;
        return this;
    }

    public MaterialBuilder useLight(boolean useLight) {
        this.useLight = useLight;
        return this;
    }

    public MaterialBuilder ambient(ColorRGBA ambient) {
        this.ambient = ambient;
        return this;
    }

    public MaterialBuilder diffuse(ColorRGBA diffuse) {
        this.diffuse = diffuse;
        return this;
    }

    public MaterialBuilder specular(ColorRGBA specular) {
        this.specular = specular;
        return this;
    }

    public MaterialBuilder shininess(float shininess) {
        this.shininess = shininess;
        return this;
    }

    public boolean isUseLight() {
        return useLight;
    }

    public ColorRGBA getDiffuse() {
        return diffuse;
    }

    public ColorRGBA getSpecular() {
        return specular;
    }

    public ColorRGBA getAmbient() {
        return ambient;
    }

    public float getShininess() {
        return shininess;
    }

    public String getDiffuseTexture() {
        return diffuseTexture;
    }

    public String getNormalTexture() {
        return normalTexture;
    }

    public boolean isTransparent() {
        return transparent;
    }
}
