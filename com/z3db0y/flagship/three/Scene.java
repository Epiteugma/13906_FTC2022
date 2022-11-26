package com.z3db0y.flagship.three;

import java.util.ArrayList;

public class Scene {
    ArrayList<Cuboid> objects = new ArrayList<>();

    public void add(Cuboid o) {
        objects.add(o);
    }

    public void remove(Cuboid o) {
        objects.remove(o);
    }
}
