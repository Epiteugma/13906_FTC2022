package com.z3db0y.flagship.three;

import java.util.ArrayList;

public class Path {
    public ArrayList<Vector3D> points = new ArrayList<>();

    public void addPoint(Vector3D point) {
        points.add(point);
    }

    public void removePoint(Vector3D point) {
        points.remove(point);
    }
}
