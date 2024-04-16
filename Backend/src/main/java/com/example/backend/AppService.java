package com.example.backend;

import com.example.backend.service.routh_hurwitz.RouthHurwitz;
import com.example.backend.service.signal_flow.GraphRep;
import com.example.backend.service.signal_flow.Node;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.InputMismatchException;

@Service
public class AppService {
    private GraphRep graphRep = new GraphRep();

    public String routhHurwitz(String equation) {
        RouthHurwitz routhHurwitz = new RouthHurwitz();
        try {
            routhHurwitz.init(equation);
        } catch (InputMismatchException e) {
            return "Invalid Input";
        }
        routhHurwitz.developRouthTable();
        return routhHurwitz.systemInfo().toString();
    }

    public void signalFlowGraph(String graphStr) {
        Gson gson = new Gson();
        ArrayList<Node> graph = new ArrayList<>();
        JSONObject graphJson = new JSONObject(graphStr);

        this.graphRep.constructGraph(graph);
    }

    public String signalFlowGraphAnalysis() {
        JSONObject analysis = new JSONObject();

        ArrayList<ArrayList<Integer>> forwardPaths = this.graphRep.getForwardPaths();
        JSONArray forwardPathsJson = new JSONArray();
        for (ArrayList<Integer> forwardPath : forwardPaths) {
            JSONArray forwardPathJson = new JSONArray();
            for (int node : forwardPath)
                forwardPathJson.put(node);
            forwardPathsJson.put(forwardPathJson);
        }
        analysis.put("Forward_Paths", forwardPathsJson);

        ArrayList<Double> forwardPathGains = this.graphRep.getForwardPathsGains();
        JSONArray forwardPathGainsJson = new JSONArray();
        for (double gain : forwardPathGains)
            forwardPathGainsJson.put(gain);
        analysis.put("Forward_Paths_Gains", forwardPathGainsJson);


        ArrayList<ArrayList<Integer>> loops = this.graphRep.getIndividualLoops();
        JSONArray loopsJson = new JSONArray();
        for (ArrayList<Integer> loop : loops) {
            JSONArray loopJson = new JSONArray();
            for (int node : loop)
                loopJson.put(node);
            loopsJson.put(loopJson);
        }
        analysis.put("Loops", loopsJson);

        ArrayList<Double> loopsGains = this.graphRep.getIndividualLoopsGains();
        JSONArray loopsGainsJson = new JSONArray();
        for (double gain : loopsGains) {
            loopsGainsJson.put(gain);
        }
        analysis.put("Loops_Gains", loopsGainsJson);

        ArrayList<ArrayList<Integer>> nonTouchingLoops = this.graphRep.getNonTouchingLoops();
        JSONArray nonTouchingLoopsJson = new JSONArray();
        for (ArrayList<Integer> nonTouchingCombination : nonTouchingLoops) {
            JSONArray combinationJson = new JSONArray();
            for (int loop : nonTouchingCombination)
                combinationJson.put(loop);
            nonTouchingLoopsJson.put(combinationJson);
        }
        analysis.put("Non_Touching_Loops", nonTouchingLoopsJson);

        ArrayList<Double> deltas = this.graphRep.getPathDelta();
        JSONArray deltasJson = new JSONArray();
        for (double delta : deltas)
            deltasJson.put(delta);
        analysis.put("Deltas", deltasJson);

        analysis.put("Delta", this.graphRep.getDelta());

        analysis.put("Transfer_Function", this.graphRep.getOverallTF());

        return analysis.toString();
    }
}