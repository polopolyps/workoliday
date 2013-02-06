## Workoliday
The purpose of the workoliday project is to make it easy to integrate external content into Polopoly.
This project consists of a Polopoly plugin, Workoliday Plugin and a Polopoly client web application.

### Use cases 
* Display content coming from any type of external feed, RSS, XML, JSON. 
* Cache your own elements or content that are slow.
* Expose your site's header and/or footer to other sites (to make a wordpress site look like your site ;).

## How to use Workoliday
In Polopoly you create a content called External Data Cache. You can find it on the page or site's sources tab.
1. Enter the URL to the external resource
2. If you need to transform the result before it gets stored on the content, you need to create a Transformer.
3. Set an update interval on the content. The update of the content will be handled by the Workoliday webapp.
4. Start the Workoliday web application if it is not already running and watch the magic! 


## How to monitor Workoliday
The Workoliday web application exposes some statistical data via JMX. 
* How many content that is currently in the index
* Number of successful/failed updates
* Top list over successful/failed content ids and their resources they are requesting

## How to add Workoliday to your project

### Binary dependency
In the top pom.xml of your project, add a dependency to the workoliday plugin.
```xml
<dependencies>
    <dependency>
        <groupId>com.atex.plugins</groupId>
        <artifactId>workoliday-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
     </dependency>
</dependencies>
```
If you want to start the workoliday web application along with all the other web applications running in Polopoly Nitro, add the following snippet in the top pom.xml of the project in the jetty plugin section.
```xml
<contextHandler implementation="org.mortbay.jetty.plugin.JettyWebAppContext">
  <warArtifact>
    <groupId>com.atex.ps.workoliday</groupId>
    <artifactId>workoliday</artifactId>
    <version>1.0-SNAPSHOT</version>
    <type>war</type>
  </warArtifact>
  <contextPath>/workoliday</contextPath>
</contextHandler>
```
### Source dependency
Checkout the workoliday project. In the top pom.xml of your project add the workoliday plugin and the workoliday web application as 
modules to the project.
```xml
<modules>
    ...
    <module>../workoliday/workoliday-plugin</module>
    <module>../workoliday/webapp-workoliday</module>
</modules>
```
If you want to start the workoliday web application along with all the other web applications running in Polopoly Nitro, add the following snippet in the top pom.xml of the project in the jetty plugin section.
```xml
<contextHandler implementation="org.mortbay.jetty.plugin.JettyWebAppContext">
  <warArtifact>
    <groupId>com.atex.ps.workoliday</groupId>
    <artifactId>workoliday</artifactId>
    <version>1.0-SNAPSHOT</version>
    <type>war</type>
  </warArtifact>
  <contextPath>/workoliday</contextPath>
</contextHandler>
```