package ru.ivanau.sd.refactoring.servlet;

import ru.ivanau.sd.refactoring.Goods;
import ru.ivanau.sd.refactoring.dao.ProductsDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GetProductsServlet extends ProductServlet {
    public GetProductsServlet(ProductsDao dao) {
        super(dao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Goods> goodsList = dao.selectAll();
        String body = goodsList.stream()
                .map(goods -> goods.getName() + "\t" + goods.getPrice() + "</br>\n")
                .collect(Collectors.joining());
        writeHtmlResponse(response, body);
    }
}
