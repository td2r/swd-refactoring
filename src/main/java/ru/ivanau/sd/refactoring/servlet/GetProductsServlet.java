package ru.ivanau.sd.refactoring.servlet;

import ru.ivanau.sd.refactoring.Goods;
import ru.ivanau.sd.refactoring.dao.ProductsDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class GetProductsServlet extends ProductServlet {
    public GetProductsServlet(ProductsDao dao) {
        super(dao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Goods> goodsList = dao.selectAll();
        response.getWriter().println("<html><body>");
        for (Goods goods : goodsList) {
            response.getWriter().println(goods.getName() + "\t" + goods.getPrice() + "</br>");
        }
        response.getWriter().println("</body></html>");

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
