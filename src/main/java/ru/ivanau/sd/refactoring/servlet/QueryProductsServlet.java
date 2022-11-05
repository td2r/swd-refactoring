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
            final Optional<Goods> max = dao.selectMax();
            String body = "<h1>Product with max price: </h1>\n" +
                    max.map(goods -> goods.getName() + "\t" + goods.getPrice() + "</br>\n").orElse("");
            writeHtmlResponse(response, body);
        } else if ("min".equals(command)) {
            final Optional<Goods> min = dao.selectMin();
            String body = "<h1>Product with min price: </h1>\n" +
                    min.map(goods -> goods.getName() + "\t" + goods.getPrice() + "</br>\n").orElse("");
            writeHtmlResponse(response, body);
        } else if ("sum".equals(command)) {
            writeHtmlResponse(response, "Summary price: \n" + dao.selectSum() + "\n");
        } else if ("count".equals(command)) {
            writeHtmlResponse(response, "Number of products: \n" + dao.selectCount() + "\n");
        } else {
            writeResponse(response, "Unknown command: " + command + "\n");
        }
    }

}
