
package com.example.backend.service.signal_flow;

import java.util.ArrayList;


public class GraphRep {
    public static void main(String[] args) {

            // Representation of the graph as adjacency list
            ArrayList<Node>graph=new ArrayList<>();
            for(int i = 0;i<8;i++)
                graph.add(new Node());

            graph.get(0).setId(0);
            graph.get(0).gain.add(1.0);
            graph.get(0).adjacent.add(graph.get(1));
            graph.get(0).gain.add(5.0);
            graph.get(0).adjacent.add(graph.get(4));
            graph.get(1).setId(1);
            graph.get(1).gain.add(2.0);
            graph.get(1).adjacent.add(graph.get(2));
            graph.get(2).setId(2);
            graph.get(2).gain.add(1.0);
            graph.get(2).adjacent.add(graph.get((1)));
            graph.get(2).gain.add(3.0);
            graph.get(2).adjacent.add(graph.get((3)));
            graph.get(3).setId(3);
            graph.get(3).gain.add(2.0);
            graph.get(3).adjacent.add(graph.get(2));
            graph.get(3).gain.add(4.0);
            graph.get(3).adjacent.add(graph.get(7));
            graph.get(4).setId(4);
            graph.get(4).gain.add(6.0);
            graph.get(4).adjacent.add(graph.get(5));
            graph.get(5).setId(5);
            graph.get(5).gain.add(7.0);
            graph.get(5).adjacent.add(graph.get(6));
            graph.get(5).gain.add(3.0);
            graph.get(5).adjacent.add(graph.get(4));
            graph.get(6).setId(6);
            graph.get(6).gain.add(8.0);
            graph.get(6).adjacent.add(graph.get(7));
            graph.get(6).gain.add(4.0);
            graph.get(6).adjacent.add(graph.get(5));
            graph.get(7).setId(7);

            // get forward paths, forward path'gains
            SignalFlow signalFlow = new SignalFlow();
            ArrayList<ArrayList<Integer>> forward_paths=signalFlow.GetForwardPaths(graph);
            System.out.println("\nNumber of forward paths: "+forward_paths.size());
            for(int i = 0;i<forward_paths.size();i++){
                System.out.print("Path "+(i+1)+": "+forward_paths.get(i));
                System.out.print("     gain: "+signalFlow.path_gain.get(i));
                System.out.println();
            }
            
            // get individula loops and their gains
            ArrayList<ArrayList<Integer>> loops=signalFlow.GetLoops(graph);
            loops=signalFlow.GetNonDoublicateLoops(loops);
            System.out.println("\nNumber of individual loops: "+loops.size());
            for(int i = 0;i<loops.size();i++){
                System.out.print("Loop "+(i+1)+": "+loops.get(i));
                System.out.print("     gain: "+signalFlow.loop_gain.get(i));
                System.out.println();
            }

            // get non_touched loops
            ArrayList<ArrayList<Integer>> non_touched_loops=new ArrayList<>();
            for(int i = 0;i<loops.size();i++)
                non_touched_loops.add(signalFlow.GetNonTouchingLoops(loops.get(i), loops));
            
            for(int i = 0;i<non_touched_loops.size();i++){
                System.out.print("\nNon_touched_loops for loop "+(i+1)+" :");
                System.out.println(non_touched_loops.get(i));
            }

        // calculate delta
        double delta=1;
        for(int i = 0 ;i<loops.size();i++){
            delta-=signalFlow.loop_gain.get(i);
            if(!non_touched_loops.get(i).isEmpty()){
                for(int j=0;j<non_touched_loops.get(i).size();j++){
                    delta+=(signalFlow.loop_gain.get(i)*signalFlow.loop_gain.get(non_touched_loops.get(i).get(j))/2.0);
                }
            }
        }
        System.out.println("\nDelta "+delta+"\n");


        // calculate delta for each path
        ArrayList<ArrayList<Integer>>path_delta_gain=new ArrayList<>();
        for(int i = 0;i<forward_paths.size();i++){
            path_delta_gain.add(signalFlow.GetNonTouchingLoops(forward_paths.get(i), loops));
        }
        for(int i=0;i<path_delta_gain.size();i++){
            System.out.print("Loops for path "+i+" :");
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
        System.out.println("\nDelta for each path: "+path_delta);

        // get overall transfer function
        double overall_T_F=0.0;
        for(int i = 0;i<forward_paths.size();i++){
            overall_T_F+=signalFlow.path_gain.get(i)*path_delta.get(i);
        }
        overall_T_F/=delta;
        System.out.println("\nOverall TF of the system: "+overall_T_F);
    }
}


