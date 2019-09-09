console.log('test works');
$('a-scene').append($('<a-box position="-1 0.5 -3" rotation="0 45 0" color="#4CC3D9"></a-box>'));

var url = ' http://localhost:7474/db/data/transaction/commit ';
var payload = {
    "statements" : [
        {"statement" : "MATCH (n:Package) RETURN n"}
    ]
}

fetch(url, {
  method: 'POST', 
  body: JSON.stringify(payload), 
  headers:{
    'Content-Type': 'application/json'
  }
}).then(response => response.json())
.then(data => console.log('Success:', data))
.catch(error => console.error('Error:', error));