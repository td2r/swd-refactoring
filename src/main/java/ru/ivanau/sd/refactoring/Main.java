package ru.ivanau.sd.refactoring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.ivanau.sd.refactoring.dao.ProductsDao;
import ru.ivanau.sd.refactoring.servlet.AddProductServlet;
import ru.ivanau.sd.refactoring.servlet.GetProductsServlet;
import ru.ivanau.sd.refactoring.servlet.QueryProductsServlet;

public class Main {
    public static void main(String[] args) throws Exception {
        final ProductsDao productionDao = new ProductsDao("jdbc:sqlite:production.db");
        productionDao.createTableIfNotExists();

        Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new AddProductServlet(productionDao)), "/add-product");
        context.addServlet(new ServletHolder(new GetProductsServlet(productionDao)),"/get-products");
        context.addServlet(new ServletHolder(new QueryProductsServlet(productionDao)),"/query");

        server.start();
        server.join();
    }
}
