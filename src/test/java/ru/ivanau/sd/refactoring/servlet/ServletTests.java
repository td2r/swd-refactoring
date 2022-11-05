package ru.ivanau.sd.refactoring.servlet;

import org.junit.*;
import ru.ivanau.sd.refactoring.Goods;
import ru.ivanau.sd.refactoring.dao.ProductsDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServletTests {
    private final static ProductsDao PRODUCTS_DAO = new ProductsDao("jdbc:sqlite:test.db");
    private final List<Goods> BOARD_GAMES = List.of(
            new Goods("Munchkin", 500),
            new Goods("Ticket to ride", 1200),
            new Goods("Dungeon & Dragons", 3500)
    );

    private StringWriter writer;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeClass
    public static void beforeAll() {
        PRODUCTS_DAO.createTable();
    }

    @Before
    public void before() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        try {
            when(response.getWriter()).thenReturn(printWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void after() {
        PRODUCTS_DAO.clearProducts();
    }

    @AfterClass
    public static void afterAll() {
        PRODUCTS_DAO.dropProducts();
    }

    @Test
    public void addServletTest() throws IOException {
        when(request.getParameter("name")).thenReturn(BOARD_GAMES.get(0).getName());
        when(request.getParameter("price")).thenReturn(String.valueOf(BOARD_GAMES.get(0).getPrice()));
        AddProductServlet servlet = new AddProductServlet(PRODUCTS_DAO);

        servlet.doGet(request, response);

        Assert.assertEquals(writer.toString(), "OK\n");
    }

    @Test
    public void getServletTest() throws IOException {
        PRODUCTS_DAO.insert(BOARD_GAMES);
        GetProductsServlet servlet = new GetProductsServlet(PRODUCTS_DAO);

        servlet.doGet(request, response);

        final String expected = "<html><body>\n" +
                BOARD_GAMES.stream()
                        .map(goods -> goods.getName() + "\t" + goods.getPrice() + "</br>\n")
                        .collect(Collectors.joining()) +
                "</body></html>\n";
        Assert.assertEquals(writer.toString(), expected);
    }

    @Test
    public void queryServletMaxTest() throws IOException {
        PRODUCTS_DAO.insert(BOARD_GAMES);
        when(request.getParameter("command")).thenReturn("max");
        QueryProductsServlet servlet = new QueryProductsServlet(PRODUCTS_DAO);

        servlet.doGet(request, response);

        final String maxPrice = BOARD_GAMES.stream()
                .map(Goods::getPrice)
                .max(Comparator.naturalOrder())
                .map(String::valueOf)
                .orElse("");
        final String expected = "<html><body>\n" +
                "<h1>Product with max price: </h1>\n" +
                BOARD_GAMES.stream()
                        .filter(goods -> String.valueOf(goods.getPrice()).equals(maxPrice))
                        .map(goods -> goods.getName() + "\t" + goods.getPrice() + "</br>\n")
                        .collect(Collectors.joining()) +
                "</body></html>\n";

        Assert.assertEquals(writer.toString(), expected);
    }

    @Test
    public void queryServletMinTest() throws IOException {
        PRODUCTS_DAO.insert(BOARD_GAMES);
        when(request.getParameter("command")).thenReturn("min");
        QueryProductsServlet servlet = new QueryProductsServlet(PRODUCTS_DAO);

        servlet.doGet(request, response);

        final String maxPrice = BOARD_GAMES.stream()
                .map(Goods::getPrice)
                .min(Comparator.naturalOrder())
                .map(String::valueOf)
                .orElse("");
        final String expected = "<html><body>\n" +
                "<h1>Product with min price: </h1>\n" +
                BOARD_GAMES.stream()
                        .filter(goods -> String.valueOf(goods.getPrice()).equals(maxPrice))
                        .map(goods -> goods.getName() + "\t" + goods.getPrice() + "</br>\n")
                        .collect(Collectors.joining()) +
                "</body></html>\n";
        Assert.assertEquals(writer.toString(), expected);
    }

    @Test
    public void queryServletSumTest() throws IOException {
        PRODUCTS_DAO.insert(BOARD_GAMES);
        when(request.getParameter("command")).thenReturn("sum");
        QueryProductsServlet servlet = new QueryProductsServlet(PRODUCTS_DAO);

        servlet.doGet(request, response);

        final int sumPrice = BOARD_GAMES.stream()
                .mapToInt(Goods::getPrice)
                .sum();
        final String expected = "<html><body>\n" +
                "Summary price: \n" +
                sumPrice + "\n" +
                "</body></html>\n";
        Assert.assertEquals(writer.toString(), expected);
    }

    @Test
    public void queryServletCountTest() throws IOException {
        PRODUCTS_DAO.insert(BOARD_GAMES);
        when(request.getParameter("command")).thenReturn("count");
        QueryProductsServlet servlet = new QueryProductsServlet(PRODUCTS_DAO);

        servlet.doGet(request, response);

        final String expected = "<html><body>\n" +
                "Number of products: \n" +
                BOARD_GAMES.size() + "\n" +
                "</body></html>\n";
        Assert.assertEquals(writer.toString(), expected);
    }
}
