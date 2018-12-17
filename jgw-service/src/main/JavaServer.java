/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import app.config.ConfigApp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 *
 * @author tam
 */
public class JavaServer {

    public static void main(String[] args) {
        //ConfigApp
        ConfigApp.init();

        //ThreadPool Server
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(20);
        threadPool.setMaxThreads(500);
        Server server = new Server(threadPool);

        //Connecter
        ServerConnector connector = new ServerConnector(server);
        connector.setHost(ConfigApp.MYSQL_HOST);
        connector.setPort(ConfigApp.WEB_SERVER_PORT);
        server.setConnectors(new Connector[]{connector});

        //Context Handler
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath(ConfigApp.WEB_SERVER_CONTEXT_PATH);

        HashSessionManager manager = new HashSessionManager();
        SessionHandler sessions = new SessionHandler(manager);
        context.setHandler(sessions);

        //Servlet for Guest
        context.addServlet(app.guest.controller.LoginController.class, "/auth/*");
        context.addServlet(app.guest.controller.ProductController.class, "/products/*");
        context.addServlet(app.guest.controller.BrandController.class, "/brands/*");
        context.addServlet(app.guest.controller.TypeController.class, "/types/*");
        

        //Servlet for User
        context.addServlet(app.user.controller.OrderController.class, "/user/order/*");
        context.addServlet(app.user.controller.AccountController.class, "/user/account/*");
        
        //Servlet for Employee
        context.addServlet(app.employee.controller.OrderController.class, "/employee/order/*");
        
        //Servlet for Admin
        context.addServlet(app.admin.controller.AccountController.class, "/admin/account/*");
        context.addServlet(app.admin.controller.ProductController.class, "/admin/product/*");

        server.setHandler(context);
        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            Logger.getLogger(JavaServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
