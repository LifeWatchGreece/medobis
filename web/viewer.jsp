<!DOCTYPE html> 
<html lang="en">
    <head> 
        <jsp:include page="/template/head.jsp"><jsp:param name="baseUrl" value="${baseUrl}" /></jsp:include>
        <script type='text/javascript'>
            var innerWidth = document.documentElement.clientWidth || document.body.clientWidth || window.innerWidth;
            var innerHeight = document.documentElement.clientHeight || document.body.clientHeight || window.innerHeight;
        </script>
        
        <!-- HEAD: YOUR CODE GOES HERE -->
        <title>MedOBIS Viewer</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="shortcut icon" href="./favicon.ico">
        <script type="text/javascript" src="https://maps.google.com/maps/api/js?v=3.6&amp;sensor=false"></script>                     

        <!-- Ext resources -->
        <script type="text/javascript" src="${baseUrl}/libs/ext/adapter/ext/ext-base.js"></script>
        <script type="text/javascript" src="${baseUrl}/libs/ext/ext-all-debug.js"></script>    
        <link rel="stylesheet" type="text/css" href="${baseUrl}/libs/ext/resources/css/ext-all.css">

        <!-- OpenLayers resources -->
        <link rel="stylesheet" type="text/css" href="${baseUrl}/libs/openlayers/theme/default/style.css">
        <script type="text/javascript" src="${baseUrl}/libs/openlayers/lib/OpenLayers.js"></script>

        <!-- GeoExt resources -->
        <script type="text/javascript" src="${baseUrl}/libs/geoext/lib/GeoExt.js"></script>    

        <!-- gxp resources -->
        <link rel="stylesheet" type="text/css" href="${baseUrl}/libs/gxp/theme/all.css">
        <script type="text/javascript" src="${baseUrl}/libs/gxp/script/loader.js"></script>   
        
        <!-- ux resources -->
        <script type="text/javascript" src="${baseUrl}/libs/ux/RowExpander.js"></script>    
        <script type="text/javascript" src="${baseUrl}/libs/ux/FileUploadField.js"></script> 
        
        <!-- Custom styling resources -->
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/custom.css">        
        <script type="text/javascript" src="${baseUrl}/js/proj4js-compressed.js"></script>
        
        <!-- My application -->
        <script type="text/javascript" src="${baseUrl}/js/app2.js"></script>           

        <!-- HEAD: END OF YOUR CODE -->
    </head> 
    <body>  
        <jsp:include page="/template/body_top.jsp"><jsp:param name="baseUrl" value="${baseUrl}" /></jsp:include>        
        <!-- BODY: YOUR CODE GOES HERE -->
        <script type='text/javascript'>
            // The main_area_div exists in the $body_top code that comes from portal
            // We want to apply the following CSS only to the viewer page
            $('#main_area_div').css('width',innerWidth-20);
            $('#main_area_div').css('position','absolute');
            $('#main_area_div').css('left',10);            
        </script>
        <div id="geowrapper" style="width: 100%"></div>
        <!-- BODY: END OF YOUR CODE -->
        <jsp:include page="/template/body_bottom.jsp"><jsp:param name="baseUrl" value="${baseUrl}" /></jsp:include>
    </body>
</html>

