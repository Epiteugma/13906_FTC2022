package com.z3db0y.flagship.three;

import java.util.Arrays;

public class Pathfinder_backup {

    public static class DestinationUnreachableException extends Exception {
        public DestinationUnreachableException(Vector3D dest, String reason) {
            super("Destination {" + dest.x + ", " + dest.y + ", " + dest.z + "} is unreachable. Reason: " + reason);
        }
    }

    static boolean checkCollision(Cuboid a, Cuboid b, int padding) {
        Vector3D[] cornersA = a.getCorners(padding/2);
        Vector3D[] cornersB = b.getCorners(padding/2);
        boolean collision = false;

        for (int i = 0; i < cornersA.length; i++) {
            if (cornersA[i].x >= cornersB[0].x && cornersA[i].x <= cornersB[1].x) {
                if (cornersA[i].y >= cornersB[0].y && cornersA[i].y <= cornersB[2].y) {
                    if (cornersA[i].z >= cornersB[0].z && cornersA[i].z <= cornersB[4].z) {
                        collision = true;
                        break;
                    }
                }
            }
        }

        return collision;
    }

    static Vector3D[] getNeighbours(Cuboid obj, Vector3D dest) {
        double minX = Math.min(dest.x, obj.position.x - 1);
        double midX = Math.min(dest.x, obj.position.x);
        double maxX = Math.min(dest.x, obj.position.x + 1);
        double minY = Math.min(dest.y, obj.position.y - 1);
        double midY = Math.min(dest.y, obj.position.y);
        double maxY = Math.min(dest.y, obj.position.y + 1);
        double z = obj.position.z;
        Vector3D[] neighbours = new Vector3D[]{
                new Vector3D(minX, minY, z),
                new Vector3D(midX, minY, z),
                new Vector3D(maxX, minY, z),
                new Vector3D(minX, midY, z),
                new Vector3D(maxX, midY, z),
                new Vector3D(minX, maxY, z),
                new Vector3D(midX, maxY, z),
                new Vector3D(maxX, maxY, z)
        };

        return neighbours;
    }

    static double distanceTo (Vector3D a, Vector3D b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) + Math.pow(a.z - b.z, 2));
    }

    public static Path findPath(Scene scene, Cuboid object, Vector3D dest, int padding) throws DestinationUnreachableException {
        // A* pathfinding
        if(object.position.z != dest.z) {
            throw new DestinationUnreachableException(dest, "Destination is not on the same level as the object.");
        }

        object = new Cuboid(object);
        Path path = new Path();
        path.addPoint(object.position);

        Vector3D lastPoint = path.points.get(path.points.size() - 1);
        while(!lastPoint.equals(dest)) {
            lastPoint = path.points.get(path.points.size()-1);
            if(lastPoint.equals(dest)) break;

            object.position = lastPoint;
            Vector3D[] neighbours = getNeighbours(object, dest);
            double[] neighbourF = new double[]{1,1,1,1,1,1,1,1};
            for(int i = 0; i < 8; i++) {
                for(Cuboid obj : scene.objects) {
                    Cuboid temp = new Cuboid(obj);
                    temp.position = neighbours[i];
                    if(checkCollision(temp, obj, padding)) {
                        neighbourF[i] = 0.0;
                    }
                }
                if(neighbourF[i] != 0.0) {
                    neighbourF[i] = 1.0 - (distanceTo(neighbours[i], dest) / distanceTo(object.position, dest));
                }
            }
            int maxFIndex = 0;
            for(int i = 0; i < 8; i++) {
                //! Temp code start
                System.out.println("{\"type\":\"plan\",\"x\":" + neighbours[i].x + ",\"y\":" + neighbours[i].y + ",\"z\":" + neighbours[i].z + "}");
                // try {
                //     Thread.sleep(1);
                // } catch (InterruptedException e) {
                //     e.printStackTrace();
                // }
                //! Temp code end
                if(neighbourF[i] > neighbourF[maxFIndex]) {
                    maxFIndex = i;
                }
            }
            System.out.println(Arrays.toString(neighbourF));

            if(neighbourF[maxFIndex] == 0.0) {
                //! Temp code start
                System.out.println("{\"type\":\"done\",\"x\":" + neighbours[maxFIndex].x + ",\"y\":" + neighbours[maxFIndex].y + ",\"z\":" + neighbours[maxFIndex].z + "}");
                //! Temp code end
                throw new DestinationUnreachableException(dest, "No F value was greater than 0.0");
            }
            else {
                //! Temp code start
                System.out.println("{\"type\":\"move\",\"x\":" + neighbours[maxFIndex].x + ",\"y\":" + neighbours[maxFIndex].y + ",\"z\":" + neighbours[maxFIndex].z + "}");
                //! Temp code end
                path.addPoint(neighbours[maxFIndex]);
            }
        }
        return path;
    }
}
