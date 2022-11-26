package com.z3db0y.flagship.three;

import java.util.*;

public class Pathfinder {
    
    public static class DestinationUnreachableException extends Exception {
        public DestinationUnreachableException(Vector3D destination, String reason) {
            super("Destination " + destination.toString() + " is unreachable: " + reason);
        }
    }

    static double distanceTo(Vector3D a, Vector3D b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    static boolean checkCollision(Cuboid a, Cuboid b, double padding) {
        Vector3D[] cornersA = a.getCorners();
        Vector3D[] cornersB = b.getCorners();
        
        return (
            cornersA[0].x - padding/2 <= cornersB[1].x + padding/2 && cornersA[1].x + padding/2 >= cornersB[0].x - padding/2 &&
            cornersA[0].y - padding/2 <= cornersB[2].y + padding/2 && cornersA[2].y + padding/2 >= cornersB[0].y - padding/2 &&
            cornersA[0].z - padding/2 <= cornersB[4].z + padding/2 && cornersA[4].z + padding/2 >= cornersB[0].z - padding/2
        );
    }

    static class Node {
        Vector3D position;
        Node parent;
        double g;
        double h;

        public Node(Vector3D position, Node parent, double g, double h) {
            this.position = position;
            this.parent = parent;
            this.g = g;
            this.h = h;
        }

        public boolean walkable(Scene scene, Cuboid object, double padding) {
            Cuboid c = new Cuboid(object);
            c.position = this.position;
            for (Cuboid c2 : scene.objects) {
                if (Pathfinder.checkCollision(c, c2, padding)) {
                    return false;
                }
            }
            return true;
        }

        public double f() {
            return g + h;
        }
    }

    static Node[] getNeighbours(Scene scene, Cuboid object, Node current, Vector3D target, double padding) {
        Node[] neighbours = new Node[8];
        double x = current.position.x;
        double y = current.position.y;
        double z = current.position.z;
        neighbours[0] = new Node(new Vector3D(x - 1, y - 1, z), current, 0, distanceTo(new Vector3D(x - 1, y - 1, z), target));
        neighbours[1] = new Node(new Vector3D(x, y - 1, z), current, 0, distanceTo(new Vector3D(x, y - 1, z), target));
        neighbours[2] = new Node(new Vector3D(x + 1, y - 1, z), current, 0, distanceTo(new Vector3D(x + 1, y - 1, z), target));
        neighbours[3] = new Node(new Vector3D(x - 1, y, z), current, 0, distanceTo(new Vector3D(x - 1, y, z), target));
        neighbours[4] = new Node(new Vector3D(x + 1, y, z), current, 0, distanceTo(new Vector3D(x + 1, y, z), target));
        neighbours[5] = new Node(new Vector3D(x - 1, y + 1, z), current, 0, distanceTo(new Vector3D(x - 1, y + 1, z), target));
        neighbours[6] = new Node(new Vector3D(x, y + 1, z), current, 0, distanceTo(new Vector3D(x, y + 1, z), target));
        neighbours[7] = new Node(new Vector3D(x + 1, y + 1, z), current, 0, distanceTo(new Vector3D(x + 1, y + 1, z), target));
        return neighbours;
    }

    public static Path findPath(Scene scene, Cuboid object, Vector3D target, double padding) throws DestinationUnreachableException {
        // A* pathfinding
        System.out.println(scene.objects);

        ArrayList<Node> open = new ArrayList<>();
        ArrayList<Node> closed = new ArrayList<>();

        open.add(new Node(object.position, null, 0, distanceTo(object.position, target)));
        Node current = null;
        while(true) {
            for(Node node : open) {
                if(current == null || node.f() < current.f() || (node.f() == current.f() && node.h < current.h)) {
                    current = node;
                }
            }

            if(current.position.equals(target)) {
                break;
            }

            open.remove(current);
            closed.add(current);

            System.out.println("Current: " + current.position.toString());

            for(Node neighbour : getNeighbours(scene, object, current, target, padding)) {
                if(!neighbour.walkable(scene, object, padding) || closed.contains(neighbour)) {
                    if(!neighbour.walkable(scene, object, padding)) {
                        System.out.println("{\"type\":\"collision\",\"position\":{\"x\":" + neighbour.position.x + ",\"y\":" + neighbour.position.y + ",\"z\":" + neighbour.position.z + "}}");
                    }
                    continue;
                }

                for(Node node : open) {
                    if(node.position.equals(neighbour.position)) neighbour = node;
                }

                System.out.println("{\"type\":\"plan\",\"x\":" + neighbour.position.x + ",\"y\":" + neighbour.position.y + ",\"z\":" + neighbour.position.z + "}");
                
                // try {
                //     Thread.sleep(50);
                // } catch (InterruptedException e) {
                //     e.printStackTrace();
                // }

                double newF = current.g + distanceTo(current.position, neighbour.position) + neighbour.h; 
                if(!open.contains(neighbour) || newF < neighbour.f()) {
                    neighbour.g = current.g + distanceTo(current.position, neighbour.position);
                    neighbour.parent = current;

                    if(!open.contains(neighbour)) System.out.println("{\"type\":\"move\",\"x\":" + neighbour.position.x + ",\"y\":" + neighbour.position.y + ",\"z\":" + neighbour.position.z + "}");
                    if(!open.contains(neighbour)) open.add(neighbour);
                }
            }

            if(open.size() == 0) {
                System.out.println("{\"type\":\"done\",\"x\":" + current.position.x + ",\"y\":" + current.position.y + ",\"z\":" + current.position.z + "}");
                throw new DestinationUnreachableException(target, "No path found");
            }
        }

        Path path = new Path();
        while(current != null) {
            path.addPoint(current.position);
            current = current.parent;
        }
        return path;
    }
}
