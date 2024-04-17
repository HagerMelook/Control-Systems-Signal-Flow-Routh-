<template>
  <h1>Signal Flow Graph Analyzer</h1>
  <div style="height: 88vh">
    <div class="GUI">
      <div id="myDiagramDiv" style="height:70vh; width :100%;border-radius: 25px; background-color:white  ">
        <div ref="container"></div>
      </div>

      <div id ="input">
        <input type="number" min="2" max="100" value="2" ref="statesNumber" style="height: 35px; border-radius: 10px; text-align:center; margin-top:15%; font-size:16px; background-color: rgb(234, 237, 241); ">
        <button @click="generateStates">Generate Nodes</button>
        <button @click="analyze">Gain Analysis</button>
      </div>
    </div>
  </div>

  <div id ="output" >
    <h1>Analysis</h1>
    <div style="padding: 1%;">
      <div id ="output-fields" >
        <h2>Forward paths:</h2>
        <table>
          <tr>
            <th></th>
            <th>Path</th>
            <th>Gain</th>
            <th>&Delta;</th>
          </tr>
          <tr v-for = "forwardpath in forwardpaths" :key ="forwardpath.id">
              <td>Path{{forwardpath.id}}</td>
              <td><span v-for= "(path,index) in forwardpath.Path" :key = "index">{{path}}<span v-if="index != forwardpath.Path.length - 1"> - </span></span></td>
              <td>{{ forwardpath.Gain }}</td>
              <td>{{ forwardpath.Delta }}</td>
          </tr>
        </table>
      </div>

      <div id ="output-fields">
        <h2>Individual loops:</h2>
        <table>
          <tr>
            <th></th>
            <th>Loop</th>
            <th>Gain</th>
          </tr>
          <tr v-for = "individualloop in individualloops" :key ="individualloop.id">
              <td>Loop{{individualloop.id}}</td>
              <td><span v-for= "(indloop,index) in individualloop.Path" :key = "index">{{indloop}}<span v-if="index != individualloop.Path.length - 1"> - </span></span></td>
              <td>{{individualloop.Gain }}</td>
          </tr>
        </table>
      </div>

      <div id ="output-fields">
        <h2>Non-Touching Loops:</h2>
        <table style="width: 50%; margin-bottom: 20px;" v-for="nontouchingloop in nontouchingloops" :key ="nontouchingloop">
          <tr>
              <th>{{nontouchingloop[0].length}} Non-Touching Loops</th>
          </tr>
          <tr v-for="ntloop in nontouchingloop" :key="ntloop">
            <td><span v-for= "(loop,index) in ntloop" :key = "index">Loop{{loop}}<span v-if="index != ntloop.length - 1"> - </span></span></td>
          </tr>
        </table>
      </div>

      <div id ="output-fields">
        <h2 style="font-size: 30px;">&Delta; = <span style="font-weight:100; color: black;">{{ delta }}</span></h2>
      </div>

      <div id ="output-fields">
        <h2>TF = <span style="font-weight:100; color: black;">{{ transferFunction }}</span></h2>
        <span></span>
      </div>

    </div>
  </div>
</template>

<script>

import axios from 'axios';
import Go from 'gojs';

