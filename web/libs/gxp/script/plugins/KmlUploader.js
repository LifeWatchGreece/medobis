/**
 * @requires plugins/Tool.js
 * @include Ext/examples/ux/fileuploadfield/FileUploadField.js
 */

Ext.namespace("gxp.plugins");

gxp.plugins.KmlUploader = Ext.extend(gxp.plugins.Tool, {

    ptype: "gxp_kmluploader", // Abbreviation of plugin component type
    baseUrl: "http://"+window.location.host,
    uploadUrl: "http://"+window.location.host+"/uploadKml",
    waitMsgText: "Loading...",
    actionConfig: null,

    addActions: function() {

        // We define the file selection button 
        var button = new Ext.ux.form.FileUploadField(Ext.apply({
            buttonOnly: true,  // Display the button with no visible text field
            buttonText: "<div class='gxp-icon-uploadkmlfile' style='padding-left: 20px; padding-top: 1px; height: 18px; display: inline-block'>Upload KML</div>",  // The text on the button
            cls: "kmluploadbutton",
            name: "file",
            listeners: {
                fileselected: Ext.createDelegate(this.uploadSelectedFile, this)
            }
        }, this.actionConfig));
        
        // We define the form that contains only the file selection button
        this.form = new Ext.form.FormPanel({
            unstyled: true,    // Removes any extra form styling
            fileUpload: true,  // is required for forms that upload files (multipart/form)
            hideLabels: true,  // Hide the form labels
            items: button,     // The only form item is the file selection button
            width: "auto"
        });

        return gxp.plugins.KmlUploader.superclass.addActions.apply(this, [this.form]);

    },

    uploadSelectedFile: function(button, value) {
        
        map1 = this.target.mapPanel.map;
        kmlFilename = value;
        getKmlUrl = this.baseUrl+'/getKmlFile/'+kmlFilename;
        
        // Submit the form through AJAX
        this.form.getForm().submit({
            url: this.uploadUrl,
            waitMsg: this.waitMsgText,
            success: function(formPanel, action) {                  
                   
                // If KML file uploaded successfully, retrieve its contents and 
                // create a layer that displays its contents
                Ext.Ajax.request({
                    url: getKmlUrl,
                    method: "GET",

                    success: function(response) {
                        
                        // Get the KML file content (XML)
                        var kmlString = response.responseText;
                        
                        // Create a new vector layer
                        var kmllayer = new OpenLayers.Layer.Vector(kmlFilename);

                        // Create a KML parser
                        var kmlformat = new OpenLayers.Format.KML({
                            'internalProjection': map1.baseLayer.projection,
                            'externalProjection': new OpenLayers.Projection("EPSG:4326") // map1.getProjectionObject()
                        });

                        // Parse KML string and return a list of features
                        var kmlfeatures = kmlformat.read(kmlString);
                        kmllayer.addFeatures(kmlfeatures);

                        // Add the vector layer to the map
                        map1.addLayer(kmllayer);
                        
                    },
                    failure: function(response) {
                        alert("No response");
                        alert(response.responseText);
                    }                              
                });                      
               
                // Reset the form where file selection button resides
                formPanel.reset();

            },
            failure: function (formPanel, action) {
                Ext.Msg.alert('Error',"File uploading failed!");
            }
        });
    }

});

Ext.preg(gxp.plugins.KmlUploader.prototype.ptype, gxp.plugins.KmlUploader);
