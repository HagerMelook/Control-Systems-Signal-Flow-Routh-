
package com.example.backend.service.signal_flow;

import java.util.ArrayList;


public class GraphRep {
    private ArrayList<Node> graph;
    private final ArrayList<Double> path_delta;
    private ArrayList<ArrayList<Integer>> loops;
    private ArrayList<ArrayList<Integer>> forward_paths;
    private ArrayList<ArrayList<Integer>> non_touched_loops;
    private final ArrayList<ArrayList<Integer>> path_delta_gain;
    private Double delta;
    private SignalFlow signalFlow;

    public GraphRep() {
        this.graph = new ArrayList<>();
        this.path_delta = new ArrayList<>();
        this.loops = new ArrayList<>();
        this.forward_paths = new ArrayList<>();
        this.non_touched_loops = new ArrayList<>();
        this.path_delta_gain = new ArrayList<>();
        this.delta = 1.0;
        this.signalFlow = new SignalFlow();
    }

    public void init(ArrayList<Node> graph) {
        this.signalFlow = new SignalFlow();
        this.graph = graph;
    }

    public ArrayList<ArrayList<Integer>> getForwardPaths(){
        System.out.println(graph.size());
        forward_paths=signalFlow.GetForwardPaths(graph);
        return forward_paths;
    }

    public ArrayList<Double> getForwardPathsGains(){
        return signalFlow.getPath_gain();
    }

    public ArrayList<ArrayList<Integer>> getIndividualLoops(){
        Node dummy_Node=new Node(graph.size());
        graph.get(graph.size()-1).adjacent.add(dummy_Node);
        graph.get(graph.size()-1).gain.add(1.0);
        graph.add(dummy_Node);
        loops=signalFlow.GetLoops(graph);
        loops=signalFlow.GetNonDoublicateLoops(loops,true);
        graph.remove(graph.size()-1);
        graph.get(graph.size()-1).adjacent.remove(graph.get(graph.size()-1).adjacent.size()-1);
        graph.get(graph.size()-1).gain.remove(graph.get(graph.size()-1).gain.size()-1);
        return loops;
    }

    public ArrayList<Double> getIndividualLoopsGains(){
        return signalFlow.getLoop_gain();
    }


    public ArrayList<ArrayList<Integer>> getNonTouchingLoops(){
        non_touched_loops=signalFlow.GetAllCombinationsOfNonTouchingLoops(loops);
        non_touched_loops=signalFlow.GetNonDoublicateLoops(non_touched_loops,false);
        return non_touched_loops;
    }

    public void getLoopsForEachPath(){
        for (ArrayList<Integer> forwardPath : forward_paths) {
            path_delta_gain.add((ArrayList<Integer>) signalFlow.getLoopsForPath(forwardPath, loops));
        }
    }

    public Double getDelta(){
        for(int i = 0 ;i<loops.size();i++){
            delta-= signalFlow.getLoop_gain().get(i);
        }
        for (ArrayList<Integer> nonTouchedLoop : non_touched_loops) {
            double temp_delta = Math.pow(-1, nonTouchedLoop.size());
            for (Integer loop_index : nonTouchedLoop) {
                temp_delta *= signalFlow.getLoop_gain().get(loop_index);
            }
            delta += temp_delta;
        }
        return delta;
    }

    public ArrayList<Double> getPathDelta(){
        for(int i = 0;i<forward_paths.size();i++){
            double path_loop_delta=1;
            for(int j =0 ;j<path_delta_gain.get(i).size();j++){
                path_loop_delta-= signalFlow.getLoop_gain().get(path_delta_gain.get(i).get(j));
            }
            for (ArrayList<Integer> nonTouchedLoop : non_touched_loops) {
                boolean non_touched_loop_path = true;
                double temp_delta = Math.pow(-1, nonTouchedLoop.size());
                for (Integer loop_index : nonTouchedLoop) {
                    temp_delta *= signalFlow.getLoop_gain().get(loop_index);
                    if (!path_delta_gain.get(i).contains(loop_index)) {
                        non_touched_loop_path = false;
                        break;
                    }
                }
                if (non_touched_loop_path) path_loop_delta += temp_delta;
            }
            path_delta.add(path_loop_delta);
        }
        return path_delta;
    }

    public Double getOverallTF(){
        double overall_T_F=0.0;
        for(int i = 0;i<forward_paths.size();i++){
            overall_T_F+= signalFlow.getPath_gain().get(i)*path_delta.get(i);
        }
        overall_T_F/=delta;
        return overall_T_F;
    }
}
