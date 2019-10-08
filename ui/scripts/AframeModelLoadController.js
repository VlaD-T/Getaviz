var aframeModelLoadController = (function () {

    let controllerConfig = {
    };

    function initialize(setupConfig) {
        application.transferConfigParams(setupConfig, controllerConfig);
    };

    async function checkAndLoadNodeById(nodeId) {
        try {
            let isLoaded = await checkIfElementIsAlreadyLoaded(nodeId);
            if (isLoaded) {
                return;
            }
            model.changeIsLoadedStatus(nodeId);
            await loadNodeById(nodeId)
            .then(data => {
                let results = data.results;
                results.forEach(result => {
                    if (result.data[0]) {
                        let aframe_code = result.data[0].row[0].aframe_code;
                        if (aframe_code) {
                            return $('#aframe-canvas').append($(aframe_code));
                        }
                    } else {
                        console.log(result);
                    }
                })
            });       
        } catch (error) {
            console.error(error);
        }
    }

    function checkIfElementIsAlreadyLoaded(nodeId) {
        return model.getEntityById(nodeId).isLoaded;
    }

    async function loadNodeById(nodeId) {
        const url = 'http://localhost:7474/db/data/transaction/commit';
        const payload = {
            'statements': [
                // neo4j requires keyword "statement", so leave as is
                { 'statement': `MATCH (n) WHERE n.nodeHashId ="${nodeId}" RETURN n` }
            ]
        }

        // Receive all the data and proceed
        let response = await fetch(url, {
            method: 'POST', 
            body: JSON.stringify(payload), 
            headers: {
                'Content-Type': 'application/json'
            }
        });

        let data = await response.json();
        return data;
    }


       /* fetch(url, {
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
                            console.log('Yay');
                            return $('#aframe-canvas').append($(aframe_code));
                        } else {
                            //return console.log(`Element with ID:${element.meta[0].id} has no "aframe_code";`);
                        }
                    })
                })
            })
            .catch(error => console.error('Error:', error));
    }*/

    /*async function checkAndLoadNodeById(nodeId) {
        let isLoaded = await checkIfElementIsAlreadyLoaded(nodeId);
        if (isLoaded) {
            return;
        }

        console.log("in Process");
        model.changeIsLoadedStatus(nodeId);
        const url = 'http://localhost:7474/db/data/transaction/commit';
        const payload = {
            'statements': [
                // neo4j requires keyword "statement", so leave as is
                { 'statement': `MATCH (n) WHERE n.nodeHashId ="${nodeId}" RETURN n` }
            ]
        }

        // Receive all the data and proceed
        fetch(url, {
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
                            console.log('Yay');
                            return $('#aframe-canvas').append($(aframe_code));
                        } else {
                            //return console.log(`Element with ID:${element.meta[0].id} has no "aframe_code";`);
                        }
                    })
                })
            })
            .catch(error => console.error('Error:', error));
    };
*/
    /*
function checkAndLoadNodeById(nodeId) {
        let isLoaded = await checkIfElementIsAlreadyLoaded(nodeId);
        checkIfElementIsAlreadyLoaded(nodeId).then(isLoaded => {
            if (isLoaded) {
                return;
            }
            
            console.log("in Process");
            model.changeIsLoadedStatus(nodeId);
            const url = 'http://localhost:7474/db/data/transaction/commit';
            const payload = {
                'statements': [
                    // neo4j requires keyword "statement", so leave as is
                    { 'statement': `MATCH (n) WHERE n.nodeHashId ="${nodeId}" RETURN n` }
                ]
            }

            // Receive all the data and proceed
            fetch(url, {
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
                                console.log('Yay');
                                return $('#aframe-canvas').append($(aframe_code));
                            } else {
                                //return console.log(`Element with ID:${element.meta[0].id} has no "aframe_code";`);
                            }
                        })
                    })
                })
                .catch(error => console.error('Error:', error));
        });
    };
    */

    return {
        initialize: initialize,
        checkAndLoadNodeById: checkAndLoadNodeById
    };
})();