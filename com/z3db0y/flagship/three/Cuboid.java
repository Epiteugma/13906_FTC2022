package com.z3db0y.flagship.three;

public class Cuboid extends Object {
    public double width = 0;
    public double height = 0;
    public double depth = 0;
    public Vector3D position = new Vector3D();

    public Vector3D[] getCorners() {
        Vector3D[] corners = new Vector3D[8];
        corners[0] = new Vector3D(position.x - width/2, position.y - height/2, position.z - depth/2);
        corners[1] = new Vector3D(position.x + width/2, position.y - height/2, position.z - depth/2);
        corners[2] = new Vector3D(position.x - width/2, position.y + height/2, position.z - depth/2);
        corners[3] = new Vector3D(position.x + width/2, position.y + height/2, position.z - depth/2);
        corners[4] = new Vector3D(position.x - width/2, position.y - height/2, position.z + depth/2);
        corners[5] = new Vector3D(position.x + width/2, position.y - height/2, position.z + depth/2);
        corners[6] = new Vector3D(position.x - width/2, position.y + height/2, position.z + depth/2);
        corners[7] = new Vector3D(position.x + width/2, position.y + height/2, position.z + depth/2);
        return corners;
    }

    public Cuboid() {
        this.width = 0;
        this.height = 0;
        this.depth = 0;
        this.position = new Vector3D();
    }

    public Cuboid(double width, double height, double depth, Vector3D position) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.position = position;
    }

    public Cuboid(Cuboid other) {
        this.width = other.width;
        this.height = other.height;
        this.depth = other.depth;
        this.position = new Vector3D(other.position.x, other.position.y, other.position.z);
    }

    public String toString() {
        return "Cuboid{" + this.width + ", " + this.height + ", " + this.depth + ", " + this.position.toString() + "}";
    }

    public Vector3D getCenter() {
        return new Vector3D(this.position.x, this.position.y, this.position.z);
    }
}
