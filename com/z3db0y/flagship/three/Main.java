package com.z3db0y.flagship.three;

import java.util.*;

import com.z3db0y.flagship.three.Pathfinder.DestinationUnreachableException;

class Main {

    public String toJSON(Object o) {
        if(o instanceof List || o instanceof ArrayList) {
            String json = "[";
            for(Object obj : (List) o) {
                json += toJSON(obj) + ",";
            }
            if(json.endsWith(",")) json = json.substring(0, json.length() - 1);
            json += "]";
            return json;
        } else if(o instanceof Map || o instanceof HashMap) {
            String json = "{";
            for(Object key : ((Map) o).keySet()) {
                Object value = ((Map) o).get(key);
                json += toJSON(key) + ":" + toJSON(value) + ",";
            }
            if(json.endsWith(",")) json = json.substring(0, json.length() - 1);
            json += "}";
            return json;
        } else if(o instanceof String) {
            return "\"" + o + "\"";
        } else if(o instanceof Integer || o instanceof Double || o instanceof Float) {
            return String.valueOf(o);
        } else if(o instanceof Boolean) {
            return o.toString();
        } else if(o == null) {
            return "null";
        } else {
            try {
                return o.toString();
            } catch(Exception e) {
                return "null";
            }
        }
    }

    public ArrayList<String> lexJSON(String json) {
        ArrayList<String> tokens = new ArrayList<>();
        for(int i = 0; i < json.length(); i++) {
            if(json.charAt(i) == ',' || json.charAt(i) == ':' || json.charAt(i) == '{' || json.charAt(i) == '}' || json.charAt(i) == '[' || json.charAt(i) == ']') {
                tokens.add(json.charAt(i) + "");
            } else if(json.charAt(i) == '"') {
                tokens.add(json.substring(i, json.indexOf('"', i + 1) + 1));
                i = json.indexOf('"', i + 1) + 1;
            } else if(json.length() >= i+4 && json.substring(i, i+4).equals("true")) {
                tokens.add("true");
                i += 3;
            } else if(json.length() >= i+5 && json.substring(i, i+5).equals("false")) {
                tokens.add("false");
                i += 4;
            } else if(json.length() >= i+4 && json.substring(i, i+4).equals("null")) {
                tokens.add("null");
                i += 3;
            } else if(json.charAt(i) == '-' || (json.charAt(i) >= '0' && json.charAt(i) <= '9')) {
                int j = i;
                while(j < json.length() && (json.charAt(j) == '-' || (json.charAt(j) >= '0' && json.charAt(j) <= '9') || json.charAt(j) == '.')) {
                    j++;
                }
                tokens.add(json.substring(i, j));
                i = j - 1;
            } else {
                if(json.charAt(i) != ' ') {
                    throw new RuntimeException("Error: Invalid character '" + json.charAt(i) + "' at index " + i);
                }
            }
        }
        return tokens;
    }

    public Object parseLexed(ArrayList<String> lexed) {
        if(lexed.get(0).charAt(0) == '"') {
            return lexed.get(0).substring(1, lexed.get(0).length() - 1);
        } else if(lexed.get(0).equals("true")) {
            return true;
        } else if(lexed.get(0).equals("false")) {
            return false;
        } else if(lexed.get(0).equals("null")) {
            return null;
        } else if(lexed.get(0).charAt(0) == '-' || (lexed.get(0).charAt(0) >= '0' && lexed.get(0).charAt(0) <= '9')) {
            if(lexed.get(0).contains(".")) {
                return Double.parseDouble(lexed.get(0));
            } else {
                return Integer.parseInt(lexed.get(0));
            }
        } else if(lexed.get(0).equals("[")) {
            ArrayList<Object> list = new ArrayList<>();
            lexed.remove(0);
            while(!lexed.get(0).equals("]")) {
                list.add(parseLexed(lexed));
                if(lexed.get(0).equals(",")) {
                    lexed.remove(0);
                }
            }
            lexed.remove(0);
            return list;
        } else if(lexed.get(0).equals("{")) {
            HashMap<String, Object> map = new HashMap<>();
            lexed.remove(0);
            while(!lexed.get(0).equals("}")) {
                String key = (String) parseLexed(lexed);
                lexed.remove(0);
                map.put(key, parseLexed(lexed));
                lexed.remove(0);
                if(lexed.get(0).equals(",")) {
                    lexed.remove(0);
                }
            }
            lexed.remove(0);
            return map;
        } else {
            throw new RuntimeException("Error: Invalid token '" + lexed.get(0) + "'");
        }
    }

