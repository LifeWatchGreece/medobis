<%-- 
    Document   : main
    Created on : Jun 16, 2014, 3:08:14 PM
    Authors    : Nikos Minadakis, Alexandros Gougousis
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>        
        <!-- HEAD: YOUR CODE GOES HERE -->
        <title>JSP Page</title>    
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jsp:include page="/template/head.jsp"><jsp:param name="baseUrl" value="${baseUrl}" /></jsp:include>
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/coords.css">            
        <!-- HEAD: END OF YOUR CODE -->
    </head>
    
    <body>
        <jsp:include page="/template/body_top.jsp"><jsp:param name="baseUrl" value="${baseUrl}" /></jsp:include>
        <!-- BODY: YOUR CODE GOES HERE -->                
        
        <div id="logo">
            <img src="${baseUrl}/images/logo_banner.png"/>
        </div>

        <div class="row">
            <div class="col-md-6">
                <!--  CONVERTING SINGLE COORDINATES:  START-->    
                <fieldset class="scheduler-border">
                    <legend class="scheduler-border">Transform coordinates </legend>

                    <form class="form-horizontal" action="${baseUrl}/coords/SimpleCoordinatesTransformer" method="get">

                        <div class="form-group">
                            <label for="latitude" class="col-sm-2 control-label">Latitude:</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" id="latitude" name="latitude">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="longitude" class="col-sm-2 control-label">Longitude</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" id="longitude" name="longitude">
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="col-sm-offset-2 col-sm-10">
                                <button type="submit" class="btn btn-default">Transform</button>
                            </div>
                        </div>

                    </form>
                    
                    <%                        
                        // If coordinates  format was invalid display an informative message.
                        if(request.getAttribute("invalidCoordinates") != null){
                            %>
                            <div class="alert alert-danger" role="alert">${invalidCoordinates}</div>
                            <%
                        // If coordinates were sent and format was not invalid display the results
                        } else if(request.getAttribute("latitude") != null) {
                            %>
                            <div style="padding: 5px; background-color: #EEEEEE">
                                <div style="font-weight:bold">Converted coordinates:</div>
                                <table>
                                    <tr>
                                        <td>Latitude: </td>
                                        <td> ${latitude}</td>
                                    </tr>
                                    <tr>
                                        <td>Longitude: </td>
                                        <td> ${longitude}</td>
                                    </tr>
                                </table>
                            </div>
                            <%
                        }
                    %>
                    
                </fieldset>
                <!--  CONVERTING SINGLE COORDINATES:  END-->        
            </div>
            <div class="col-md-6">
                <!--  CONVERTING COORDINATES FILE:  START -->    
                <fieldset class="scheduler-border">
                    <legend class="scheduler-border">Transform file </legend>

                    <div style="font-weight: bold; margin-bottom: 10px; color: gray">Step 1: Upload a file</div>
                    <div>
                        <form method="post" action="${baseUrl}/coords/file_upload" enctype="multipart/form-data">

                            <div class="row form-group">
                                <div class="col-sm-4">
                                        <!-- The file selector -->
                                        <span class="btn btn-default btn-file" style="width:100%">                                                
                                                Select a file...                                                
                                                <input type="file" name="file" />
                                        </span>
                                </div>
                                <div class="col-sm-8" id="files_to_upload">
                                        <!-- A field to display the selected file -->
                                        <input type="text" name="selected_file" class="form-control" disabled />
                                </div>
                            </div>
                            <div style="text-align: right; margin-top:5px" class="col-md-12">
                                <input type="submit" value="Upload File" class="btn btn-primary btn-xs" />
                            </div>                        
                        </form>
                    </div>
                    <%          
                        // If the encoding is not UTF-8, display a warning
                        if(request.getAttribute("fileEncoding") != null){
                            if(!request.getAttribute("fileEncoding").equals("UTF-8")){%>
                                <div style="clear: both"></div>
                                <div class="alert alert-warning"><strong>Warning:</strong> Looks like the encoding of this file is not UTF-8!</div>
                            <%}
                        }
                    %>

                    <%  
                        // If a file has just been uploaded, display a button for file convertion
                        if(request.getAttribute("status") != null){
                            if(request.getAttribute("status").equals("uploaded")){%>
                                <div style="font-weight: bold; margin-bottom: 10px; color: gray">Step 2: Convert the file</div>
                                <div>
                                    <form action="${baseUrl}/coords/CoordinatesTransformer" method="post">
                                        <div style="text-align: right; margin-top:5px" class="col-md-12">
                                            <input type="submit" value="Transform File" class="btn btn-primary btn-xs" />
                                        </div> 
                                    </form>    
                                </div>
                            <%}
                        }                                                    
                    %>                                        
                            
                    <%     
                        // If a file has just been converted, display a download button
                        if(request.getAttribute("status") != null){
                            if(request.getAttribute("status").equals("converted")){%>
                                <div style="font-weight: bold; margin-bottom: 10px; color: gray">Step 3: Download converted file</div>    
                                <div>   
                                    <form method="get" action="${baseUrl}/coords/file_download" enctype="multipart/form-data">
                                        <div style="text-align: right; margin-top:5px" class="col-md-12">
                                            <input id="downloadButton" type="submit" value="Download Converted File" class="btn btn-primary btn-xs" />
                                        </div>
                                    </form>                        
                                </div>  
                            <%}
                        }                                                    
                    %>                                           
                        
                </fieldset>
                <!--  CONVERTING COORDINATES FILE:  END -->   
            </div>
        </div>    
        <script type="text/javascript">
            
            // This function is related to the display of the file-upload input field
            $("input[name='file']").change(function(){
                // This is a loop that can be used in case there are many selected files
                for(var i=0; i< this.files.length; i++){
                        var file = this.files[i];
                        name = file.name.toLowerCase();						
                        $('input[name="selected_file"]').val(name);
                }
            });	
            
           // Disable this button after it has been used
           $('#downloadButton').on('click',function(){
                $('#downloadButton').attr('disabled','disabled');
           });

        </script>
        <!-- BODY: END OF YOUR CODE -->
        <jsp:include page="/template/body_bottom.jsp"><jsp:param name="baseUrl" value="${baseUrl}" /></jsp:include>
    </body>
</html>
