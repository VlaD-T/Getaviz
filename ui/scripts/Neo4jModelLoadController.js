var neo4jModelLoadController = (function () {

    let controllerConfig = {
        url: 'http://localhost:7474/db/data/transaction/commit',
        loadStartData: 'rootPackages',
        showLoadSpinner: false
    };

    let loaderController = {
        dataToLoad: 0
    };

    //Add to loader
    let loaderApplicationEvent = {			
        sender: neo4jModelLoadController,
        value: 'toLoad'
    };

    function initialize(setupConfig) {
        application.transferConfigParams(setupConfig, controllerConfig);
        events.loaded.on.subscribe(loadElementsAndChangeState);
        events.loaded.off.subscribe(updateLoadSpinner);
        events.childsLoaded.on.subscribe(getChildMetaDataOnExpand);
        if (controllerConfig.showLoadSpinner) {
            createLoadSpinner();
        }
    };


    // Spinner to show loading process of entites 
    // starts with loading from Neo4j and ends with creating a Model in AframeCanvasManipulator
    function createLoadSpinner() {
        let loader = document.createElement('div');
        loader.id = 'loaderController';
        loader.className = 'hidden';
        loader.innerHTML = '<div class="loaderController-roller"><div class="loader">Loading...</div><div id="loaderController-data"></div>';
        document.body.appendChild(loader);
    }

    function updateLoadSpinner(payload) { // payload = toLoad / loaded;
        if (!controllerConfig.showLoadSpinner) {
            return;
        }
        let loader = document.getElementById('loaderController');
        payload.value === 'toLoad'? loaderController.dataToLoad ++ : loaderController.dataToLoad--
        if (loaderController.dataToLoad !== 0) {
            loader.classList.remove('hidden');
            let loaderData = document.getElementById('loaderController-data');
            loaderData.innerHTML = `Loading elements: ${loaderController.dataToLoad}`;
        } else {
            loader.classList.add('hidden');
        }
    }


    // Get metadata on launch. Either rootPackages or everything (depends on settings)
    async function getStartMetaData() {
        let payload = {};
        if (controllerConfig.loadStartData === 'rootPackages') {
            console.log('Load root packages');
            payload = {
                'statements': [
                    // neo4j requires keyword "statement", so leave as is
                    // { 'statement': `MATCH (p:Package) WHERE NOT (:Package)-[:CONTAINS]->(p) RETURN p` }
                    { 'statement': `MATCH (p:Package) RETURN p` }
                ]
            }
        } else { // load everything
            console.log('Load everything');
        }

        let response = await getNeo4jData(payload);
        let data = [];
        let childNodes = [];
        if (!response[0].data.length) {
            return;
        }

        // Add all metadata information from response (may be more root packages)
        for (object of response[0].data) {
            // console.log(object)
            let metadataProperty = object.row[0].metadata;
            let metadataObject = JSON.parse(`${metadataProperty}`); // create an object
            data.push(metadataObject);

            // Get any child node to show the expand sign
            let singleChildNode = await getChildNodesMetaData(object.row[0].hash, true);
            childNodes.push(...singleChildNode);
        }

        model.createEntities(data)
        model.createEntities(childNodes);
    };

    // Get MetaData for Child node
    async function getChildMetaDataOnExpand(applicationEvent) {
        let data = await getChildNodesMetaData(applicationEvent.entity.id, false);
        model.createEntities(data); 
    };

    // Get MetaData for Child node
    async function getChildNodesMetaData(parentNodeHash, limitOne) {
        let limit = '';
        if (limitOne) {
            limit = `LIMIT 1`;
        }
        let payload = {
            'statements': [
                // neo4j requires keyword "statement", so leave as is
                { 'statement': `MATCH (parent)-[:DECLARES|HAS|CONTAINS]->(child) WHERE parent.hash = "${parentNodeHash}" AND EXISTS(child.hash) RETURN child ${limit}` }
            ]
        };

        let response = await getNeo4jData(payload);
        let data = [];
        if (!response[0].data.length) {
            return data;
        }

        for (object of response[0].data) {
            let metadataProperty = object.row[0].metadata;
            let metadataObject = JSON.parse(`${metadataProperty}`); // create an object
            data.push(metadataObject);
        }

        return data;
    }

    // For given Node in Model load it's A-Frame code and create the element in DOM
    async function loadElementsAndChangeState(applicationEvent) {
        try {
            for (entity of applicationEvent.entities) {
                // Entity is already in DOM or will be added soon. 
                if (entity.loaded) {
                    continue;
                }

                loaderApplicationEvent.value = 'toLoad'
                updateLoadSpinner(loaderApplicationEvent);

                let results = await getAframeCodeById(entity.id);
                for (result of results) {
                    // There may be some empty entites, like buildingSegments. They don't have any data, so we can't create an element for them.
                    if (result.data[0]) {
                        // console.log('append Element') // to test an event loop
                        // Start drawing element
                        canvasManipulator.appendAframeElementWithProperties(result.data[0]);
                    } else {
                        loaderApplicationEvent.value = 'loaded'
                        updateLoadSpinner(loaderApplicationEvent);
                    } 
                }
            }
        } catch (error) {
            console.error(error);
        }
    };

    // Getting A-Frame code for node
    // This function is called for each new node from metaData from Model.js (createEntities) separately.
    async function getAframeCodeById(nodeId) {
        const payload = {
            'statements': [
                // neo4j requires keyword "statement", so leave as is
                { 'statement': `MATCH (n) WHERE n.nodeHashId ="${nodeId}" RETURN n` }
            ]
        }

        let response = await getNeo4jData(payload);
        return response;
    }

    // Universal method to load data from Neo4j using imported query
    async function getNeo4jData(payload) {
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
        getStartMetaData: getStartMetaData,
        getChildMetaDataOnExpand: getChildMetaDataOnExpand
    };
})();