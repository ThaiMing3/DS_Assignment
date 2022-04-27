package com.mycompany.assignment;
import java.io.*;
import java.util.*;

class Graph {

    // No. of vertices in graph
    private int v;

    // adjacency list
    private ArrayList<Integer>[] adjList;
    private List<ArrayList<Integer>> path2 = new ArrayList<>();

    /**
     * Default constructor
     */
    public Graph(){}
    
    // Constructor
    public Graph(int vertices) {

        // initialise vertex count
        this.v = vertices;

        // initialise adjacency list
        initAdjList();
    }

    /**
     * Initialize adjacency list
     */
    @SuppressWarnings("unchecked")
    private void initAdjList() {
        adjList = new ArrayList[v];

        for (int i = 0; i < v; i++) {
            adjList[i] = new ArrayList<>();
        }
    }

    /**
     * Add edge from u to v vice versa
     * @param u
     * @param v 
     */
    public void addEdge(int u, int v) {
        adjList[u].add(v);
        adjList[v].add(u);
    }

    /**
     * To start finding path
     * @param s
     * @param d 
     */
    public void InitPathFinder(int s, int d) {
        boolean[] isVisited = new boolean[v];
        ArrayList<Integer> pathList = new ArrayList<>();

        // add source to path[]
        pathList.add(s);

        // Call recursive utility
        pathFinder(s, d, isVisited, pathList);
    }

    /**
     * Recursive method to find all path
     * @param u
     * @param d
     * @param isVisited
     * @param localPathList (for recursive)
     */
    private void pathFinder(Integer u, Integer d,
            boolean[] isVisited,
            ArrayList<Integer> localPathList) {

        if (u.equals(d)) {
            String tmp = localPathList.toString();
            tmp = tmp.substring(1, tmp.length() - 1);
            String[] tmp3 = tmp.split(", ");
            ArrayList<Integer> tmp4 = new ArrayList<>();
            for (String x : tmp3) {
                Integer tmp2 = Integer.parseInt(x);
                tmp4.add(tmp2);
            }
            path2.add(tmp4);
            // if match found then no need to traverse more till depth
            return;
        }

        // Mark the current node
        isVisited[u] = true;

        // Recur for all the vertices
        // adjacent to current vertex
        for (Integer i : adjList[u]) {
            if (!isVisited[i]) {
                // store current node
                // in path[]
                localPathList.add(i);
                pathFinder(i, d, isVisited, localPathList);

                // remove current node
                // in path[]
                localPathList.remove(i);
            }
        }

        // Mark the current node
        isVisited[u] = false;
    }

    /**
     * Delete palindrome path
     * @return all path
     */
    public String deletePath() {
        // Nested loop that will remove the palindrome path based on a specific index
        for (int i = 0; i < path2.size(); i++) {
            for (int j = 0; j < path2.size(); j++) {
                if (i != j) {
                    int[] arr = new int[path2.get(i).size()];
                    for (int k = 0; k < path2.get(i).size(); k++) {
                        arr[k] = path2.get(i).get(k);
                    }
                    int[] arr1 = new int[path2.get(j).size()];
                    int l = 0;
                    for (int k = path2.get(j).size() - 1; k >= 0; k--) {
                        arr1[l] = path2.get(j).get(k);
                        l++;
                    }
                    // Compare element in path(index i) with element in path(index j)
                    // If same then remove the path(index j)
                    if (Arrays.equals(arr, arr1)) {
                        path2.remove(j);
                    }
                }
            }
        }
        // Sort in ascending order (size)
        ArrayList<Integer> tmp = new ArrayList<>();
        for (int i = 0; i < path2.size() - 1; i++) {
            for (int j = 0; j < path2.size() - 1 - i; j++) {
                if(path2.get(j).size() > path2.get(j+1).size()){
                    tmp = path2.get(j);
                    path2.remove(j);
                    path2.add(j, path2.get(j));
                    path2.remove(j + 1);
                    path2.add(j + 1, tmp);
                }
            }
        }
        
        // Add into String str to return to GUI to be printed out
        String str = "";
        for (int i = 0; i < path2.size(); i++) {
            str += (i+1) + ". " + path2.get(i) + "\n";
        }
        return str;
    }

    // Test method
    public static void main(String args[]) {
        System.out.println("START");
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        Graph g = new Graph(n + 1);

        for (int i = 0; i < n; i++) {
            int s = sc.nextInt();
            int d = sc.nextInt();
            g.addEdge(s, d);
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if (i != j) {
                    g.InitPathFinder(i, j);
                }
            }
        }
        g.deletePath();

    }

    /**
     * 
     * @param text
     * @param graphsize
     * @return String to be printed out in GUI
     */
    public String split(String text, int graphsize){
        Graph g = new Graph(graphsize+1);
        String[] temp = text.split("\n");
        for (int i = 0; i < temp.length; i++) {
            String[] temp2 = temp[i].split(",");
            g.addEdge(Integer.valueOf(temp2[0]), Integer.valueOf(temp2[1]));
        }
        for (int i = 1; i <= graphsize; i++) {
            for (int j = 1; j <= graphsize; j++) {
                if (i != j) {
                    g.InitPathFinder(i, j);
                }
            }
        }
        return g.deletePath();
    }
}
