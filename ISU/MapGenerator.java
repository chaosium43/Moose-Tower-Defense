package ISU;

import java.util.*;
// helper class for generating a map
public class MapGenerator {
    private static HashMap<Vector2, Vector2> dsu;
    private static Vector2[] directions = {new Vector2(1, 0), new Vector2(-1, 0), new Vector2(0, 1), new Vector2(0, -1)};
    private static Vector2 find(Vector2 v) { // finds the root node of a coordinate in the dsu
        Vector2 parent = dsu.get(v);
        if (parent.equals(v)) { // root node is found
            return v;
        }
        dsu.put(v, find(parent)); // look for the parent's root node and then set root to parent's root
        return dsu.get(v);
    }
    private static boolean connected(Vector2 a, Vector2 b) { // returns if two nodes are connected in the DSU
        return find(a) == find(b);
    }
    private static void join(Vector2 a, Vector2 b) { // joins two nodes together
        if (!connected(a, b)) {
            dsu.put(find(a), find(b));
        }
    }
    public static void generate(TileMap requester) {
        // initializing the disjoint set union and the list of nodes to iterate through

        dsu = new HashMap<Vector2, Vector2>();
        Vector2[] nodeList = new Vector2[112];
        HashMap<Vector2, Boolean> possiblityMap = new HashMap<Vector2, Boolean>();

        for (int x = 0, i = 0; x < 14; x++) {
            for (int y = 0; y < 8; y++, i++) {
                Vector2 v = new Vector2(x, y);
                dsu.put(v, v); 
                possiblityMap.put(v, false);
                nodeList[i] = v;
            }
        }

        // randomizing the order of the node listing
        for (int i = 0; i < nodeList.length; i++) {
            int j = (int)(Math.random() * nodeList.length);
            Vector2 a = nodeList[i];
            Vector2 b = nodeList[j];
            nodeList[i] = b;
            nodeList[j] = a;
        }

        // deciding on the starting and ending node
        Vector2 headNode = new Vector2(0, 0);
        Vector2 tailNode = new Vector2(0, 0);
        while (headNode.subtract(tailNode).manhattan() < 18) { // ensuring start and end aren't too close together
            headNode = new Vector2((int)(Math.random() * 14), (int)(Math.random() * 8));
            tailNode = new Vector2((int)(Math.random() * 14), (int)(Math.random() * 8));
        }

        // iterating through every node in the node list until the head and tail nodes are connected
        for (Vector2 node: nodeList) {
            possiblityMap.put(node, true);
            for (Vector2 direction: directions) { // iterating once through each direction
                Vector2 neighbour = node.add(direction);
                if (possiblityMap.containsKey(neighbour)) { // check if node is in range
                    if (possiblityMap.get(neighbour)) { // node is a potential path tile
                        join(node, neighbour);
                    }
                }
            }
            if (connected(headNode, tailNode)) { // enough potential path nodes have been added such that a path from the head to tail node exists
                break;
            }
        }

        // run bfs to find the path that was formed
        // this also has the benefit of finding the shortest possible path, preventing weird loop de loops and double width paths and other headaches
        HashMap<Vector2, Integer> distances = new HashMap<Vector2, Integer>();
        distances.put(headNode, 0);
        Queue<Vector2> bfs = new LinkedList<Vector2>();
        bfs.add(headNode);
        while (!bfs.isEmpty()) {
            Vector2 front = bfs.poll();
            int pathLength = distances.get(front) + 1; // path length of all nodes outbounds from the current node
            for (Vector2 direction: directions) {
                Vector2 neighbour = front.add(direction);
                if (possiblityMap.containsKey(neighbour)) { // node exists
                    if (possiblityMap.get(neighbour) && !distances.containsKey(neighbour)) { // check that the node is a potential path tile and that it hasn't been visited before
                        distances.put(neighbour, pathLength);
                        if (neighbour.equals(tailNode)) { // destination has been found and search may terminate
                            bfs.clear();
                            break;
                        }
                        bfs.offer(neighbour); // add neighbour node to be explored
                    }
                }
            }
        }

        // backtrack from the tail node to build the original path
        int[][] map = new int[14][8];
        map[(int)tailNode.getX()][(int)tailNode.getY()] = 4;

        Vector2 currentNode = tailNode;
        LinkedList<Vector2> pathPrototype = new LinkedList<Vector2>();
        pathPrototype.add(tailNode);
        while (!currentNode.equals(headNode)) {
            int distance = distances.get(currentNode);
            for (Vector2 direction: directions) {
                Vector2 neighbour = currentNode.add(direction);
                if (distances.containsKey(neighbour)) { // found a node that brings the path one closer to the head node
                    if (distances.get(neighbour) + 1 == distance) {
                        pathPrototype.addFirst(neighbour);
                        map[(int)neighbour.getX()][(int)neighbour.getY()] = 1;
                        currentNode = neighbour;
                        break;
                    }
                }
            }
        }

        map[(int)headNode.getX()][(int)headNode.getY()] = 3;

        // manually writing the path prototype to an array
        Vector2[] path = new Vector2[pathPrototype.size()];
        for (int i = 0; pathPrototype.size() != 0; i++) {
            path[i] = pathPrototype.poll();
        }
        requester.writeMap(map, path, headNode, tailNode);
    }
}
