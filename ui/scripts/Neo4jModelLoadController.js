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
        events.loaded.on.subscribe(changeStateAndLoadElements);
        events.loaded.off.subscribe(updateLoadSpinner);
        if (controllerConfig.showLoadSpinner) {
            createLoadSpinner();
        }
    };

    function createLoadSpinner() {
        let loader = document.createElement('div');
        loader.id = 'loaderController';
        loader.className = 'hidden';
        loader.innerHTML = '<div class="loaderController-roller"><div class="loader">Loading...</div><div id="loaderController-data"></div>';
        document.body.appendChild(loader);
    }

    function updateLoadSpinner(payload) { // payload = +- 1;
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

    async function getStartData() {
        let payload = {}
        if (controllerConfig.loadStartData === 'rootPackages') {
            console.log('Load root packages');
            payload = {
                'statements': [
                    // neo4j requires keyword "statement", so leave as is
                    { 'statement': `MATCH (p:Package) WHERE NOT (:Package)-[:CONTAINS]->(p) RETURN p` }
                    // { 'statement': `MATCH (p:Package) RETURN p` }
                ]
            }
        } else { // load all
            console.log('Load everything');
        }

        // If no elements found
        let response = await getNeo4jData(payload);
        let data = [];
        if (!response[0].data.length) {
            return data;
        }

        // Add all metadata information from response
        for (object of response[0].data) {
            let metadataProperty = object.row[0].metadata;
            let metadataObject = JSON.parse(`${metadataProperty}`); // create an object
            data.push(metadataObject);
        }
        return data; // proceed with Model.js
    };

    async function changeStateAndLoadElements(applicationEvent) {
        try {
            for (entity of applicationEvent.entities) {
                //Element must be in entities, but not in DOM. 
                let isInDOM = checkIfElementIsInDOM(entity.id);
                if (isInDOM) {
                    continue;
                }

                loaderApplicationEvent.value = 'toLoad'
                updateLoadSpinner(loaderApplicationEvent);

                let results = await loadNodeById(entity.id);
                for (result of results) {
                    // There may be some empty entites, like buildingSegments. They don't have any data, so we can't create an element for them.
                    if (result.data[0]) {
                        // console.log('append Element') // to test event loop
                        // Start drawing element
                        canvasManipulator.appendAframeElementWithProperties(result.data[0]);
                    } else {
                        loaderApplicationEvent.value = 'loaded'
                        updateLoadSpinner(loaderApplicationEvent);
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