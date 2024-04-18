package com.example.backend.service.signal_flow;

import java.util.ArrayList;
import java.util.HashMap;

public class SignalFlow {
    private final ArrayList<ArrayList<Integer>> ForwardPaths;
    private final ArrayList<Double> path_gain;
    private final ArrayList<ArrayList<Integer>> Loops;
    private final ArrayList<Double> loop_gain;
    private ArrayList<Integer> non_touched;
    private final ArrayList<ArrayList<Integer>> AllCombination;
    private double temp;

    public SignalFlow() {
        this.ForwardPaths = new ArrayList<>();
        this.path_gain = new ArrayList<>();
        this.Loops = new ArrayList<>();
        this.loop_gain = new ArrayList<>();
        this.AllCombination = new ArrayList<>();
        this.non_touched = new ArrayList<>();
        this.temp = 1.0;
    }

    public ArrayList<Double> getPath_gain() {
        return path_gain;
    }

    public ArrayList<Double> getLoop_gain() {
        return loop_gain;
    }

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
        for(int i =0;i<start.adjacent.size();i++){
            if(!visited.contains(start.adjacent.get(i))){
                path.add(start.adjacent.get(i).getId());
                temp*=start.gain.get(i);
                GetForwardPath(path, visited,path_gain, start.adjacent.get(i),dest);
                path.remove(path.indexOf(start.adjacent.get(i).getId()));
                temp/=start.gain.get(i);
            }
        }
        visited.remove(start);
    }

    // get loops and their gain
    private HashMap<Integer,Double>temp_gain=new HashMap<>();
    public ArrayList<ArrayList<Integer>> GetLoops(ArrayList<Node>graph){
        ArrayList<Integer> loop = new ArrayList<>();
        ArrayList<Node>visited=new ArrayList<>();
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
    public ArrayList<ArrayList<Integer>> GetNonDoublicateLoops(ArrayList<ArrayList<Integer>> loops,boolean loop_test){
        for(int i=0;i<loops.size();i++){
            for(int j=i+1;j<loops.size();j++){
                int no_of_dublicate_node=0;
                boolean dublicate = false;
                for(int k=0;k<loops.get(i).size();k++){
                    if(!loop_test &&loops.get(j).contains(loops.get(i).get(k)))
                        no_of_dublicate_node++;
                    else if(loop_test &&loops.get(j).contains(loops.get(i).get(k))){
                        dublicate=check_doublicte_of_loop(k,loops.get(i),loops.get(j));
                        break;
                    }
                }
                if((!loop_test &&no_of_dublicate_node==loops.get(i).size()&&no_of_dublicate_node==loops.get(j).size())||(loop_test && dublicate)){
                    loops.remove(j);
                    if(loop_test)loop_gain.remove(j);
                }
            }
        }
        return loops;
    }

    public boolean check_doublicte_of_loop(int index,ArrayList<Integer>first_loop,ArrayList<Integer> sec_loop){
        if((first_loop.size()!=sec_loop.size())||index!=0) return false;
        int sec_index=sec_loop.indexOf(first_loop.get(index));
        while(index<first_loop.size()-1){
            if(first_loop.get(index)!=sec_loop.get(sec_index)) return false;
            index++;
            sec_index=(sec_index+1)%sec_loop.size();
            if(sec_index==0)sec_index++;
        }
        return true;
    }

    // get all combinations of non_touching loops
    public ArrayList<ArrayList<Integer>> GetAllCombinationsOfNonTouchingLoops(ArrayList<ArrayList<Integer>> loops){
        for(int i=0;i<loops.size();i++){
            non_touched=new ArrayList<>();
            GetNonTouchingLoops(i,loops.get(i), loops);
        }
        return AllCombination;
    }
    public void GetNonTouchingLoops(int loop_index,ArrayList<Integer> current,ArrayList<ArrayList<Integer>> loops){
        boolean[]touched=new boolean[loops.size()];
        for(int j=0;j<loops.size();j++)
            touched[j]=false;
        for(Integer node :current){
            for(int i=0;i<loops.size();i++){
                if(loops.get(i).contains(node)&& !touched[i]){
                    touched[i]=true;
                }
            }
        }
        non_touched.add(loop_index);
        for(int k =0;k<loops.size();k++){
            if(!touched[k]){
                ArrayList<Integer>temp_loop=new ArrayList<>();
                temp_loop.addAll(current);
                temp_loop.addAll(loops.get(k));
                GetNonTouchingLoops(k, temp_loop, loops);
                AllCombination.add((ArrayList<Integer>)non_touched.clone());
                temp_loop.removeAll(loops.get(k));
                non_touched.remove(non_touched.indexOf(k));
            }
        }
        return;
    }

    // get non_touched_loops_for_path
    public ArrayList<Integer> getLoopsForPath(ArrayList<Integer> current,ArrayList<ArrayList<Integer>> loops){
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