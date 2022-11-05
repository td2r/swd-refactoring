package ru.ivanau.sd.refactoring.servlet;

import ru.ivanau.sd.refactoring.dao.ProductsDao;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProductServlet extends HttpServlet {
    protected final ProductsDao dao;

    public ProductServlet(ProductsDao dao) {
        this.dao = dao;
    }

    protected void writeResponse(final HttpServletResponse response, final String text) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(text);
    }

    protected void writeHtmlResponse(final HttpServletResponse response, final String body) throws IOException {
        writeResponse(response, "<html><body>\n" + body + "</body></html>\n");
    }
}
