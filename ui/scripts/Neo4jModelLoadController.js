var neo4jModelLoadController = (function () {

    let controllerConfig = {
        url: 'http://localhost:7474/db/data/transaction/commit',
        loadStartData: 'rootPackages',
        showLoadSpinner: false
    };

    // Count may jump from 300 to 100 because of the empty entites, like buildingSegments.
    // Loader is impacted by Neo4j requests and entity creation:
    // 
    let loaderController = {
        dataToLoad: 0
    };

    // Loader standard parameters
    let loaderApplicationEvent = {			
        sender: neo4jModelLoadController,
        value: 'toLoad'
    };


    function initialize(setupConfig) {
        application.transferConfigParams(setupConfig, controllerConfig);
        events.loaded.on.subscribe(loadElementsAndChangeState);
        events.loaded.off.subscribe(updateLoadSpinner);
        events.childsLoaded.on.subscribe(onNodeExpandGetChildrenMeta);

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
        payload.value === 'toLoad'? loaderController.dataToLoad ++ : loaderController.dataToLoad --
        if (loaderController.dataToLoad !== 0) { // show loader
            loader.classList.remove('hidden');
            let loaderData = document.getElementById('loaderController-data');
            loaderData.innerHTML = `Loading elements: ${loaderController.dataToLoad}`;
        } else { // hide loader
            loader.classList.add('hidden');
        }
    }


    // Get metadata on launch. Either rootPackages or everything (depends on settings)
    async function loadStartMetaData() {
        let payload = {};
        let loadOneChild;
        if (controllerConfig.loadStartData === 'rootPackages') {
            loadOneChild = true;
            payload = {
                'statements': [
                    // neo4j requires keyword "statement", so leave as is
                    { 'statement': `MATCH (p:Package) WHERE NOT (:Package)-[:CONTAINS]->(p) RETURN p` }
                ]
            }
        } else { // load everything
            loadOneChild = false;
            payload = {
                'statements': [
                    // neo4j requires keyword "statement", so leave as is
                    { 'statement': `MATCH (p:Package) RETURN p` }
                ]
            }
        }

        let parentNodes = await getMetadataForParentNodes(payload);
        let childNodes = await getMetadataForChildNodes(parentNodes, loadOneChild);
        model.createEntities(parentNodes)
        model.createEntities(childNodes);
    }


    // Returning parent nodes metadata, prepared to be inserted into model.js
    async function getMetadataForParentNodes(payload) {
        let response = await getNeo4jData(payload);
        let data = await getMetadataFromResponse(response);
        return data;
    }


    // Get child nodes metadata + one or all child node of this node.
    async function getMetadataForChildNodes(parentNodes, limitOne) {
        let childNodesMetadata = [];
        if (!parentNodes) {
            return childNodesMetadata;
        }

        for (parentNode of parentNodes) {
            let childNodes = await getNeo4jChildNodes(parentNode.id, limitOne);
            if (!childNodes) {
                continue;
            }

            for (childNode of childNodes) {
                childNodesMetadata.push(childNode);
            }
        }

        return childNodesMetadata;
    }


    // Get MetaData for Child node on node expand
    async function onNodeExpandGetChildrenMeta(applicationEvent) {
        let entity = model.getEntityById(applicationEvent.entity.id)
        entity.childsLoaded = true;
        entity.children.forEach(child => {
            return child.dummyForExpand = false;
        });
        let parentNodes = await getNeo4jChildNodes(applicationEvent.entity.id, false); // parent nodes inside selected node
        let childNodes = await getMetadataForChildNodes(parentNodes, true); // select single child to show expand sign
        model.createEntities(parentNodes); 
        model.createEntities(childNodes); 
    };


    // Get MetaData for Child node
    async function getNeo4jChildNodes(parentNodeHash, limitOne = false) {
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
        let data = await getMetadataFromResponse(response);
        return data;
    }


    // Return metadata object from response
    async function getMetadataFromResponse(response) {
        let data = [];
        if (!response[0].data.length) {
            return;
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
    }


    // Getting A-Frame code for node
    // This function is called for each new node from metaData from Model.js (createEntities) separately.
    async function getAframeCodeById(nodeId) {
        const payload = {
            'statements': [
                // neo4j requires keyword "statement", so leave as is
                { 'statement': `MATCH (n) WHERE n.nodeHashId ="${nodeId}" AND EXISTS(n.aframeProperty) RETURN n` }
            ]
        }

        let response = await getNeo4jData(payload);
        return response;
    }


    // Universal method to load data from Neo4j using imported query
    async function getNeo4jData(payload) {
        loaderApplicationEvent.value = 'toLoad'
        updateLoadSpinner(loaderApplicationEvent);

        let response = await fetch(controllerConfig.url, {
            method: 'POST', 
            body: JSON.stringify(payload), 
            headers: {
                'Content-Type': 'application/json'
            }
        });

        let data = await response.json();

        loaderApplicationEvent.value = 'loaded'
        updateLoadSpinner(loaderApplicationEvent);
        return data.results;
    }


    return {
        initialize: initialize,
        loadStartMetaData: loadStartMetaData
    };
})();