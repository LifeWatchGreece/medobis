<!DOCTYPE html> 
<html lang="en">
    <head>                 
        <!-- HEAD: YOUR CODE GOES HERE -->
        <title>MedOBIS vLab</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jsp:include page="/template/head.jsp"><jsp:param name="baseUrl" value="${baseUrl}" /></jsp:include>
        <link rel="shortcut icon" href="./favicon.ico">
        <style type="text/css">
            .linkarea , .linkarea_off {
                 width: 310px;
            }
            .linkarea:hover {               
                cursor: pointer;
                border-color: blue;
            }            
        </style>
        <!-- HEAD: END OF YOUR CODE -->
    </head> 
    <body>  
        <jsp:include page="/template/body_top.jsp"><jsp:param name="baseUrl" value="${baseUrl}" /></jsp:include>
        <!-- BODY: YOUR CODE GOES HERE -->
        <div class="row">
            <div style="text-align: center">
                <img src="images/medobis150px.png" style="width: 50px">
                <span style="font-weight: bold; margin-left: 10px; font-size: 18px; color: #146E76">MedOBIS vLab</span>
            </div>
        </div>
        
        <div class="row">            
            <div class="col-md-4" style="text-align: center">
              <div class="thumbnail linkarea" onclick="location.href='${baseUrl}/viewer'">
                <img src="images/viewer.png">
                <div class="caption">
                  <h3>MedOBIS Viewer</h3>
                  <p style='text-align: left'>MedObis viewer allows users to search and download marine species datasets from all over the Mediterranean.
In addition weekly satellite Mediterranean data and climatology since 2002, Mediterranean water bodies, ports, lakes and
marine protected sites are freely available.
The user will have the opportunity to query through datasets and visualize species occurrences and the corresponded
satellite values and download the  results in several different formats. </p>                 
                </div>
              </div>
            </div>
            <div class="col-md-4" style="text-align: center">
              <div class="thumbnail linkarea" onclick="location.href='${baseUrl}/coords'">
                <img src="images/globe6.jpg">
                <div class="caption">
                  <h3>Coordinates Convertor</h3>
                  <p style='text-align: left'>The numerical values for latitude and longitude can occur in a number of different formats.
The coordinate conversion tool is useful of converting a degrees-minutes-seconds or degrees-decimal-minutes format to the
decimal degrees format and interpreting the spatial information easily.
The user can calculate a single position or a spreadsheet of coordinates. 
                  </p>                 
                </div>
              </div>
            </div>            
        </div>
        <!-- BODY: END OF YOUR CODE -->
        <jsp:include page="/template/body_bottom.jsp"><jsp:param name="baseUrl" value="${baseUrl}" /></jsp:include>
    </body>
</html>

