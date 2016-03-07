/**
 * @requires plugins/Tool.js
 * ...
 */

// The namespace of our plugin
Ext.namespace("gxp.plugins");

gxp.plugins.SaveWmsLayer = Ext.extend(gxp.plugins.Tool, {

    // Some local variables that will be used as constants

    // Plugin class abbreviation (without using namespace)
    ptype: "gxp_savewmslayer",	

    // Here we define the UI elements that trigger the desired action and which function
    // handle each action.
    addActions: function() {
        var saveButton = new Ext.SplitButton({
            id: 'layersavemenu',
            text: 'Save WMS Layer',
            menu: new Ext.menu.Menu({
                items: [
                    {
                        text: 'No layer found', 
                        id: 'nofound'
                    }                                
                ]
            })
        });
        
        return gxp.plugins.SaveWmsLayer.superclass.addActions.apply(this, [saveButton]);                    
    },

});

Ext.preg(gxp.plugins.SaveWmsLayer.prototype.ptype, gxp.plugins.SaveWmsLayer);
 
