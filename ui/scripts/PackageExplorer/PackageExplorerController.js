var packageExplorerController = (function () {

	let packageExplorerTreeID = "packageExplorerTree";
	let jQPackageExplorerTree = "#packageExplorerTree";

	let tree;

	let controllerConfig = {
		projectIcon: "scripts/PackageExplorer/images/project.png",
		packageIcon: "scripts/PackageExplorer/images/package.png",
		typeIcon: "scripts/PackageExplorer/images/type.png",
		fieldIcon: "scripts/PackageExplorer/images/field.png",
		methodIcon: "scripts/PackageExplorer/images/method.png",
		elementsSelectable: true
	};

	function initialize(setupConfig) {
		application.transferConfigParams(setupConfig, controllerConfig);
		events.loaded.on.subscribe(addTreeNode);
	}

	function activate(rootDiv) {
		//create zTree div-container
		let zTreeDiv = document.createElement("DIV");
		zTreeDiv.id = "zTreeDiv";

		let packageExplorerTreeUL = document.createElement("UL");
		packageExplorerTreeUL.id = packageExplorerTreeID;
		packageExplorerTreeUL.setAttribute("class", "ztree");

		zTreeDiv.appendChild(packageExplorerTreeUL);
		rootDiv.appendChild(zTreeDiv);

		//create zTree
		prepareTreeView();
		events.selected.on.subscribe(onEntitySelected);
	}

	function reset() {
		prepareTreeView();
	}

	function prepareTreeView() {
		let items = [];

		//zTree settings
		var settings = {
			check: {
				enable: controllerConfig.elementsSelectable,
				chkboxType: { "Y": "ps", "N": "s" }
			},
			data: {
				simpleData: {
					enable: true,
					idKey: "id",
					pIdKey: "parentId",
					rootPId: ""
				}
			},
			callback: {
				onCheck: zTreeOnCheck,
				onClick: zTreeOnClick,
				beforeExpand: zTreeBeforeExpand,

			},
			view: {
				showLine: false,
				showIcon: true,
				selectMulti: false
			}

		};

		//create zTree
		tree = $.fn.zTree.init($(jQPackageExplorerTree), settings, items);
	}


	function zTreeOnCheck(event, treeId, treeNode) {
		let entities = [];

		// First of all get all the child nodes, if this is first check for this node
		let wasChecked = model.getEntityById(treeNode.id).wasChecked;
		if (!wasChecked) {
			let applicationEvent = {
				sender: packageExplorerController,
				entity: treeNode
			}

			events.wasChecked.on.publish(applicationEvent);
		}

		// Process with changing check state
		let nodes = tree.getChangeCheckedNodes();
		nodes.forEach(function (node) {
			node.checkedOld = node.checked; //fix zTree bug on getChangeCheckedNodes
			let entity = model.getEntityById(node.id)
				entities.push(entity);
		});

		let applicationEvent = {
			sender: packageExplorerController,
			entities: entities
		};

		if (!treeNode.checked) {
			events.filtered.on.publish(applicationEvent);
		} else {
			events.filtered.off.publish(applicationEvent);
		}
	}

	function zTreeOnClick(treeEvent, treeId, treeNode) {
		var applicationEvent = {
			sender: packageExplorerController,
			entities: [model.getEntityById(treeNode.id)]
		};
		events.selected.on.publish(applicationEvent);
	}

	// Before expand load all child nodes and remove dummyForExpand state (entity state)
	function zTreeBeforeExpand(event, treeId, treeNode) {
		let entity = model.getEntityById(treeId.id)
		if (entity.wasExpanded) {
			return true; // true to expand the list
		}

		let applicationEvent = {
			sender: packageExplorerController,
			entity: entity
		};

		events.wasExpanded.on.publish(applicationEvent);
		tree.expandNode(treeId, true, false, true, false);
		return false; // tree.expandNode already opens the list, so return false to leave it open
	}

	// Currently called from Model.js 
	function addTreeNode(applicationEvent) {
		
		//build items for ztree
		let items = new Map();
		applicationEvent.entities.forEach(function (entity) {

			let item;

			if (entity.belongsTo === undefined) {
				//rootpackages
				if (entity.type !== "issue") {
					if (entity.type === "Namespace") {
						item = {
							id: entity.id,
							open: false,
							checked: false,
							parentId: "",
							name: entity.name,
							icon: controllerConfig.packageIcon,
							iconSkin: "zt"
						};
					} else {
						item = {
							id: entity.id,
							open: true,
							checked: false,
							parentId: "",
							name: entity.name,
							icon: controllerConfig.projectIcon,
							iconSkin: "zt"
						};
					}
				}
			} else {
				switch (entity.type) {
					case "Project":
						item = { id: entity.id, open: true, checked: false, parentId: entity.belongsTo.id, name: entity.name, icon: controllerConfig.projectIcon, iconSkin: "zt" };
						break;
					case "Namespace":
						item = { id: entity.id, open: false, checked: false, parentId: entity.belongsTo.id, name: entity.name, icon: controllerConfig.packageIcon, iconSkin: "zt" };
						break;
					case "Class":
						if (entity.id.endsWith("_2") || entity.id.endsWith("_3")) {
							break;
						};
						item = { id: entity.id, open: false, checked: false, parentId: entity.belongsTo.id, name: entity.name, icon: controllerConfig.typeIcon, iconSkin: "zt" };
						break;
					case "ParameterizableClass":
						item = { id: entity.id, open: false, checked: false, parentId: entity.belongsTo.id, name: entity.name, icon: controllerConfig.typeIcon, iconSkin: "zt" };
						break;
					case "Enum":
						item = { id: entity.id, open: false, checked: false, parentId: entity.belongsTo.id, name: entity.name, icon: controllerConfig.typeIcon, iconSkin: "zt" };
						break;
					case "EnumValue":
						item = { id: entity.id, open: false, checked: false, parentId: entity.belongsTo.id, name: entity.name, icon: controllerConfig.fieldIcon, iconSkin: "zt" };
						break;
					case "Attribute":
						item = { id: entity.id, open: false, checked: false, parentId: entity.belongsTo.id, name: entity.name, icon: controllerConfig.fieldIcon, iconSkin: "zt" };
						break;
					case "Method":
						item = { id: entity.id, open: false, checked: false, parentId: entity.belongsTo.id, name: entity.name, icon: controllerConfig.methodIcon, iconSkin: "zt" };
						break;

					default:
						events.log.warning.publish({ text: "FamixElement not in tree: " + entity.type });

						return;
				}
			}

			if (item !== undefined) {
				items.set(entity.id, item);
			}
		});

		// Sort by type and name (ASC)
		items = getSortedMap(items);

		// Add items to explorer. To build a proper tree make sure, that parent node is already there.
		items.forEach((value, key, index) => {
			// We don't need duplicates
			let nodeAlreadyAdded = tree.getNodeByParam("id", key, null);
			if (nodeAlreadyAdded) {
				return;
			}

			// Make sure parrent is already added, to make a proper tree
			// case 1: no parent. Means root package
			if (!value.parentId) {
				return tree.addNodes(null, value);
			}

			// case 2: has parent. Check if parrent is there. If not, add first. 
			if (value.parentId) {
				let parentAlreadyAdded = tree.getNodeByParam("id", value.parentId, null);
				if (!parentAlreadyAdded) {
					let parent = items.get(value.parentId);
					tree.addNodes(null, parent);
				}

				// Parent node is there, so now add child node.
				let parent = tree.getNodeByParam("id", value.parentId, null); // new get because of the possible insertion
				let newNodeIndex = -1; // per default add to the end of the list

				// In case there are child nodes in the current parent, find the right place to insert this new child-node
				let existingChildNodes = parent.children;
				if (existingChildNodes) {
					// We need only child nodes of this parent
					let sortExistingChildNodes = new Map();
					existingChildNodes.forEach(node => {
						sortExistingChildNodes.set(node.id, node); // insert existing child nodes
					});
					sortExistingChildNodes.set(key, value);	// insert current child node
					sortExistingChildNodes = getSortedMap(sortExistingChildNodes);

					// Array because we have to find the index
					let itemsArray = Array.from(sortExistingChildNodes.entries());
					for (let i = 0; i < itemsArray.length; i++) {
						let item = itemsArray[i];
						if (item[0] == key) { // item[0] because of the array. Represents the key. 
							newNodeIndex = i;
							break;
						}
					}
				}
				
				return tree.addNodes(parent, newNodeIndex, value, true);
			}
		});
	};

	// Sortierung nach Typ und Alphanumerisch
	function getSortedMap(items) {
		items = new Map([...items.entries()].sort(
			function (a, b) {

				// we have map, so "a" and "b" are arrays of key[0] and value[1]
				let entryA = a[1] // get value from entry
				let sortStringA = "";
				switch (entryA.icon) {
					case controllerConfig.packageIcon:
						sortStringA = "1" + entryA.name.toUpperCase();
						break;
					case controllerConfig.typeIcon:
						sortStringA = "2" + entryA.name.toUpperCase();
						break;
					case controllerConfig.fieldIcon:
						sortStringA = "3" + entryA.name.toUpperCase();
						break;
					case controllerConfig.methodIcon:
						sortStringA = "4" + entryA.name.toUpperCase();
						break;
					default:
						sortStringA = "0" + entryA.name.toUpperCase();
				}

				let entryB = b[1] // get value from entry
				let sortStringB = "";
				switch (entryB.icon) {
					case controllerConfig.packageIcon:
						sortStringB = "1" + entryB.name.toUpperCase();
						break;
					case controllerConfig.typeIcon:
						sortStringB = "2" + entryB.name.toUpperCase();
						break;
					case controllerConfig.fieldIcon:
						sortStringB = "3" + entryB.name.toUpperCase();
						break;
					case controllerConfig.methodIcon:
						sortStringB = "4" + entryB.name.toUpperCase();
						break;
					default:
						sortStringB = "0" + entryB.name.toUpperCase();
						break;
				}

				if (sortStringA < sortStringB) {
					return -1;
				}
				if (sortStringA > sortStringB) {
					return 1;
				}

				return 0;
			}
		));
		return items;
	}

	function onEntitySelected(applicationEvent) {
		if (applicationEvent.sender !== packageExplorerController) {
			var entity = applicationEvent.entities[0];
			var item = tree.getNodeByParam("id", entity.id, null);
			tree.selectNode(item, false);
		}
	}







	/*
    function zTreeOnCheck(event, treeId, treeNode) {
        		
		var treeObj = $.fn.zTree.getZTreeObj("packageExplorerTree");
        var nodes = treeObj.getChangeCheckedNodes();
        
		var entityIds = [];
		for(var i = 0; i < nodes.length;i++) {
			nodes[i].checkedOld = nodes[i].checked; //Need for the ztree to set getChangeCheckedNodes correct
			entityIds.push(nodes[i].id);
		}
		
		publishOnVisibilityChanged(entityIds, treeNode.checked, "packageExplorerTree");
		
    }

    function zTreeOnClick(event, treeId, treeNode) {        
		publishOnEntitySelected(treeNode.id, "packageExplorerTree");
    }
    
	
    function onEntitySelected(event, entity) {
        if(event.sender != "packageExplorerTree") {
			var tree = $.fn.zTree.getZTreeObj("packageExplorerTree");   
            var item = tree.getNodeByParam("id", entity.id, null);
            tree.selectNode(item, false);         
        }   
		interactionLogger.logManipulation("PackageExplorerTree", "highlight", entity.id);
    }
    
    function onVisibilityChanged(event, ids, visible) {
        if(event.sender != "packageExplorerTree") {            
			var tree = $.fn.zTree.getZTreeObj("packageExplorerTree");
            
			for(var i = 0; i < ids.length;i++) {
				var item = tree.getNodeByParam("id", ids[i], null);
				tree.checkNode(item, visible, false, false);
				item.checkedOld = item.checked;
			}
        }
		
    }
    
    function onRelationsVisibilityChanged(event, entities, visible) {
        var tree = $.fn.zTree.getZTreeObj("packageExplorerTree");
        for(var i = 0; i < entities.length; i++) {
            var id = entities[i];
            
			var item = tree.getNodeByParam("id", id, null);
            tree.checkNode(item, visible, false, false);
            item.checkedOld = item.checked;
			interactionLogger.logManipulation("PackageExplorerTree", "uncheck", id);
        }
    }
	*/

	return {
		initialize: initialize,
		activate: activate,
		reset: reset,
		addTreeNode: addTreeNode
	};
})();