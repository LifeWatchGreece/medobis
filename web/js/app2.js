/**
 * @require plugins/KmlUploader.js
 * @require widgets/Viewer.js
 * @require widgets/CrumbPanel.js
 * @require plugins/WMSGetFeatureInfo.js
 * @require plugins/FeatureGrid.js
 * @require plugins/SaveWmsLayer.js
*/

var app;

Ext.onReady(function() {        
    
    function buildMousePosition(){
        // Let's add the hosting DIV at the top bar, using plain javascript
        var coordsParent = document.querySelector('#mymap .x-toolbar-ct .x-toolbar-left');
        var coordsDiv = document.createElement('div');
        coordsDiv['id'] = 'coordsDiv'; 
        coordsDiv['style'] = 'width:280px; height:30px; float: right; font-size: 12px; text-align: right; position: absolute; top: 3px; right:0px; padding-top: 3px; padding-right: 10px';
        coordsParent.appendChild(coordsDiv);

        // Create control and place it to the hosting DIV
        mouseCoords = new OpenLayers.Control.MousePosition({
                            id: "coord_mouse",
                            formatOutput: function(lonLat){                                                                                             
                                var source = new Proj4js.Proj('EPSG:900913');    
                                var dest = new Proj4js.Proj('EPSG:4326');     
                                var p = new Proj4js.Point(lonLat.lon,lonLat.lat);   
                                Proj4js.transform(source, dest, p);     
                                var longtitude = parseFloat(p.x).toFixed(7);
                                var latitude = parseFloat(p.y).toFixed(7);
                                var padding = "";
                                if((longtitude > 10)&&(longtitude<100)){
                                    padding = padding + " ";
                                } else if(longtitude < 10){
                                    padding = padding + "  ";
                                }
                                if((latitude > 10)&&(latitude<100)){
                                    padding = padding + " ";
                                } else if(latitude < 10){
                                    padding = padding + "  ";
                                }
                                if(longtitude > 0 ){
                                    padding = padding + " ";
                                }
                                if(latitude > 0 ){
                                    padding = padding + " ";
                                }
                                return "<pre style='background-color: ; border: 0px'>Lat/Long: "+padding+latitude+" , "+longtitude+" </pre>";
                            },
                            div: document.getElementById('coordsDiv'),
                        });
                        
        // Add the control to the map
        app.mapPanel.map.addControl(mouseCoords);
    }
    
    Proj4js.defs["EPSG:900913"] = "+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +no_defs";                        
    Proj4js.defs["EPSG:4326"] = "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs"; 
    
    app = new gxp.Viewer({
        
        // In order to create the appropriate DIV (which wil be used to host 
        // the control) in the top bar, the Viewer object should have been created
        // and the Viewer's HTML should have been rendered. So, we need to use
        // the 'ready' event
        listeners: {
                     'ready': buildMousePosition
                 },
        
        // Configuration object for the wrapping container of the viewer. 
        // This will be an Ext.Panel if it has a renderTo property, or an Ext.Viewport otherwise.
        portalConfig: {
            renderTo: document.getElementById('geowrapper'),
            layout: "border",
            height: (innerHeight-270), // 150px are needed for the 3 panel-headings, their margins and some text content
            
            // by configuring items here, we don't need to configure portalItems
            // and save a wrapping container
            items: [{
                // a TabPanel with the map
                id: "centerpanel",
                xtype: "tabpanel",
                region: "center",
                activeTab: 0, // map needs to be visible on initialization
                border: true,
                items: ["mymap"]
            }, {
                // container for the FeatureGrid
                id: "south",
                xtype: "gxp_crumbpanel",
                region: "south",
                height: 150,
                collapsible: true,
                collapseMode: "mini",
                collapsed: true,
                hideCollapseTool: true,
                split: true,
                border: true
            }, {
                // container for the queryform
                id: "west",
                xtype: "container",
                layout: "fit",
                region: "west",
                width: 250
            }],
            bbar: {id: "mybbar"}
        },        
        
        tools: [
            {
                 ptype: "gxp_layertree",        // Allow us to add a tree of layers to the gxp.Viewer and provides a context menu on layer nodes.
                 outputConfig: {
                     id: "tree",
                     border: true,
                     tbar: [] // A top bar for this plugon. we will add buttons to "tree.bbar" later
                 },
                 outputTarget: "west"
             }, {
                 ptype: "gxp_addviewerlayers",         // A plugin to add layers to the map
                 actionTarget: "tree.tbar",      // Place it on the top bar of layertree plugin
                 listeners: {
                     'wmslayeradded': function(aa,layerName){
                         //alert(layerName);
                     }
                 }
             }, {
                 ptype: "gxp_removeviewerlayer",                          // A plugin to remove a layer from the map
                 actionTarget: ["tree.tbar", "tree.contextMenu"],    // Place it on the top bar of layertree plugin
                 listeners: {
                     'wmslayerremoved': function(layerName){
                         //alert(layerName);
                     }
                 }
             }, {
                 ptype: "gxp_zoomtoextent",     // Provides an action for zooming to an extent.
                 actionTarget: "map.tbar"
             },{
                ptype: "gxp_zoomtolayerextent",
                actionTarget: ["tree.tbar", "tree.contextMenu"]
            }, {
                 ptype: "gxp_zoom",             // Provides actions for box zooming, zooming in and zooming out.
                 actionTarget: "map.tbar"
             }, {
                 ptype: "gxp_navigationhistory",    // Provides two actions for zooming back and forth.
                 actionTarget: "map.tbar"
             },{
                 ptype: "gxp_featuremanager",       // Plugin for a shared feature manager that other tools (feature editing, grid and querying) can reference.
                                                    // Works on layers added by the gxp.plugins.WMSSource plugin
                 id: "featuremanager",
                 autoLoadFeatures: true,
                 paging: true, 
                 autoSetLayer: true,
                 maxFeatures: 200
             },{
                 ptype: "gxp_featuregrid",          // Plugin for displaying vector features in a grid. Requires a gxp.plugins.FeatureManager. 
                 id: "featuregrid",
                 featureManager: "featuremanager",
                 showTotalResults: true,
                 outputConfig: {
                     loadMask: true
                 },
                 outputTarget: "south"
             },{
                ptype: "gxp_loadingindicator",
                loadingMapMessage:"Loading..."
            },{
                ptype: "gxp_googlegeocoder",
                outputTarget: "map.tbar",
                outputConfig: {
                    emptyText: "Search for a location ..."
                }
            },{
                 ptype: "gxp_wmsgetfeatureinfo",    // This plugins provides an action which, when active, will issue a GetFeatureInfo request 
                                                    // to the WMS of all layers on the map. The output will be displayed in a popup.
                 outputConfig: {
                     width: 400,
                     height: 250
                 },
                 actionTarget: "map.tbar", 
                 toggleGroup: "layertools"
             },{
                ptype: "gxp_legend",
                outputConfig: {
                            autoScroll: true
                        },
                actionTarget: "map.tbar"
             },{
                 ptype: "gxp_queryform",            // Plugin for performing queries on feature layers 
                 featureManager: "featuremanager",
                 outputConfig: {
                     title: "Query",
                     width: 320
                 },
                 actionTarget: "map.tbar",
            },{
                ptype: "gxp_kmluploader",
                actionTarget: "map.tbar"
            },{
                ptype: "gxp_savewmslayer",
                id: "savewmslayertool",
                actionTarget: "map.tbar",
                listeners: {
                    'globallayeradded': function(layerName,layerObj){
                            var targetBar = Ext.getCmp('mybbar');
                            var splitButton = Ext.getCmp('layersavemenu');
                            var parts = layerName.split(':');
                            var geoWorkspace = parts[0];
                            var geoLayer = parts[1];
                            
                            // If there is no WMS layer in the dropdown, remove the "No layer found" message
                            var count = splitButton.menu.items.getCount();
                            if (count == 1)
                                splitButton.menu.remove('nofound');
                            
                            splitButton.menu.add({
                                text: layerName, 
                                id: 'L:'+layerName,
                                menu: {
                                    xtype:'menu',
                                    floating: true,
                                    items: [
                                        {
                                            text: 'CSV',
                                            href: 'http://'+window.location.host+':8080/geoserver/'+geoWorkspace+'/ows?service=WFS&version=1.0.0&request=GetFeature&typeName='+geoWorkspace+'%3A'+geoLayer+'&outputformat=csv',
                                            hrefTarget: 'blank'
                                        },{
                                            text: 'KML',
                                            href: 'http://'+window.location.host+':8080/geoserver/'+geoWorkspace+'/wms/kml?layers='+layerName,                                            
                                            hrefTarget: 'blank'
                                        }
                                    ]
                                }
                            });
                            targetBar.doLayout();
                    },
                    'globallayerremoved': function(layerName){
                        var targetBar = Ext.getCmp('mybbar');
                        var splitButton = Ext.getCmp('layersavemenu');
                        splitButton.menu.remove('L:'+layerName);
                        
                        var count = splitButton.menu.items.getCount();
                        if (count == 0)
                            splitButton.menu.add({
                                text: 'No layer found', 
                                id: 'nofound',                                
                            });
                    }
                }
            }  
        ],
        
        // Layer source configurations for this viewer, keyed by source id. The source id 
        // will be used to reference the layer source in the layers array of the map object.
        defaultSourceType: "gxp_wmssource",
        sources: {
            google: {
		ptype: "gxp_googlesource"
            },
            osm: {
                ptype: "gxp_osmsource"
            },
            medobis: {
                url: "/geoserver/wms",
                version: "1.1.1"                
            },
            ol: {
                ptype: "gxp_olsource"
            }
        },
        
        // map and layers
        map: {
            id: "mymap", // id needed to reference map in portalConfig above
            title: "Mediterranean OBIS - Open Access to Marine Biodiversity Data",
            projection: "EPSG:900913",
            displayProjection:new OpenLayers.Projection("EPSG:4326"),
            units: "m",
            center: [2003446.44, 4474152.62],
            zoom: 3,
            //minScale: 200000,
            //maxScale: 100000000,
            //numZoomLevels: 20,
            //
            //maxExtent: new OpenLayers.Bounds(-180, -90, 180, 90),
            //maxExtent: new OpenLayers.Bounds(-180, -90, 180, 90),
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
                source: "google",
                name: "SATELLITE",
                group: "background"
           }],
            items: [{
                    xtype: "gx_zoomslider",
                    vertical: true,
                    height: 100
                },{
                    xtype: "gxp_scaleoverlay",
                    height: 50
            }]
        }
    });
    
    /*
    app.mapPanel.map.events.register('zoomend', app.mapPanel.map, function() {
        alert('Zoom level = '+app.mapPanel.map.getZoom());    
    });
    */
    
    
    //alert('1');
    // querySelector() returns the first element within the document that 
    // matches the specified group of selectors.
    //var targetEl = document.querySelector('#mymap .x-toolbar-ct .x-toolbar-left');
    //var topToolbar = Ext.select('#mymap .x-toolbar-ct .x-toolbar-left').first();
    //alert('2');
    //var $coordsDiv = $('<div/>').attr('id', 'coordsDiv');
    //$('#mymap .x-toolbar-ct .x-toolbar-left').append($coordsDiv);
    //Ext.DomHelper.insertFirst(topToolbar, {tag: 'div', id: 'coordsDiv'});               
    
});
/*
alert('1');
var targetEl = document.querySelector('#mymap .x-toolbar-ct .x-toolbar-left');
alert('2');
var div1 = document.createElement('div');
div1['id'] = 'coordsDiv';    
targetEl.appendChild(div1);

alert('3');
    mouseCoords = new OpenLayers.Control.MousePosition({
                        id: "coord_mouse",
                        formatOutput: function(lonLat){                                                                                             
                            var source = new Proj4js.Proj('EPSG:900913');    
                            var dest = new Proj4js.Proj('EPSG:4326');     
                            var p = new Proj4js.Point(lonLat.lon,lonLat.lat);   
                            Proj4js.transform(source, dest, p);     

                            return parseFloat(p.x).toFixed(7)+" , "+parseFloat(p.y).toFixed(7);
                        },
                        target: document.getElementById('coordsDiv'),
                    });
    alert('4');
    app.mapPanel.map.addControl(mouseCoords);
    
    */