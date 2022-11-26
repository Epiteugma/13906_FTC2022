package com.z3db0y.flagship.three;

public class Line {
    public Vector3D start;
    public Vector3D end;

    public Line(Vector3D start, Vector3D end) {
        this.start = start;
        this.end = end;
    }

    public Line() {
        this.start = new Vector3D();
        this.end = new Vector3D();
    }
}
