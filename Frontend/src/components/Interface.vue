<template>
  <div class="GUI">
    <input type="number" min="0" max="100" ref="statesNumber">
    <button @click="generateStates">Generate Nodes</button>
  </div>
  <div ref="container">

  </div>
</template>

<script>

import axios from 'axios'
import Konva from 'konva'

export default {
  data(){
    return{
      nodes : [],
      nodeID : 0,
      nodeXY :[50,50],
    }
  },

  methods:{
    
    initialize(){
      this.nodeID=0;
      this.nodeXY=[50,50];
      this.nodes=[];
    },

    generateStates(){
      let numberOfNodes = this.$refs.statesNumber.value;
      console.log(numberOfNodes);
      //send nodes to backend

      this.initialize();
      for (let i in numberOfNodes){
        let t;
        let c= new Konva.Circle({
          radius : 5,
          x : this.nodeXY[0]=this.nodeXY[0]+50,
          y : this.nodeXY[1]=this.nodeXY[1]+50,
          id: this.nodeID++,
          fill:"black",
        });

        if (this.nodeID==1||this.nodeID==numberOfNodes+1){
          c.fill("white");
          c.stroke("black");
          t=new Konva.Text({
            fontSize: 3,
            fontFamily: 'Calibri',
            width: 10,
            align: 'center',
          });
          if(this.nodeID==1) t.text("C(S)");
          else t.text("R(S)"); 
          this.nodes[i]= new Konva.Group({
            x : this.nodeXY[0]=this.nodeXY[0]+50,
            y : this.nodeXY[1]=this.nodeXY[1]+50,
            draggable : true,
          });
          this.nodes[i].add(c);
          this.nodes[i].add(t);
        }
        else{
          this.nodes[i]=c;
        }
      }
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
