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
            let data = await loadNodeById(nodeId);
            let results = data.results;
            // Aframe code property is nested, so get it out
            results.forEach(result => {
                if (result.data[0]) {
                    console.log(result.data[0]);
                    let aframeProperty = result.data[0].row[0].aframeProperty;
                    let aframeObject = JSON.parse(`{ ${aframeProperty} }`);
                    console.log(aframeObject);
                    


                    let aframe_code = result.data[0].row[0].aframe_code;
                    if (aframe_code) {
                        // $('a-scene a-entity#camera').after(aframe_code);  
                        model.changeIsLoadedStatus(nodeId);      
                        // Tests
                        let entity = document.getElementById(nodeId);
                        // entity.originalColor = component.getAttribute("color");

                        // Second option
                        let sceneEl = document.querySelector('a-scene');
                        // AFRAME.registerComponent('do-something-once-loaded', {
                        //     init: function () {
                        //       // This will be called after the entity has properly attached and loaded.
                        //       console.log('I am ready!');
                        //       console.log(this);                              
                        //     }
                        // });
                        let boxTag = 'a-box';
                        let entityEl = document.createElement(`${boxTag}`);
                        // entityEl.setAttribute('do-something-once-loaded', '');
                        entityEl.setAttribute('geometry', {
                            primitive: 'box',
                            height: 3,
                            width: 1
                          });
                        
                        let color = sceneEl.getAttribute('color');
                        sceneEl.appendChild(entityEl);

                        // If any connection is shown and all the elements are transparent, apply the transparency to a new element.
                        // if (model.getEntityById(nodeId).isTransparent) {
                        //     let entities = [];
                        //     entities.push(document.getElementById(nodeId));
                        //     let transparencyValue = relationTransparencyController.getControllerConfig().fullFadeValue;
                        //     canvasManipulator.changeTransparencyOfEntities(entities, transparencyValue); 
                        // }
                    }
                }
            }) 
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

    return {
        initialize: initialize,
        checkAndLoadNodeById: checkAndLoadNodeById
    };
})();