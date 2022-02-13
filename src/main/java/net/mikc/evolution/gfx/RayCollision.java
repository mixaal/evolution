package net.mikc.evolution.gfx;

public class RayCollision {
    private final boolean collision;
    private final float distance;
    private final Vec3f point;

    public RayCollision(final boolean haveCollision, final float distance, final Vec3f point) {
        this.collision = haveCollision;
        this.distance = distance;
        this.point = point;
    }

    public boolean isCollision() {
        return collision;
    }

    public float getDistance() {
        return distance;
    }

    public Vec3f getPoint() {
        return point;
    }
}
