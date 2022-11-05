package ru.ivanau.sd.refactoring.servlet;

import ru.ivanau.sd.refactoring.Goods;
import ru.ivanau.sd.refactoring.dao.ProductsDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class QueryProductsServlet extends ProductServlet {
    public QueryProductsServlet(ProductsDao dao) {
        super(dao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        if ("max".equals(command)) {
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Product with max price: </h1>");
            Optional<Goods> max = dao.selectMax();
            if (max.isPresent()) {
                response.getWriter().println(max.get().getName() + "\t" + max.get().getPrice() + "</br>");
            }
            response.getWriter().println("</body></html>");
        } else if ("min".equals(command)) {
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Product with min price: </h1>");
            Optional<Goods> min = dao.selectMin();
            if (min.isPresent()) {
                response.getWriter().println(min.get().getName() + "\t" + min.get().getPrice() + "</br>");
            }
            response.getWriter().println("</body></html>");
        } else if ("sum".equals(command)) {
            response.getWriter().println("<html><body>");
            response.getWriter().println("Summary price: ");
            response.getWriter().println(dao.selectSum());
            response.getWriter().println("</body></html>");
        } else if ("count".equals(command)) {
            response.getWriter().println("<html><body>");
            response.getWriter().println("Number of products: ");
            response.getWriter().println(dao.selectCount());
            response.getWriter().println("</body></html>");
        } else {
            response.getWriter().println("Unknown command: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
