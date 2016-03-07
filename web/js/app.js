/**
 * @require plugins/Kml.js
*/

var app;
Ext.onReady(function() {
    
    app = new gxp.Viewer({
        // Configuration object for the wrapping container of the viewer. 
        // This will be an Ext.Panel if it has a renderTo property, or an Ext.Viewport otherwise.
        portalConfig: {
            renderTo: document.getElementById('geowrapper'),
            layout: "border",
            width: 950,
            height: 600,
            
            // by configuring items here, we don't need to configure portalItems
            // and save a wrapping container
            items: [{
                // a TabPanel with the map and a dummy tab
                id: "centerpanel",
                xtype: "tabpanel",
                region: "center",
                activeTab: 0, // map needs to be visible on initialization
                border: true,
                items: ["mymap", {title: "Dummy Tab"}]
            }, {
                // container for the FeatureGrid
                id: "south",
                xtype: "container",
                layout: "fit",
                region: "south",
                height: 150
            }, {
                // container for the queryform
                id: "west",
                xtype: "container",
                layout: "fit",
                region: "west",
                width: 200
            }],
            bbar: {id: "mybbar"}
        },
        
        // configuration of all tool plugins for this application
        // Plugin documentation can be found here:  http://gxp.opengeo.org/master/doc/
        // For each tool:
        //      actionTarget = where to place tool's action element (e.g button, menu,...)
        //      outputTarget = where to place tool's output container
        //
        //     the default for actionTarget is "map.tbar"
        tools: [
            {
                ptype: "gxp_loadingindicator"   // A static plugin to display a loading indicator for the map
             }, {
                 ptype: "gxp_layertree",        // Allow us to add a tree of layers to the gxp.Viewer and provides a context menu on layer nodes.
                 outputConfig: {
                     id: "tree",
                     border: true,
                     tbar: [] // A top bar for this plugon. we will add buttons to "tree.bbar" later
                 },
                 outputTarget: "west"
             }, {
                 ptype: "gxp_addlayers",        // A plugin to add layers to the map
                 actionTarget: "tree.tbar"      // Place it on the top bar of layertree plugin
             }, {
                 ptype: "gxp_removelayer",                          // A plugin to remove a layer from the map
                 actionTarget: ["tree.tbar", "tree.contextMenu"]    // Place it on the top bar of layertree plugin
             }, {
                 ptype: "gxp_layerproperties",  // Shows the properties of a selected layer from the map.
                 actionTarget: ["tree.tbar", "tree.contextMenu"]
             }, {
                 ptype: "gxp_zoomtoextent",     // Provides an action for zooming to an extent.
                 actionTarget: "map.tbar"
             }, {
                 ptype: "gxp_zoom",             // Provides actions for box zooming, zooming in and zooming out.
                 actionTarget: "map.tbar"
             }, {
                 ptype: "gxp_navigationhistory",    // Provides two actions for zooming back and forth.
                 actionTarget: "map.tbar"
             },{
                ptype: "gxp_styler",
                actionTarget: ["tree.tbar", "tree.contextMenu"]
             },{
                 ptype: "gxp_wmsgetfeatureinfo",    // This plugins provides an action which, when active, will issue a GetFeatureInfo request 
                                                    // to the WMS of all layers on the map. The output will be displayed in a popup.
                 outputConfig: {
                     width: 400,
                     height: 250
                 },
                 actionTarget: "map.tbar", 
                 toggleGroup: "layertools"
             }, {
                 ptype: "gxp_mapproperties",        // Shows the properties of the map
                 outputConfig: {
                     title: 'Map properties'
                 }
             },{
                ptype: "gxp_legend",
                outputConfig: {
                            autoScroll: true
                        },
                actionTarget: "map.tbar"
             },{
                 ptype: "gxp_featuremanager",       // Plugin for a shared feature manager that other tools (feature editing, grid and querying) can reference.
                                                    // Works on layers added by the gxp.plugins.WMSSource plugin
                 id: "featuremanager",
                 maxFeatures: 20
             }, {
                 ptype: "gxp_featureeditor",        // Plugin for feature editing. Requires a gxp.plugins.FeatureManager.
                 featureManager: "featuremanager",
                 autoLoadFeature: true, // no need to "check out" features
                 outputConfig: {panIn: false},
                 toggleGroup: "layertools"
             }, {
                 ptype: "gxp_featuregrid",          // Plugin for displaying vector features in a grid. Requires a gxp.plugins.FeatureManager. 
                 featureManager: "featuremanager",
                 outputConfig: {
                     id: "featuregrid"
                 },
                 outputTarget: "south"
             }, {
                 ptype: "gxp_queryform",            // Plugin for performing queries on feature layers 
                 featureManager: "featuremanager",
                 outputConfig: {
                     title: "Query",
                     width: 320
                 },
                 actionTarget: ["featuregrid.bbar", "tree.contextMenu"],
                 appendActions: false
             }, {
                ptype: "gxp_kml",
                actionTarget: "map.tbar"
            },{
                 // not a useful tool - just a demo for additional items
                 actionTarget: "mybbar", // ".bbar" would also work
                 actions: [{text: "Click me - I'm a tool on the portal's bbar"}]
        }],
        
        // Layer source configurations for this viewer, keyed by source id. The source id 
        // will be used to reference the layer source in the layers array of the map object.
        defaultSourceType: "gxp_wmssource",
        sources: {
            opengeo: {
                url: "http://gxp.opengeo.org/geoserver/wms",
                version: "1.1.1"
            },
            osm: {
                ptype: "gxp_osmsource"
            },
            mygeo: {
                url: "http://10.1.6.192/geoserver/meddemo/wms",
                version: "1.1.1"
            },
            ol: {
                ptype: "gxp_olsource"
            }
        },
        
        // map and layers
        map: {
            id: "mymap", // id needed to reference map in portalConfig above
            title: "Map",
            projection: "EPSG:900913",
            units: "m",
            maxExtent: [-20037508.34, -20037508.34, 20037508.34, 20037508.34],
            center: [-10764594.758211, 4523072.3184791],
            zoom: 3,
            controls: [
                new OpenLayers.Control.Zoom(),
                new OpenLayers.Control.Attribution(),
                new OpenLayers.Control.Navigation()
            ],
            layers: [{
                source: "osm",
                name: "mapnik",
                group: "background"
            },{
                source: "ol",
                type: "OpenLayers.Layer",
                args: ["Blank"],
                visibility: false,
                group: "background"
            },{
                source: "opengeo",
                name: "usa:states",
                title: "States, USA - Population",
                queryable: true,
                bbox: [-13884991.404203, 2870341.1822503, -7455066.2973878, 6338219.3590349],
                selected: true
            },{
                source: "mygeo",
                name: "recordpos2"
            }]
        }
    });
    
    
    
});
