package com.starsoft.tomcat;

import java.net.InetAddress;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Embedded;

public class EmbeddedTomcat {
 /**
  * 
  * @author  QQ:393568883
  */
 private Embedded tomcat; //
 public void startup() throws Exception {
  String webrootPath = "F:\\workspace\\eclipse\\draftoa\\WebContent"; // 
  String contextPath = "/"; // 
  String xmlPath = "F:\\workspace\\eclipse\\draftoa\\WebContent\\WEB-INF\\web.xml"; // 
  tomcat = new Embedded();
  Engine engine = tomcat.createEngine();
  tomcat.setCatalinaHome(webrootPath);
//  Host host = tomcat.createHost("192.168.1.103", webrootPath);
  Host host = tomcat.createHost("127.0.0.1", webrootPath);
  Context context = tomcat.createContext(contextPath, webrootPath);
  context.setAltDDName(xmlPath);
  context.setReloadable(false);
  host.addChild(context);
  engine.addChild(host);
  engine.setDefaultHost(host.getName());
  engine.setName("EmbedTomcatServer");
  tomcat.addEngine(engine);
  Connector connector = tomcat.createConnector(InetAddress
    .getByName("localhost"), 8081, false);
  connector.setURIEncoding("UTF-8");
  tomcat.addConnector(connector);
  tomcat.start();
 }
 
 public void shutdown() throws Exception {
  tomcat.stop();
 }

 public static void main(String[] args) {
  try {
   new EmbeddedTomcat().startup();
  } catch (Exception e) {
   e.printStackTrace();
  }
 }
}

