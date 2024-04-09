
package com.example.backend.service.signal_flow;

import java.util.ArrayList;

class Node{
    public Node() {
    }

    private String name; 
    public ArrayList<Integer> gain=new ArrayList<>();
    public ArrayList<Node> adjacent=new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

public class GraphRep {
    public static void main(String[] args) {
            int start_index=0;
            int dest_index=4;
            ArrayList<Node>graph=new ArrayList<>();
            for(int i = 0;i<8;i++){
                graph.add(new Node());
            }
            graph.get(0).setName("R");
            graph.get(0).gain.add(1);
            graph.get(0).adjacent.add(graph.get(1));
            graph.get(0).gain.add(5);
            graph.get(0).adjacent.add(graph.get(5));
            // graph.get(0).gain.add(5);
            // graph.get(0).adjacent.add(graph.get(0));
            graph.get(1).setName("X1");
            graph.get(1).gain.add(2);
            graph.get(1).adjacent.add(graph.get(2));
            graph.get(2).setName("X2");
            graph.get(2).gain.add(3);
            graph.get(2).adjacent.add(graph.get((3)));
            graph.get(2).gain.add(1);
            graph.get(2).adjacent.add(graph.get((1)));
            graph.get(3).setName("X3");
            graph.get(3).gain.add(4);
            graph.get(3).adjacent.add(graph.get(4));
            graph.get(3).gain.add(2);
            graph.get(3).adjacent.add(graph.get(2));
            // graph.get(3).gain.add(2);
            // graph.get(3).adjacent.add(graph.get(1));
            // graph.get(3).gain.add(2);
            // graph.get(3).adjacent.add(graph.get(3));
            graph.get(4).setName("C");
            graph.get(5).setName("X5");
            graph.get(5).gain.add(6);
            graph.get(5).adjacent.add(graph.get(6));
            graph.get(6).setName("X6");
            graph.get(6).gain.add(7);
            graph.get(6).adjacent.add(graph.get((7)));
            graph.get(6).gain.add(3);
            graph.get(6).adjacent.add(graph.get((5)));
            graph.get(7).setName("X7");
            graph.get(7).gain.add(8);
            graph.get(7).adjacent.add(graph.get((4)));
            graph.get(7).gain.add(4);
            graph.get(7).adjacent.add(graph.get((6)));

            SignalFlow signalFlow = new SignalFlow();
            ArrayList<ArrayList<String>> forward_paths=signalFlow.GetForwardPaths(graph,start_index,dest_index);
            for(int i = 0;i<forward_paths.size();i++){
                for(int j = 0;j<forward_paths.get(i).size();j++){
                    System.out.print(forward_paths.get(i).get(j));
                }
                System.out.print(signalFlow.path_gain.get(i));
                System.out.println();
            }
            
            // signalFlow = new SignalFlow();
            ArrayList<ArrayList<String>> loops=signalFlow.GetLoops(graph,start_index,dest_index);
            for(int i = 0;i<loops.size();i++){
                for(int j = 0;j<loops.get(i).size();j++){
                    System.out.print(loops.get(i).get(j));
                }
                System.out.print(signalFlow.loop_gain.get(i));
                System.out.println();
            }

            ArrayList<ArrayList<Integer>> non_touched_loops=new ArrayList<>();
            for(int i = 0;i<loops.size();i++)
                non_touched_loops.add(signalFlow.GetNonTouchingLoops(loops.get(i), loops));
            
            for(int i = 0;i<non_touched_loops.size();i++){
                System.out.println(non_touched_loops.get(i));
            }
        double delta=1;
        for(int i = 0 ;i<loops.size();i++){
            delta-=signalFlow.loop_gain.get(i);
            if(!non_touched_loops.get(i).isEmpty()){
                for(int j=0;j<non_touched_loops.get(i).size();j++){
                    delta=delta+(signalFlow.loop_gain.get(i)*signalFlow.loop_gain.get(non_touched_loops.get(i).get(j))/2.0);
                }
            }
        }
        System.out.println(delta);
        ArrayList<ArrayList<Integer>>path_delta_gain=new ArrayList<>();
        for(int i = 0;i<forward_paths.size();i++){
            path_delta_gain.add(signalFlow.GetNonTouchingLoops(forward_paths.get(i), loops));
        }
        for(int i=0;i<path_delta_gain.size();i++){
            System.out.println(path_delta_gain.get(i));
        }

        ArrayList<Double>path_delta=new ArrayList<>();
        for(int i = 0;i<forward_paths.size();i++){
            double path_loop_delta=1;
            for(int j =0 ;j<path_delta_gain.get(i).size();j++){
                path_loop_delta-=signalFlow.loop_gain.get(path_delta_gain.get(i).get(j));
                if(!non_touched_loops.get(path_delta_gain.get(i).get(j)).isEmpty()){
                    for(Integer loop_index:non_touched_loops.get(path_delta_gain.get(i).get(j))){
                        if(path_delta_gain.get(i).contains(loop_index))
                        path_loop_delta+=(signalFlow.loop_gain.get(path_delta_gain.get(i).get(j))*signalFlow.loop_gain.get(path_delta_gain.get(i).get(loop_index)))/2.0;
                    }
                }
            }
            path_delta.add(path_loop_delta);
        }
        System.out.println(path_delta);

        double overall_T_F=0;
        for(int i = 0;i<forward_paths.size();i++){
            overall_T_F+=signalFlow.path_gain.get(i)*path_delta.get(i);
        }
        overall_T_F/=delta;

        System.out.println(overall_T_F);
    }
}


