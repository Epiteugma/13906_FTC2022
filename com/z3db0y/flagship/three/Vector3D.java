package com.z3db0y.flagship.three;

public class Vector3D {
    public double x = 0;
    public double y = 0;
    public double z = 0;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public String toString() {
        return "Vector3D{" + this.x + ", " + this.y + ", " + this.z + "}";
    }

    public boolean equals(Vector3D other) {
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }
}
