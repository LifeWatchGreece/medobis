/**
 * @requires plugins/Tool.js
 */

Ext.namespace("gxp.plugins");

gxp.plugins.PolygonFilter = Ext.extend(gxp.plugins.Tool, {

    ptype: "gxp_polyfilter", // Abbreviation of plugin component type
    baseUrl: "http://"+window.location.host+"/medobis2",
    uploadUrl: "http://"+window.location.host+"/medobis2"+"/uploadKml",
    buttonText: "Draw Polygon",
    waitMsgText: "Loading...",
    actionConfig: null,

    addActions: function() {

        gxpMap = this.target.mapPanel.map;
        featureManager = this.target.tools['featuremanager'];

        var actions = gxp.plugins.WMSGetFeatureInfo.superclass.addActions.call(this, [{
            tooltip: "Select features with polygon",
            iconCls: "gxp-icon-getfeatureinfo",
            buttonText: this.buttonText,
            toggleGroup: this.toggleGroup,
            enableToggle: true,
            allowDepress: true,
            toggleHandler: function(button, pressed) {
                if (pressed) {
                                        
                    // Let's define the drawing style of a vector
                    OpenLayers.Feature.Vector.style['default']['strokeWidth'] = '2';
                    
                    // Get the supported renderers (usually this array will be: ["SVG", "VML", "Canvas"] )
                    var renderer = OpenLayers.Util.getParameters(window.location.href).renderer;
                    renderer = (renderer) ? [renderer] : OpenLayers.Layer.Vector.prototype.renderers;
                    
                    // Create a vector layer
                    var vectors = new OpenLayers.Layer.Vector("Vector Layer", {
                        renderers: renderer
                    });
                    
                    // Define event handlers for the vector layer 
                    vectors.events.on({
                        'featureselected': function(feature) {
                            document.getElementById('counter').innerHTML = this.selectedFeatures.length;
                        },
                        'featureunselected': function(feature) {
                            document.getElementById('counter').innerHTML = this.selectedFeatures.length;
                        }
                    });
                    
                    // If we don't add the vector layer to the map, the vector will
                    // dissapear when we finish drawing it.
                    gxpMap.addLayer(vectors);
                    
                    // We define some vector drawing controls
                    drawControls = {
                        point: new OpenLayers.Control.DrawFeature(
                            vectors, OpenLayers.Handler.Point
                        ),
                        line: new OpenLayers.Control.DrawFeature(
                            vectors, OpenLayers.Handler.Path
                        ),
                        polygon: new OpenLayers.Control.DrawFeature(
                            vectors, OpenLayers.Handler.Polygon, {
                                eventListeners: {
                                    "featureadded": function(event){
                                        //var lastFeature = features[features.length - 1];
                                        //var lastFeatureId = lastFeature.id;
                                        //var polyGeometry = lastFeature.geometry;
                                        alert('Feature added!');
                                        alert(event.feature.geometry);                                        
                                        alert(gxpMap.layers.length);
                                        
                                        for(var i = 0; i < gxpMap.layers.length; i++){
                                            alert("ID = "+gxpMap.layers[i].id+" , NAME = "+gxpMap.layers[i].name+" , BASE = "+gxpMap.layers[i].isBaseLayer+" , InSwitcher = "+gxpMap.layers[i].displayInLayerSwitcher+" , Visibility = "+gxpMap.layers[i].visibility+" , CLass = "+gxpMap.layers[i].CLASS_NAME);
                                        }
                                        
                                        /*
                                        var pfilter = new OpenLayers.Filter.Spatial({
                                            type: OpenLayers.Filter.Spatial.INTERSECTS,
                                            value: event.feature.geometry
                                        });
                                        
                                        featureManager.featureStore.setOgcFilter(pfilter);
                                        
                                        for(var i = 0; i < gxpMap.layers.length; i++){
                                            alert('Refreshing...'+gxpMap.layers[i].name);
                                            gxpMap.layers[i].refresh();
                                        }
                                        */
                                        
                                        //alert(featureManager.featureStore.getCount());
                                        //alert(featureManager.featureLayer.name);
                                        //var countFeatures = featureManager.featureLayer.features.length;
                                        //alert(countFeatures);
                                        
                                        //alert(gxpMap.layers[4].features.length);
                                    }
                                }
                            }
                        ),
                        select: new OpenLayers.Control.SelectFeature(
                            vectors,
                            {
                                clickout: false, toggle: false,
                                multiple: false, hover: false,
                                toggleKey: "ctrlKey", // ctrl key removes from selection
                                multipleKey: "shiftKey", // shift key adds to selection
                                box: true
                            }
                        ),
                        selecthover: new OpenLayers.Control.SelectFeature(
                            vectors,
                            {
                                multiple: false, hover: true,
                                toggleKey: "ctrlKey", // ctrl key removes from selection
                                multipleKey: "shiftKey" // shift key adds to selection
                            }
                        )
                    };             
                    
                    // Add the vector drawing controls to the map
                    for(var key in drawControls) {
                        gxpMap.addControl(drawControls[key]);
                    }
                    
                    // Unselect features by clicking out ot them
                    drawControls.select.clickout = true;
                    
                    // Allows us to a define a selection box (by clicking and dragging 
                    // the mouse). All the features defined inside that box will be
                    // selected. If this capability is not enabled, when we click and drag
                    // outside a feature, the resulted action will be map dragging.
                    drawControls.select.box = true;
                    
                    var viewerBottomBar = Ext.getCmp('mybbar');
                    viewerBottomBar.add({
                            xtype: 'splitbutton',   // A dropdown field/button
                            text: 'Select User',
                            menu: new Ext.menu.Menu({
                                items: [
                                    {
                                        text: 'Select feature', 
                                        handler: function(){
                                            toggleControl('select');
                                        }
                                    },
                                    {
                                        text: 'None', 
                                        handler: function(){
                                            toggleControl('none');
                                        }
                                    },
                                    {
                                        text: 'Draw Point', 
                                        handler: function(){
                                            toggleControl('point');
                                        }
                                    },
                                    {
                                        text: 'Draw Line', 
                                        handler: function(){ 
                                            toggleControl('line');
                                        }
                                    },
                                    {
                                        text: 'Draw Polygon', 
                                        handler: function(){ 
                                            toggleControl('polygon'); 
                                        }
                                    }
                                ]
                            })
                        });
                    viewerBottomBar.doLayout(); // display
                    
                    function toggleControl(element) {
                        for(key in drawControls) {
                            var control = drawControls[key];
                            if(element == key) {
                                alert(element);
                                control.activate();
                            } else {
                                control.deactivate();
                            }
                        }
                    }
                    
                } else {
                    alert("Button is released!")
                }
             }
        }]);
    
        return gxp.plugins.PolygonFilter.superclass.addActions.apply(this, [actions]);

    },


});

Ext.preg(gxp.plugins.PolygonFilter.prototype.ptype, gxp.plugins.PolygonFilter);
