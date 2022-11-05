package ru.ivanau.sd.refactoring.servlet;

import ru.ivanau.sd.refactoring.dao.ProductsDao;

import javax.servlet.http.HttpServlet;

public class ProductServlet extends HttpServlet {
    protected final ProductsDao dao;

    public ProductServlet(ProductsDao dao) {
        this.dao = dao;
    }
}
