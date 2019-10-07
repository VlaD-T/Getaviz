// Prepare all the requests
const url = 'http://localhost:7474/db/data/transaction/commit';
const payload = {
    'statements' : [
      // neo4j requires keyword "statement", so leave as is
        {'statement' : 'MATCH (d:District) RETURN d'},
        {'statement' : 'MATCH (b:Building) RETURN b'},
        {'statement' : 'MATCH (bs:BuildingSegment) RETURN bs'}
    ]
}

// Receive all the data and proceed
fetch(url, {
  method: 'POST', 
  body: JSON.stringify(payload), 
  headers:{
    'Content-Type': 'application/json'
  }
}).then(response => response.json())
.then(selects => {
// loop through select queries (districts, buildings, b-segments)
  selects.results.forEach(select => {
  // loop through elements for selected query
    select.data.forEach(element => {
      let aframe_code = element.row[0].aframe_code;
      if (aframe_code) {
        return $('a-scene').append($(aframe_code));
      } else {
        //return console.log(`Element with ID:${element.meta[0].id} has no "aframe_code";`);
      }
    })
  })
})
.catch(error => console.error('Error:', error));









const url = 'http://localhost:7474/db/data/transaction/commit';
        const payload = {
            'statements': [
                // neo4j requires keyword "statement", so leave as is
                { 'statement': `MATCH (n) WHERE n.nodeHashId ="${nodeId}" RETURN n`}
            ]
        }


var aframeModelLoadController = (function () {

  let controllerConfig = {
  };

  function initialize(setupConfig) {
      application.transferConfigParams(setupConfig, controllerConfig);
  };

  async function checkIfElementWasAlreadyLoaded(nodeId) {
      return model.getEntityById(nodeId).isLoaded;
  }

  function checkAndLoadNodeById(nodeId) {
      checkIfElementWasAlreadyLoaded(nodeId).then(isLoaded => {
          if (isLoaded) {
              return;
          } 
          
          console.log("is not loaded");
          
          
      });

      // Receive all the data and proceed
      /*fetch(url, {
          method: 'POST',
          body: JSON.stringify(payload),
          headers: {
              'Content-Type': 'application/json'
          }
      }).then(response => response.json())
          .then(selects => {
              // loop through select queries (districts, buildings, b-segments)
              selects.results.forEach(select => {
                  // loop through elements for selected query
                  select.data.forEach(element => {
                      let aframe_code = element.row[0].aframe_code;
                      if (aframe_code) {
                          return $('#aframe-canvas').append($(aframe_code));
                      } else {
                          //return console.log(`Element with ID:${element.meta[0].id} has no "aframe_code";`);
                      }
                  })
              })
          })
          .catch(error => console.error('Error:', error));*/
  };

  return {
      initialize: initialize,
      checkAndLoadNodeById: checkAndLoadNodeById,
     // loadData: loadData
  };
})();