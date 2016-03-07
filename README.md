The MedOBIS vlab contains two tools. A Viewer for data visualization and a coordinates convertor. Both are built with Java/J2EE. The Viewer also uses the OpenLayers, GeoExt and GXP javascript libraries.

##Requirements

The MedOBIS vLab has been tested within the following environment:

* Java 7
* MySQL 5.6
* Tomcat 7
* Geoserver 2.7.2

##Installation

The vlab must be deployed on the same server as Geoserver. 

####Database Schema and configuration

MedOBIS vLab requires a MySQL database with a schema described in medobis.sql file in the docs directory. 

Database connection and application paths should be configured through the appropriate parameters in web.xml. The initial value of baseUrl must also be set in the AuthFilter class.


####Authentication

A very basic authentication mechanism (login/logout) has been included in application's code. 
This mechanism is meant to change according to your access control requirements. The default
and hard-coded credentials for logging in are:

username: admin
password: admin
