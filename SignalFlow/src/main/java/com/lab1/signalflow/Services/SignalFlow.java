package com.lab1.signalflow.Services;

import java.util.ArrayList;

public class SignalFlow {
    
    ArrayList<ArrayList<String>> ForwardPaths=new ArrayList<>();
    ArrayList<Integer>path_gain=new ArrayList<>();
    ArrayList<ArrayList<String>> Loops=new ArrayList<>();
    ArrayList<Integer>loop_gain=new ArrayList<>();
    int temp=1;

    public ArrayList<ArrayList<String>> GetForwardPaths(ArrayList<Node>graph,int start_index,int dest_index){
        ArrayList<String> path = new ArrayList<>();
        ArrayList<Node>visited=new ArrayList<>();
        path.add(graph.get(start_index).getName());
        GetForwardPath(path, visited,path_gain, graph.get(start_index), graph.get(dest_index));
        return ForwardPaths;
    }

    public void GetForwardPath(ArrayList<String>path,ArrayList<Node>visited,ArrayList<Integer>path_gain,Node start,Node dest){
        if(start.getName().equals(dest.getName())){
            path_gain.add(temp);
            ForwardPaths.add((ArrayList)path.clone());
            return;
        }
        visited.add(start);
        for(Node adj_node:start.adjacent){
            if(!visited.contains(adj_node)){
                path.add(adj_node.getName());
                temp*=start.gain.get(start.adjacent.indexOf(adj_node));
                GetForwardPath(path, visited,path_gain, adj_node,dest);
            
            path.remove(path.indexOf(adj_node.getName()));
            temp/=start.gain.get(start.adjacent.indexOf(adj_node));
            }
        }
        visited.remove(start);
    }
    ArrayList<Integer>temp_gain=new ArrayList<>();
    public ArrayList<ArrayList<String>> GetLoops(ArrayList<Node>graph,int start_index,int dest_index){
        ArrayList<String> loop = new ArrayList<>();
        ArrayList<Node>visited=new ArrayList<>();
        loop.add(graph.get(start_index).getName());
        temp_gain.add(1);
        GetLoop(loop,visited,loop_gain, graph.get(start_index), graph.get(dest_index));
        return Loops;
    }
    public void GetLoop(ArrayList<String>loop,ArrayList<Node>visited,ArrayList<Integer>loop_gain,Node start,Node dest){
        if(start.getName().equals(dest.getName())){
            return;
        }
        visited.add(start);
        for(Node adj_node:start.adjacent){
            if(!visited.contains(adj_node)){
                loop.add(adj_node.getName());
                temp_gain.add(start.gain.get(start.adjacent.indexOf(adj_node)));
                GetLoop(loop, visited,loop_gain, adj_node, dest);
                loop.remove(loop.indexOf(adj_node.getName()));
                temp_gain.remove(temp_gain.indexOf(start.gain.get(start.adjacent.indexOf(adj_node))));
            }
            else{
                temp_gain.add(start.gain.get(start.adjacent.indexOf(adj_node)));
                ArrayList<String>temp_loop=new ArrayList<>();
                for(int i=loop.indexOf(adj_node.getName());i<loop.size();i++){
                    temp_loop.add(loop.get(i));
                    temp*=temp_gain.get(i+1);
                }
                temp_loop.add(adj_node.getName());
                temp_gain.remove(temp_gain.indexOf(start.gain.get(start.adjacent.indexOf(adj_node))));
                //temp*=start.gain.get(start.adjacent.indexOf(adj_node));
                Loops.add(temp_loop);
                loop_gain.add(temp);
                temp=1;
                
            }
            }
        visited.remove(start);
    }

    public ArrayList<Integer> GetNonTouchingLoops(ArrayList<String> current,ArrayList<ArrayList<String>> loops){
        boolean[]touched=new boolean[loops.size()];
        for(int j=0;j<loops.size();j++)
        touched[j]=false;
        for(String node :current){
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