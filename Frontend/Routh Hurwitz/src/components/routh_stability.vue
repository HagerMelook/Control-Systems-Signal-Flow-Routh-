<template>
    <div class="body">
        <div class = "input">
            <h2>Enter characteristic equation:</h2>
            <input class = "equation" v-model="equation" placeholder="CHs.eq" />
            <br>
            <button class = "button" @click="check_stability" >Check Stability</button>
        </div>  
        <br>
        <div v-if = "solve">
            <div v-if="valid">
                <table>
                    <colgroup>
                        <col>
                    </colgroup>
                    <template v-for="i in output.Routh_Table.length" v-bind:key="i.value">
                        <tr>
                            <td>S^ {{output.Routh_Table.length - i}} row</td>
                            <template v-for="j in output.Routh_Table[0].length" v-bind:key="j.value">
                                <td>{{output.Routh_Table[i-1][j-1]}}</td>
                            </template>
                        </tr>
                    </template>
                </table>
                <br>
                <div v-if = "stable===true">
                    <h1> Because No changes in sign appear in the first column, we find that the system is stable.</h1>
                </div>
                <div v-else>
                    <h1> Because {{output.Number_of_RHS_poles}} changes in sign appear in the first column, we find that {{output.Number_of_RHS_poles}} roots of the characteristic equation lie in the right hand side of the s-plane. Hence the system is unstable.</h1>
                    <table>
                        <tr>
                            <th>Pole order</th>
                            <th>Pole value</th>
                        </tr>
                        <tr v-for="k in RHS_poles.length" v-bind:key="k.value">
                            <td style="background-color: #FDDF95">pole {{k}}</td>
                            <td>{{RHS_poles[k-1]}}</td>
                        </tr>
                    </table>
                </div>
            </div>
            <div v-else>
                <h1>In valid input.</h1>
            </div>
        </div> 
    </div>
</template>

<script>

    export default ({
        data() {
            return {
            equation : '',
            solve : false ,
            output : [] ,
            stable : false ,
            RHS_poles : [] ,
            valid: false,
            ok : false ,
            }
        },

        methods: {
      
            check_stability(){ 
                fetch("http://localhost:8080/routh" , {
                    method : "post",
                    body:
                        this.equation,
                })
                
                .then(response =>{
                    if(!response.ok){
                        this.ok = false ;
                        this.valid = false ;
                        this.solve = true ;
                    }
                    else{
                        this.ok = true ;
                        return response.json() ;
                    }
                }).then(data =>{
                    if(this.ok){
                        this.output = data ;
                        this.display_system(data) ;    
                    }                  
                });
            },

            display_system(data){ 
                if(data.Stability == 'Stable')
                    this.stable = true ;

                if(data.Stability == 'Not Stable'){
                    this.stable = false ;
                    this.RHS_poles = [] ;
                    for (var i=0 ; i< data.RHS_poles.length ; i++){
                        if(data.RHS_poles[i].Imaginary ==0)
                            this.RHS_poles.push(data.RHS_poles[i].Real) ;
                        else if(data.RHS_poles[i].Imaginary <0)
                            this.RHS_poles.push(data.RHS_poles[i].Real + '' + data.RHS_poles[i].Imaginary+'j' ) ;
                        else 
                            this.RHS_poles.push(data.RHS_poles[i].Real  +'+' + data.RHS_poles[i].Imaginary+'j' ) ;
                    }
                }
                this.valid = true;
                this.solve = true ;    
            },
        },
    });
</script> 


<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap');


table , td , th {
    border: 4px solid black ;
    border-collapse: collapse ;
    font-size: 25px;
}

th , td{
    padding: 10px ;
}

colgroup {
    width: 250px ;
    background-color: #FDDF95 ;
    font-size: 50px;
}

.button {
  background-color: #aa0470; 
  border: none;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  display: block;
  margin: auto;
  font-size: 16px;
  cursor: pointer;
}

.equation {
  width: 100%;
  padding: 12px 20px;
  margin: 8px 0;
  display: inline-block;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 25px;
}

.input {
  border-radius: 5px;
  background-color: #f2f2f2;
  padding: 20px;
}

h1{
   color: red ;
}

</style>

/* 5s^5+12s^4+0s^3-s^2+s+1 */
/* s^3+2s^2+s+1 */ 