    public Object parseJSON(String json) {
        return parseLexed(lexJSON(json));
    }

    public static void main(String[] args) {
        Main instance = new Main();
        String json = args[0];
        Object parsed = instance.parseJSON(json);
        if(!(parsed instanceof Map)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "invalid json");
            error.put("reason", "json must be an object");
            System.out.println(instance.toJSON(error));
            return;
        }
        Map<Object, Object> map = (Map<Object, Object>) parsed;
        if(!map.containsKey("obstacles") || !map.containsKey("robot") || !map.containsKey("target") || !map.containsKey("padding") || !(map.get("obstacles") instanceof List) || !(map.get("robot") instanceof Map) || !(map.get("target") instanceof Map) || !(map.get("padding") instanceof Integer)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "invalid json");
            error.put("reason", "top level key missing or invalid");
            System.out.println(instance.toJSON(error));
            return;
        }
        List<Object> obstacles = (List<Object>) map.get("obstacles");
        Map<Object, Object> robot = (Map<Object, Object>) map.get("robot");
        Map<Object, Object> target = (Map<Object, Object>) map.get("target");
        int padding = (int) map.get("padding");
        if(!target.containsKey("x") || !target.containsKey("y") || !target.containsKey("z") || (!(target.get("x") instanceof Double) && !(target.get("x") instanceof Integer)) || (!(target.get("y") instanceof Double) && !(target.get("y") instanceof Integer)) || (!(target.get("z") instanceof Double) && !(target.get("z") instanceof Integer))) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "invalid json");
            error.put("reason", "target key missing or invalid");
            System.out.println(instance.toJSON(error));
            return;
        }
        if(!robot.containsKey("x") || !robot.containsKey("y") || !robot.containsKey("z") || !robot.containsKey("w") || !robot.containsKey("h") || !robot.containsKey("d") || (!(robot.get("w") instanceof Double) && !(robot.get("w") instanceof Integer)) || (!(robot.get("h") instanceof Double) && !(robot.get("h") instanceof Integer)) || (!(robot.get("d") instanceof Double) && !(robot.get("d") instanceof Integer)) || (!(robot.get("x") instanceof Double) && !(robot.get("x") instanceof Integer)) || (!(robot.get("y") instanceof Double) && !(robot.get("y") instanceof Integer)) || (!(robot.get("z") instanceof Double) && !(robot.get("z") instanceof Integer))) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "invalid json");
            error.put("reason", "robot key missing or invalid");
            System.out.println(instance.toJSON(error));
            return;
        }

        Cuboid robotC = new Cuboid();
        robotC.position.x = (robot.get("x") instanceof Integer) ? Double.valueOf((Integer) robot.get("x")) : ((double) robot.get("x"));
        robotC.position.y = (robot.get("y") instanceof Integer) ? Double.valueOf((Integer) robot.get("y")) : ((double) robot.get("y"));
        robotC.position.z = (robot.get("z") instanceof Integer) ? Double.valueOf((Integer) robot.get("z")) : ((double) robot.get("z"));
        robotC.width = (robot.get("w") instanceof Integer) ? Double.valueOf((Integer) robot.get("w")) : ((double) robot.get("w"));
        robotC.height = (robot.get("h") instanceof Integer) ? Double.valueOf((Integer) robot.get("h")) : ((double) robot.get("h"));
        robotC.depth = (robot.get("d") instanceof Integer) ? Double.valueOf((Integer) robot.get("d")) : ((double) robot.get("d"));

        Scene world = new Scene();
        for(Object obstacle : obstacles) {
            if(!(obstacle instanceof Map)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "invalid json");
                error.put("reason", "obstacle must be an object");
                System.out.println(instance.toJSON(error));
                return;
            }
            Map<Object, Object> obstacleMap = (Map<Object, Object>) obstacle;
            if(!obstacleMap.containsKey("x") || !obstacleMap.containsKey("y") || !obstacleMap.containsKey("z") || !obstacleMap.containsKey("w") || !obstacleMap.containsKey("h") || !obstacleMap.containsKey("d") || (!(obstacleMap.get("w") instanceof Double) && !(obstacleMap.get("w") instanceof Integer)) || (!(obstacleMap.get("h") instanceof Double) && !(obstacleMap.get("h") instanceof Integer)) || (!(obstacleMap.get("d") instanceof Double) && !(obstacleMap.get("d") instanceof Integer)) || (!(obstacleMap.get("x") instanceof Double) && !(obstacleMap.get("x") instanceof Integer)) || (!(obstacleMap.get("y") instanceof Double) && !(obstacleMap.get("y") instanceof Integer)) || (!(obstacleMap.get("z") instanceof Double) && !(obstacleMap.get("z") instanceof Integer))) {
                System.out.println(obstacleMap);
                Map<String, String> error = new HashMap<>();
                error.put("error", "invalid json");
                error.put("reason", "obstacle key missing or invalid");
                System.out.println(instance.toJSON(error));
                return;
            }
            Cuboid obstacleC = new Cuboid();
            obstacleC.position.x = (obstacleMap.get("x") instanceof Integer) ? Double.valueOf((Integer) obstacleMap.get("x")) : ((double) obstacleMap.get("x"));
            obstacleC.position.y = (obstacleMap.get("y") instanceof Integer) ? Double.valueOf((Integer) obstacleMap.get("y")) : ((double) obstacleMap.get("y"));
            obstacleC.position.z = (obstacleMap.get("z") instanceof Integer) ? Double.valueOf((Integer) obstacleMap.get("z")) : ((double) obstacleMap.get("z"));
            obstacleC.width = (obstacleMap.get("w") instanceof Integer) ? Double.valueOf((Integer) obstacleMap.get("w")) : ((double) obstacleMap.get("w"));
            obstacleC.height = (obstacleMap.get("h") instanceof Integer) ? Double.valueOf((Integer) obstacleMap.get("h")) : ((double) obstacleMap.get("h"));
            obstacleC.depth = (obstacleMap.get("d") instanceof Integer) ? Double.valueOf((Integer) obstacleMap.get("d")) : ((double) obstacleMap.get("d"));
            System.out.println(obstacleC);
            world.add(obstacleC);
        }

        Vector3D targetV = new Vector3D();
        targetV.x = (target.get("x") instanceof Integer) ? Double.valueOf((Integer) target.get("x")) : ((double) target.get("x"));
        targetV.y = (target.get("y") instanceof Integer) ? Double.valueOf((Integer) target.get("y")) : ((double) target.get("y"));
        targetV.z = (target.get("z") instanceof Integer) ? Double.valueOf((Integer) target.get("z")) : ((double) target.get("z"));

        try {
            ArrayList<Vector3D> path = Pathfinder.findPath(world, robotC, targetV, padding).points;
            ArrayList<ArrayList<Double>> pointList = new ArrayList<>();
            for(Vector3D point : path) {
                ArrayList<Double> pointArray = new ArrayList<>();
                pointArray.add(point.x);
                pointArray.add(point.y);
                pointArray.add(point.z);
                pointList.add(pointArray);
            }
            System.out.println(instance.toJSON(pointList));
        } catch(DestinationUnreachableException e) {
            System.out.println("{\"error\":\"destination unreachable\",\"reason\":\"" + e.getMessage() + "\"}");
        }
    }

}