export default {

  data(){
    return{
      server: 'http://localhost:8080',
      nodes : [],
      forwardpaths:[],
      fpId: 0,
      individualloops:[],
      loopId: 0,
      nontouchingloops:[],
      delta: 0,
      transferFunction: 0,
    }
  },

  methods:{
    
    generateStates(){
      let numberOfNodes = this.$refs.statesNumber.value;
      console.log(numberOfNodes);
      this.nodes=[];
      for (let i=0;i<numberOfNodes;i++){
        if(i==0) this.nodes[i]={name:"R(S)",color:"RGB(193,193,214)",fontColor:"black",receive:false}
        else if(i==numberOfNodes-1) this.nodes[i]={name:"C(S)",color:"RGB(193,193,214)",fontColor:"black",from:false}
        else this.nodes[i]={name:i,color:"RGB(64,70,103)",fontColor:"white"}
      }
      this.myDiagram.model = new go.GraphLinksModel(this.nodes);
    },
    modifyGain(e){
      console.log(e)
      e.subject.part.data.gain=e.Yr.kc;
      // console.log(e.subject.part.data);
      // console.log(e.subject.name);
    //   this.myDiagram.commit(d => {
    //     d.links.each(link => {
    //     console.log(link.data)
    // });
    //   }, "decrease scale");
    },
    async analyze(){
      let from=[],to=[],gains=[], i=0;
      this.myDiagram.commit(d => {
        d.links.each(link => {
          from[i] = Math.abs(link.data.from)-1;
          to[i] = Math.abs(link.data.to)-1;
          gains[i++] = link.data.gain;
          }
        );
      }, "decrease scale");
      let req = {
        "nodes" : this.$refs.statesNumber.value,
        "from" : from,
        "to" : to,
        "gains" : gains,
      };
      console.log(JSON.stringify(req))
      await axios.post("http://localhost:8080/flowgraph", req);

      const response = await axios.get("http://localhost:8080/flowgraph/analysis")
      let analysis = response.data
      
      this.delta = analysis.Delta 
      this.transferFunction = analysis.Transfer_Function

      for(let i = 0 ;i < analysis.Forward_Paths.length ;i++)
      {
        var forwardPath ={
          id: this.fpId++,
          Path: analysis.Forward_Paths[i],
          Gain: analysis.Forward_Paths_Gains[i],
          Delta: analysis.Deltas[i]
        }
        this.forwardpaths.push(forwardPath)
      }

      for(let i = 0 ;i < analysis.Loops.length ;i++)
      {
        var loop ={
          id: this.loopId++,
          Path: analysis.Loops[i],
          Gain: analysis.Loops_Gains[i],
        }
        this.individualloops.push(loop)
      }

      let ntLoops = []
      let lastSize = 2;
      for(let i = 0 ;i < analysis.Non_Touching_Loops.length ;i++)
      {
        let currentSize = analysis.Non_Touching_Loops[i].length
        if(currentSize != lastSize)
        {
          this.nontouchingloops.push(ntLoops)
          ntLoops = []
        }
        ntLoops.push(analysis.Non_Touching_Loops[i])
        lastSize = currentSize 
        if(i == analysis.Non_Touching_Loops.length -1)
        {
          this.nontouchingloops.push(ntLoops)
        }
      }
      this.showOutput()
    }
    ,showOutput() {
        var x = document.getElementById("output")
        x.style.display = 'block'
    }
  },

  mounted(){
  
    this.myDiagram =new go.Diagram("myDiagramDiv",{
      "undoManager.isEnabled": true,
      "TextEdited": this.modifyGain,
      });
    this.myDiagram.nodeTemplate = new go.Node("Auto",{})
    .add(new go.Shape("Ellipse",{
      width: 35,
      height: 35, 
      background: null, 
      portId: "",
      toLinkableDuplicates:true,
      fromLinkableDuplicates:true,
      toLinkableSelfNode:true,
      fromLinkableSelfNode:true,
      fromLinkable: true,
      toLinkable: true,
      relinkableFrom:true,
      relinkableTo:true,
      cursor: "pointer"
    })
      .bind("fill","color")
      .bind("portId","id")
      .bind("toLinkable","receive")
      .bind("fromLinkable","from"))
    .add(new go.TextBlock("",{
      stroke: "white",
      font: "bold 12px sans-serif",
      verticalAlignment: go.Spot.Center,
      textAlign: "center",
      cursor:"pointer"
    })
      .bind("text", "name")
      .bind("stroke","fontColor")
    );
    
    this.myDiagram.linkTemplate =new go.Link({
      curve: go.Link.Bezier,
      reshapable:true,
      adjusting: go.Link.Stretch,
      strokeWidth: 2,
      toShortLength: 6,
      relinkableFrom:true,
      relinkableTo:true,
    })
    .add(new go.Shape({
      strokeWidth: 3,
      stroke: "black",
    }))
    .add(new go.Shape({
      fill: "black",
      stroke: null,
      toArrow: "Standard",
      scale:1.2,
      stroke: "black"
    }))
    .add(new go.TextBlock("",{
      segmentOffset: new go.Point(0,-15),
      font:"bold 12px sans-serif",
      editable:true,
    })
      .bind("text","gain")
    )
      this.myDiagram.toolManager.linkingTool.archetypeLinkData  =
        { gain : 1 };
    },
    
  }
</script>

<style scoped>

  h2 {
    margin: 40px 0 0;
    margin-bottom: 2%;
  }
  ul {
    list-style-type: none;
    padding: 0;
  }
  li {
    display: inline-block;
    margin: 0 10px;
  }
  a {
    color: #47bea7;
  }
  h1{
    font-family: Georgia, 'Times New Roman', Times, serif;
    font-size: 60px;
    margin-top: 0;
  }
  h2{
    margin-bottom:12px;
    font-size: 30px;
  }
  button{
    display: block;
    color:azure;
    background-color: rgb(64, 70, 103);
    height: 60px;
    width: 160px;
    margin-left: 7px; 
    margin-right: 7px;
    margin-top: 40%;
    border-radius: 10px;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
  }
  button:hover{
    background-color: rgb(60, 54, 82);
  }
  table {
        width: 100%;
        border-collapse: collapse;
  }
  th, td {
      border: 1px solid #dddddd;
      text-align: center;
      padding: 8px;
      font-size: 25px;
  }
  th {
      background-color: #f2f2f2;
      font-size: 25px;
  }
  tr {
      border-top: 1px solid #dddddd;
      border-bottom: 1px solid #dddddd;
  }

  .GUI{
    height: fit-content;
    border: 3px solid rgb(40, 38, 58);
    border-radius: 25px;
    display: flex;
  }
  #input{
    width: fit-content;
    border-left: 3px solid rgb(40, 38, 58);
    border-top-right-radius: 25px;
    border-bottom-right-radius: 25px;
    background-color: rgb(193, 193, 214);
  }
  #output{
    height: fit-content;
    margin-top: -5%;
    border-top: 0px;
    background-color: white;
    border: 3px solid rgb(40, 38, 58);
    border-radius: 25px;
    display:none
  }
  #output-fields{
    text-align: left;
  }

</style>
