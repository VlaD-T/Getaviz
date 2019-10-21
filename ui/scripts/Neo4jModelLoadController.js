var neo4jModelLoadController = (function () {

    let controllerConfig = {
        url: 'http://localhost:7474/db/data/transaction/commit',
        loadStartData: 'rootPackages'
    };

    function initialize(setupConfig) {
        application.transferConfigParams(setupConfig, controllerConfig);
        events.loaded.on.subscribe(changeStateAndLoadElements);
    };

    async function getStartData() {
        let payload = {}
        if (controllerConfig.loadStartData === 'rootPackages') {
            console.log('Load root packages');
            payload = {
                'statements': [
                    // neo4j requires keyword "statement", so leave as is
                    { 'statement': `MATCH (p:Package) WHERE NOT (:Package)-[:CONTAINS]->(p) RETURN p` }
                ]
            }
        } else { // load all
            console.log('Load everything');
        }

        let response = await getNeo4jData(payload);
        return response;
    };

    async function changeStateAndLoadElements(applicationEvent) {
        try {
            for (entity of applicationEvent.entities) {
                //Element must be in entities, but not in DOM. 
                let isInDOM = checkIfElementIsInDOM(entity.id);
                if (isInDOM) {
                    continue;
                }

                let results = await loadNodeById(entity.id);
                for (result of results) {
                    // There may be some empty entites, like buildingSegments. They don't have any data, so we can't create an element for them.
                    if (result.data[0]) {
                        console.log('append Element')
                        canvasManipulator.appendAframeElementWithProperties(result.data[0]);
                    }
                }

                model.changeEntityLoadedState(entity.id); //Change the status, so that we don't create the same DOM element again. 
            }
        } catch (error) {
            console.error(error);
        }
    };

    function checkIfElementIsInDOM(nodeId) {
        entity = model.getEntityById(nodeId)
        return entity ? entity.loaded : false
    }

    async function loadNodeById(nodeId) {
        const payload = {
            'statements': [
                // neo4j requires keyword "statement", so leave as is
                { 'statement': `MATCH (n) WHERE n.nodeHashId ="${nodeId}" RETURN n` }
            ]
        }

        let response = await getNeo4jData(payload);
        return response;
    }

    async function getNeo4jData(payload) {
        // Receive all the data and proceed
        let response = await fetch(controllerConfig.url, {
            method: 'POST', 
            body: JSON.stringify(payload), 
            headers: {
                'Content-Type': 'application/json'
            }
        });

        let data = await response.json();
        return data.results;
    }

    return {
        initialize: initialize,
        getStartData: getStartData
    };
})();