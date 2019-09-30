package com.revolut.moneytransfer;

import com.revolut.moneytransfer.constant.MoneyTransferConstants;
import com.revolut.moneytransfer.controller.AccountController;
import com.revolut.moneytransfer.controller.TransactionController;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.servlet.ServletContainer;

public class MoneyTransferApplication {

    public static Server server;

    public static void main(String[] args) throws Exception {
        initServer();
        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }

    public static void initServer(){
        if(server == null){
            synchronized (MoneyTransferApplication.class) {
                if(server == null) {
                    QueuedThreadPool threadPool = new QueuedThreadPool(200, 10, 180);
                    server = new Server(threadPool);
                    ServerConnector connector = new ServerConnector(server);
                    connector.setPort(MoneyTransferConstants.SERVER_PORT);
                    server.setConnectors(new Connector[] { connector });
                    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
                    context.setContextPath("/");
                    server.setHandler(context);
                    ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
                    jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
                            AccountController.class.getCanonicalName() + "," +
                                    TransactionController.class.getCanonicalName());
                }
            }
        }
    }
}
