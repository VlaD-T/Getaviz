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
        return console.log(`Element with ID:${element.meta[0].id} has no "aframe_code";`);
      }
    })
  })
})
.catch(error => console.error('Error:', error));