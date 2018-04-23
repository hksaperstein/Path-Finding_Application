package edu.wpi.cs3733d18.teamp.Pathfinding;


import java.util.ArrayList;

abstract public class Pathfinder {

    /**
     * runPathfinder is a template pattern method that finds the path and puts it in ArrayList
     * @param srcNode start node
     * @param destNode destination node
     * @return the path created by the algorithm
     */
    public ArrayList<Node> runPathfinder(Node srcNode, Node destNode){
        findPath(srcNode, destNode);
        ArrayList<Node> path = getPath(destNode);
        System.out.println("Returning path!");
        return path;
    }
    public abstract void findPath(Node srcNode, Node destNode);

    /**
     * getPath helper function for parsing the path generated by search algorithm
     * @param node destination node from search algorithm
     * @return ArrayList<Node> constituting the path
     */
    public ArrayList<Node> getPath(Node node) {
        System.out.println("Getting path!");
        ArrayList<Node> tempPath = new ArrayList<Node>();
//        System.out.println("Node given: " + n.toString());
        int count = 0;
        while (node.hasParent()) {
            System.out.println("count: " + count);
            count++;
//            System.out.println("Parent: " + n.getParent());
            tempPath.add(node);
            node = node.getParent();
        }
        tempPath.add(node);
//        System.out.println("tempPath: " + tempPath);
        ArrayList<Node> path = (ArrayList<Node>) tempPath.clone();

//        System.out.println("tempPath.size(): " + tempPath.size());
//        System.out.println("path: " + path);
//        System.out.println("path.size(): " + path.size());
        for (int i = 0; i < tempPath.size() / 2; i++) {
            path.set(tempPath.size() - i - 1, tempPath.get(i));
            path.set(i, tempPath.get(tempPath.size() - i - 1));
        }
        return path;
    }

}
