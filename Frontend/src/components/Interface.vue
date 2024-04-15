<template>
  <div class="GUI">
    <input type="number" min="2" max="100" value="2" ref="statesNumber">
    <button @click="generateStates">Generate Nodes</button>
    <button @click="analyze">Gain Analysis</button>
  </div>
  <div id="myDiagramDiv" ref="myDiagramDiv" style="height:500px; widows:100px; border: 5px black solid; background-color: #DAE4E4;">
  <div ref="container"></div>

  </div>

</template>

<script>

import axios from 'axios'
import Go from 'gojs'

export default {
  data(){
    return{
      nodes : [],
    }
  },

  methods:{
    
    generateStates(){
      let numberOfNodes = this.$refs.statesNumber.value;
      console.log(numberOfNodes);
      this.nodes=[];
      for (let i=0;i<numberOfNodes;i++){
        if(i==0) this.nodes[i]={name:"C(S)",color:"RGB(193,193,214)",fontColor:"black"}
        else if(i==numberOfNodes-1) this.nodes[i]={name:"R(S)",color:"RGB(193,193,214)",fontColor:"black"}
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
    analyze(){
      let links=[], i=0;
      this.myDiagram.commit(d => {
        d.links.each(link => {
          links[i++]={
            "from" : Math.abs(link.data.from)-1,
            "to" : Math.abs(link.data.to)-1,
            "gain" : link.data.gain,
          }
        });
      }, "decrease scale");
      let req = {
        "numberOfNodes" : this.$refs.statesNumber.value,
        "branches" : links
      };
      console.log(JSON.stringify(req))
      axios.post("http://localhost:8080/flowgraph",JSON.stringify(req));
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
      // toLinkableDuplicates:true,
      fromLinkable: true,
      toLinkable: true,
      relinkableFrom:true,
      relinkableTo:true,
      cursor: "pointer"
    })
      .bind("fill","color")
      .bind("portId","id"))
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
h3 {
  margin: 40px 0 0;
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
  color: #42b983;
}
</style>