let neo4jModelLoadController = (function () {

    //  Default config (Fallback in case it's a new setup without a proper config)
    let controllerConfig = {
        url: 'http://localhost:7474/db/data/transaction/commit',
        loadStartData: 'rootPackages',
        showLoadSpinner: true
    };

    // Count may jump from 300 to 100 because of the empty entites, like buildingSegments.
    // Loader is impacted by Neo4j requests and entity creation:
    let loaderController = {
        dataToLoad: 0,
        toLoad: 'toLoad',
        loaded: 'loaded'
    };


    function initialize() {
        // Override config with ones from setup
        if (setup.neo4jModelLoadConfig) {
            controllerConfig = {...controllerConfig, ...setup.neo4jModelLoadConfig}
        }
        events.loaded.on.subscribe(loadElementsAndChangeState);
        // events.wasChecked.on.subscribe(onNodeCheck);

        if (controllerConfig.showLoadSpinner) {
            createLoadSpinner();
        }
    };


    // Spinner to show loading process of entites 
    // starts with loading from Neo4j and ends with creating a Model in AframeCanvasManipulator
    // Loader is added to a new container on top of everything
    function createLoadSpinner() {
        let loader = document.createElement('div');
        loader.id = 'loaderController';
        loader.className = 'hidden';
        loader.innerHTML = '<div class="loaderController-roller"><div class="loader">Loading...</div><div id="loaderController-data"></div>';
        document.body.appendChild(loader);
    }

    function updateLoadSpinner(payload) { // payload = toLoad || loaded;
        if (!controllerConfig.showLoadSpinner) {
            return;
        }
        let loader = document.getElementById('loaderController');
        payload === loaderController.toLoad ? loaderController.dataToLoad ++ : loaderController.dataToLoad --
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
        // Select root packages ('rootPackages')
        let loadOneChild = true;
        let cypherQuery = `MATCH (p:Package) WHERE NOT (:Package)-[:CONTAINS]->(p) RETURN p`;

        if (controllerConfig.loadStartData === 'everything') {
            loadOneChild = false;
            cypherQuery = `MATCH (p:Package) RETURN p`;
        }

        let parentNodes = await getMetadataForParentNodes(cypherQuery);
        let childNodes = await getMetadataForChildNodes(parentNodes, loadOneChild);
        model.createEntities([...parentNodes, ...childNodes]);
    }


    // Returning parent nodes metadata, prepared to be inserted into model.js
    async function getMetadataForParentNodes(cypherQuery) {
        let response = await getNeo4jData(cypherQuery);
        let data = await getMetadataFromResponse(response);        
        return data;
    }


    // Get child nodes metadata + one or all child node of this node.
    async function getMetadataForChildNodes(parentNodes, limitOne) {
        updateLoadSpinner(loaderController.toLoad);
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

        updateLoadSpinner(loaderController.loaded);
        return childNodesMetadata;
    }


    function onNodeCheck(entity) {
        entity.wasChecked = true;
        let data = loadNodesRecursively(entity)
    }

    async function loadNodesRecursively(entity) {
        // Exclude already loaded nodes from query
        let whereStatement = `child.hash = ""`;
        if (entity.children) {
            entity.children.forEach(child => {
                // If was already checked, we don't need these node anymore
                if (child.wasChecked) {
                    return whereStatement += `OR child.hash = "${child.id}"`;
                }
            });
        }

        // Kinderknoten ziehen (direkte Kinder)
        const cypherQuery = `MATCH (parent)-[:DECLARES|HAS|CONTAINS]->(child)
                             WHERE parent.hash = "${entity.id}" 
                             AND EXISTS(child.hash) 
                             AND NOT (${whereStatement})
                             RETURN child`;
        let response = await getNeo4jData(cypherQuery);
        let data = await getMetadataFromResponse(response);         

        // For optimization, bulk load of entities
        let entitiesToLoad = [];
        data.forEach(entry => {
            if (!model.getEntityById(entry.id)) {
                return entitiesToLoad.push(entry);
            } 
        });
        model.createEntities(entitiesToLoad);
        
        // Find next nodes
        data.forEach(entry => {
            return loadNodesRecursively(entry);
        });
    }


    // Get MetaData for Child node on node expand
    async function onNodeExpand(entity) {
        entity.wasExpanded = true;
        let parentNodes = await getNeo4jChildNodes(entity.id, false); // parent nodes inside of the selected node
        let childNodes = await getMetadataForChildNodes(parentNodes, true); // select single child to show the expand sign
        model.createEntities([...parentNodes, ...childNodes]);
    };


    // Get MetaData for Child node
    async function getNeo4jChildNodes(parentNodeHash, limitOne = false) {
        let limit = '';
        if (limitOne) {
            limit = `LIMIT 1`;
        }
        const cypherQuery = `MATCH (parent)-[:DECLARES|HAS|CONTAINS]->(child) WHERE parent.hash = "${parentNodeHash}" AND EXISTS(child.hash) RETURN child ${limit}`;

        let response = await getNeo4jData(cypherQuery);
        let data = await getMetadataFromResponse(response);
        return data;
    }


    // Return metadata object from response
    async function getMetadataFromResponse(response) {
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
        // Prepare cypherQuery with all the nodes ids
        let whereStatement = null;
        applicationEvent.entities.forEach(entity => {
            if (whereStatement == null) {
                return whereStatement = `n.nodeHashId = "${entity.id}"`;
            }
            return whereStatement += ` OR n.nodeHashId = "${entity.id}"`;
        });

        if (!whereStatement) {
            return;                
        }
        const cypherQuery = `MATCH (n) WHERE ${whereStatement} AND EXISTS(n.aframeProperty) RETURN n`;

        updateLoadSpinner(loaderController.toLoad); // firstly add one, because of the getNeo4jData
        let response = await getNeo4jData(cypherQuery);
        if (!response) {
            return;
        }

        let elements = response[0].data;
        for (element of elements) {
            updateLoadSpinner(loaderController.toLoad); // second add, because of the appendAframeElementWithProperties
            // There may be some empty entites, like buildingSegments. They don't have any data, so we can't create an element for them.
            if (element) {
                canvasManipulator.appendAframeElementWithProperties(element); // Start drawing the element
            } else {
                updateLoadSpinner(loaderController.loaded);
            }
        }
        updateLoadSpinner(loaderController.loaded);
    }


    // Universal method to load a data from Neo4j using imported cypher-query
    async function getNeo4jData(cypherQuery) {
        const payload = {
            'statements': [
                // neo4j requires keyword "statement", so leave as is
                { 'statement': `${cypherQuery}` }
            ]
        }

        try {
            let response = await fetch(controllerConfig.url, {
                method: 'POST', 
                body: JSON.stringify(payload), 
                headers: {
                    'Content-Type': 'application/json'
                }
            });
    
            let data = await response.json();
            return data.results;
        } catch (error) {
            console.error(error);
        }
    }


    return {
        initialize: initialize,
        loadStartMetaData: loadStartMetaData,
        updateLoadSpinner: updateLoadSpinner,
        loaderController: loaderController,
        onNodeExpand: onNodeExpand,
        onNodeCheck: onNodeCheck
    };
})();