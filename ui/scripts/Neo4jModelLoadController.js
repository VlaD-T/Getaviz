var neo4jModelLoadController = (function () {

    let controllerConfig = {
        url: 'http://localhost:7474/db/data/transaction/commit',
        loadStartData: 'rootPackages'
    };

    function initialize(setupConfig) {
        application.transferConfigParams(setupConfig, controllerConfig);
        events.selected.on.subscribe(checkAndLoadNodesById); // packageExplorer - by activating the element
        events.filtered.off.subscribe(checkAndLoadNodesById); // packageExplorer - by showing the element
    };

    function loadStartData() {
        console.log(controllerConfig.loadStartData);
    };

    async function loadRootPackages() {
        const payload = {
            'statements': [
                // neo4j requires keyword "statement", so leave as is
                { 'statement': `MATCH (n) WHERE n.nodeHashId ="${nodeId}" RETURN n` }
            ]
        }

        let response = await getNeo4jData(payload);
    }

    async function checkAndLoadNodesById(applicationEvent) {
        try {
            for (entity of applicationEvent.entities) {
                let isLoaded = await checkIfElementIsAlreadyLoaded(entity.id);
                if (isLoaded) {
                    continue;
                }

                model.changeLoadedStatusForEntity(entity.id);
                let results = await loadNodeById(entity.id);
                for (result of results) {
                    // There may be some empty entites, like buildingSegments. They don't have any data, so we can't create an element for them.
                    if (result.data[0]) {
                        canvasManipulator.appendAframeElementWithProperties(result.data[0]);
                    }
                }
            }
        } catch (error) {
            console.error(error);
        }
    };

    async function checkIfElementIsAlreadyLoaded(nodeId) {
        return model.getEntityById(nodeId).loaded;
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
        loadStartData: loadStartData
    };
})();