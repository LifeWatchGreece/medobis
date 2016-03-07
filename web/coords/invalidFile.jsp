<%-- 
    Document   : invalidFile
    Created on : Apr 22, 2015, 11:22:34 AM
    Author     : Alexandros
--%>

<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>       
        <!-- HEAD: YOUR CODE GOES HERE -->
        <title>Invalid CSV Input File</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jsp:include page="/template/head.jsp"><jsp:param name="baseUrl" value="${baseUrl}" /></jsp:include>
        <link rel="stylesheet" type="text/css" href="${baseUrl}/coords.css">        
        <!-- HEAD: END OF YOUR CODE -->
    </head>
    
    <body>
        <jsp:include page="/template/body_top.jsp"><jsp:param name="baseUrl" value="${baseUrl}" /></jsp:include>
        <!-- BODY: YOUR CODE GOES HERE -->
        <div id="logo">
            <img src="${baseUrl}/coords/logo_banner.png"/>
        </div>
            <%
            if(request.getAttribute("invalidFileFormat").equals("yes")){%>
                <div style="color:red">Invalid file format!</div>
                <p>
                    Please check the following about your CSV file:<br>
                        1. It is encoded in UTF8<br>    
                        2. The values are separated using ";" character<br>    
                </p>
            <%}            
                ArrayList<String> errorList = (ArrayList<String>) request.getAttribute("errorList");
                if(errorList != null){
                    for(String errorMessage : errorList){%>
                        <%=errorMessage%>                
                    <%}
                }
            %>
            <div style="text-align: left; margin-top: 10px">
                <a href="${baseUrl}/coords" class="btn btn-default btn-sm">Try again</a>
            </div>
        <!-- BODY: END OF YOUR CODE -->
        <jsp:include page="/template/body_bottom.jsp"><jsp:param name="baseUrl" value="${baseUrl}" /></jsp:include>
    </body>
</html>
