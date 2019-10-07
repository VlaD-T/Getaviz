var aframeModelLoadController = (function () {

    let controllerConfig = {
    };

    function initialize(setupConfig) {
        application.transferConfigParams(setupConfig, controllerConfig);
    };

    async function checkIfElementWasAlreadyLoaded(nodeId) {
        return model.getEntityById(nodeId).isLoaded;
    }

    //model.getEntityById(nodeId).isLoaded = true;

    function checkAndLoadNodeById(nodeId) {
        checkIfElementWasAlreadyLoaded(nodeId).then(isLoaded => {
            if (isLoaded) {
                return;
            }
            console.log("in Process");
            model.changeLoadedStatus(nodeId);
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
                                console.log("Yay");
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

    return {
        initialize: initialize,
        checkAndLoadNodeById: checkAndLoadNodeById
    };
})();