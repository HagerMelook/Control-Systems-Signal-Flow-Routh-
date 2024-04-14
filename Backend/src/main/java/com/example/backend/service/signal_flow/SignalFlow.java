package com.example.backend.service.signal_flow;

import java.util.ArrayList;
import java.util.HashMap;

public class SignalFlow {
    
    ArrayList<ArrayList<Integer>> ForwardPaths=new ArrayList<>();
    ArrayList<Double>path_gain=new ArrayList<>();
    ArrayList<ArrayList<Integer>> Loops=new ArrayList<>();
    ArrayList<Double>loop_gain=new ArrayList<>();
    double temp=1.0;

    // get forward paths and their gains
    public ArrayList<ArrayList<Integer>> GetForwardPaths(ArrayList<Node>graph){
        ArrayList<Integer> path = new ArrayList<>();
        ArrayList<Node>visited=new ArrayList<>();
        path.add(graph.get(0).getId());
        GetForwardPath(path, visited,path_gain, graph.get(0), graph.get(graph.size() - 1));
        return ForwardPaths;
    }

    public void GetForwardPath(ArrayList<Integer>path,ArrayList<Node>visited,ArrayList<Double>path_gain,Node start,Node dest){
        if(start.getId()==dest.getId()){
            path_gain.add(temp);
            ForwardPaths.add((ArrayList)path.clone());
            return;
        }
        visited.add(start);
        for(Node adj_node:start.adjacent){
            if(!visited.contains(adj_node)){
                path.add(adj_node.getId());
                temp*=start.gain.get(start.adjacent.indexOf(adj_node));
                GetForwardPath(path, visited,path_gain, adj_node,dest);
                path.remove(path.indexOf(adj_node.getId()));
                temp/=start.gain.get(start.adjacent.indexOf(adj_node));
            }
        }
        visited.remove(start);
    }
    
    // get loops and their gain
    HashMap<Integer,Double>temp_gain=new HashMap<>();
    public ArrayList<ArrayList<Integer>> GetLoops(ArrayList<Node> graph){
        ArrayList<Integer> loop = new ArrayList<>();
        ArrayList<Node> visited=new ArrayList<>();
        loop.add(graph.get(0).getId());
        temp=1.0;
        GetLoop(loop,visited,loop_gain, graph.get(0), graph.get(graph.size() - 1));
        return Loops;
    }
    public void GetLoop(ArrayList<Integer>loop,ArrayList<Node>visited,ArrayList<Double>loop_gain,Node start,Node dest){
        if(start.getId()==dest.getId()){
            return;
        }
        visited.add(start);
        for(Node adj_node:start.adjacent){
            if(!visited.contains(adj_node)){
                loop.add(adj_node.getId());
                temp_gain.put(adj_node.getId(),start.gain.get(start.adjacent.indexOf(adj_node)));
                GetLoop(loop, visited,loop_gain, adj_node, dest);
                loop.remove(loop.indexOf(adj_node.getId()));
                temp_gain.remove(adj_node.getId());
            }
            else{
                ArrayList<Integer>temp_loop=new ArrayList<>();
                for(int i=loop.indexOf(adj_node.getId());i<loop.size();i++){
                    temp_loop.add(loop.get(i));
                    if(i!=loop.size()-1)
                        temp*=temp_gain.get(loop.get(i+1));
                    
                }
                temp_loop.add(adj_node.getId());
                temp*=start.gain.get(start.adjacent.indexOf(adj_node));
                Loops.add(temp_loop);
                loop_gain.add(temp);
                temp=1.0;
                
            }
            }
        visited.remove(start);
    }
    // remove dublicate loops
    public ArrayList<ArrayList<Integer>> GetNonDoublicateLoops(ArrayList<ArrayList<Integer>> loops){
        for(int i=0;i<loops.size();i++){
            for(int j=i+1;j<loops.size();j++){
                int no_of_dublicate_node=0;
            for(Integer node:loops.get(i)){
                if(loops.get(j).contains(node))
                        no_of_dublicate_node++;
                }
                if(no_of_dublicate_node==loops.get(i).size()&&no_of_dublicate_node==loops.get(j).size()){
                    loops.remove(j);
                    loop_gain.remove(j);
                }
            }
            }
            return loops;
        }

    // get non_touched_loops
    public ArrayList<Integer> GetNonTouchingLoops(ArrayList<Integer> current,ArrayList<ArrayList<Integer>> loops){
        boolean[]touched=new boolean[loops.size()];
        for(int j=0;j<loops.size();j++)
            touched[j]=false;
        for(Integer node :current){
            for(int i=0;i<loops.size();i++){
                if(loops.get(i).contains(node)&&touched[i]==false){
                    touched[i]=true;
                }
            }
        }
        ArrayList<Integer>non_touched=new ArrayList<>();
        for(int k =0;k<loops.size();k++)
            if(!touched[k])
                non_touched.add(k);
        return non_touched;
    }

}