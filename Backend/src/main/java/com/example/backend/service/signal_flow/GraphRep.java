
package com.example.backend.service.signal_flow;

import java.util.ArrayList;


public class GraphRep {
    ArrayList<Node>graph=new ArrayList<>();
    ArrayList<Double>path_delta=new ArrayList<>();
    ArrayList<ArrayList<Integer>> loops = new ArrayList<>();
    ArrayList<ArrayList<Integer>> forward_paths=new ArrayList<>();
    ArrayList<ArrayList<Integer>> non_touched_loops=new ArrayList<>();
    ArrayList<ArrayList<Integer>>path_delta_gain=new ArrayList<>();
    Double delta=1.0;
    SignalFlow signalFlow = new SignalFlow();

    public void repGraph(){
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
    }

    public ArrayList<ArrayList<Integer>> getForwardPaths(){
        forward_paths=signalFlow.GetForwardPaths(graph);
        return forward_paths;
    }

    public ArrayList<Double> getForwardPathsGains(){
        return signalFlow.path_gain;
    }

    public ArrayList<ArrayList<Integer>> getIndividualLoops(){
        loops=signalFlow.GetLoops(graph);
        loops=signalFlow.GetNonDoublicateLoops(loops);
        return loops;
    }

    public ArrayList<Double> getIndividualLoopsGains(){
        return signalFlow.loop_gain;
    }

    public ArrayList<ArrayList<Integer>> getNonTouchingLoops(){
        for(int i = 0;i<loops.size();i++)
            non_touched_loops.add(signalFlow.GetNonTouchingLoops(loops.get(i), loops));
        return non_touched_loops;
    }

    public ArrayList<ArrayList<Integer>> getLoopsForEachPath(){
        for(int i = 0;i<forward_paths.size();i++){
            path_delta_gain.add(signalFlow.GetNonTouchingLoops(forward_paths.get(i), loops));
        }
        return path_delta_gain;
    }

    public Double getDelta(){
        for(int i = 0 ;i<loops.size();i++){
            delta-=signalFlow.loop_gain.get(i);
            if(!non_touched_loops.get(i).isEmpty()){
                for(int j=0;j<non_touched_loops.get(i).size();j++){
                    delta+=(signalFlow.loop_gain.get(i)*signalFlow.loop_gain.get(non_touched_loops.get(i).get(j))/2.0);
                }
            }
        }
        return delta;
    }

    public ArrayList<Double> getPathDelta(){
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
        return path_delta;
    }

    public Double getOverallTF(){
        double overall_T_F=0.0;
        for(int i = 0;i<forward_paths.size();i++){
            overall_T_F+=signalFlow.path_gain.get(i)*path_delta.get(i);
        }
        overall_T_F/=delta;
        return overall_T_F;
    }
}


