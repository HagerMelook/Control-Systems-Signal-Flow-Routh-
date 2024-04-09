<template>
  <div class="GUI">
    <input type="number" min="2" max="100" value="2" ref="statesNumber">
    <button @click="generateStates">Generate Nodes</button>
    <button @click="null">Set Branches</button>
    <button @click="null">Gain Analysis</button>
  </div>
  <div ref="container">
  </div>
<!-- 
  <div id="graph" @load="init" style="height:100px; widows:100px;">

  </div> -->

</template>

<script>

import axios from 'axios'
import Konva from 'konva'
// import Go from 'go'

export default {
  data(){
    return{
      nodes : [],
      nodeXY :[200,200],
      go : null,
    }
  },

  methods:{
    

    // init(){
      
    //   // graph = go(graph,"graph");
    // },

    generateStates(){
      let numberOfNodes = this.$refs.statesNumber.value;
      console.log(numberOfNodes);
      //send nodes to backend

      this.initialize();
      for (let i=0;i<numberOfNodes;i++){
        let c= new Konva.Circle({
          radius : 15,
          id: i,
          fill:"black",
        });
        let t=new Konva.Text({
          fontSize: 15,
          fontFamily: 'Calibri',
          width: 25,
          align: 'center',
          x: c.x()-13,
          y: this.nodeXY[1]-56,
        });
        if (i==0||i==numberOfNodes-1){
          c.fill("white");
          c.stroke("black");
          if(i==0) t.text("C(S)");
          else t.text("R(S)"); 
        }
        else{
          t.stroke("white")
          t.text(i);
        }
        let g=new Konva.Group({
          x: this.nodeXY[0]=this.nodeXY[0]+50,
          y: this.nodeXY[1],
          draggable: true,
          id: i,
        });
        g.add(c).add(t);
        this.nodes[i]=g;
        this.nodes[i].add(c);
        this.nodes[i].add(t);
        this.layer.add(this.nodes[i]);
      }
    },

    initialize(){
      this.layer.destroyChildren().batchDraw()
      this.nodeXY=[50,50];
      this.nodes=[];
    },

  },

  mounted(){
    this.stage = new Konva.Stage({
      container: this.$refs.container,
      width: window.innerWidth*97/100,
      height: window.innerHeight*86/100,
      id :0,
      name: 'container'
    });
    this.layer = new Konva.Layer();
    this.stage.add(this.layer);
  }

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
