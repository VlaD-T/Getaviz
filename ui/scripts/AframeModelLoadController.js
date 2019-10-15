var aframeModelLoadController = (function () {

    let controllerConfig = {
    };

    function initialize(setupConfig) {
        application.transferConfigParams(setupConfig, controllerConfig);
    };

    // AFRAME.registerComponent('set-aframe-attributes', {
    //     // schema defines properties of element
    //     schema: {
    //         tag: { type: 'string', default: '' },
    //         id: { type: 'string', default: '' },
    //         class: { type: 'string', default: '' },
    //         position: { type: 'string', default: '' },
    //         width: { type: 'string', default: '' },
    //         height: { type: 'string', default: '' },
    //         depth: { type: 'string', default: '' },
    //         color: { type: 'string', default: '' },            
    //         shader: { type: 'string', default: '' },
    //         flatShading: { type: 'string', default: '' }
    //     },

    //     init: function () {
    //         // This will be called after the entity has properly attached and loaded.
    //         this.attrValue = ''; // imported payload is in this.data, so we don't need this one
    //         Object.keys(this.schema).forEach(key => {
    //             this.el.setAttribute(`${key}`, this.data[key]);                
    //         })

    //         model.changeIsLoadedStatus(this.data.id); // set to loaded, so that we don't create this element again
    //         // // if element must be transparent, apply transparency method
    //         // if (model.getEntityById(this.data.id).mustBeTransparent) {
    //         //     let entities = [];
    //         //     entities.push(document.getElementById(this.data.id));
    //         //     let transparencyValue = relationTransparencyController.getControllerConfig().fullFadeValue;
    //         //     canvasManipulator.changeTransparencyOfEntities(entities, transparencyValue); 
    //         // }
    //     }
    // });

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
                    let aframeProperty = result.data[0].row[0].aframeProperty;
                    let aframeObject = JSON.parse(`${aframeProperty}`); // create an object
                    
                    let entityEl = document.createElement(`${aframeObject.tag}`);
                    entityEl.setAttribute('set-aframe-attributes', aframeObject); // this attributes will be set after element is created
                    let sceneEl = document.querySelector('a-scene');
                    sceneEl.appendChild(entityEl);
                }
            }) 
        } catch (error) {
            console.error(error);
        }
    }

    async function checkIfElementIsAlreadyLoaded(nodeId) {
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