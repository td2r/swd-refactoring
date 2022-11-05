package ru.ivanau.sd.refactoring.servlet;

import org.junit.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServletTests {
    private static class Goods {
        private final String name;
        private final String price;

        public Goods(String name, String price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }
    }

    private final static String CONNECTION_URL = "jdbc:sqlite:test.db";
    private final List<Goods> BOARD_GAMES = List.of(
            new Goods("Munchkin", "500"),
            new Goods("Ticket to ride", "1200"),
            new Goods("Dungeon & Dragons", "3500")
    );

    private static Connection connection;

    private StringWriter writer;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeClass
    public static void beforeAll() {
        try {
            connection = DriverManager.getConnection(CONNECTION_URL);
            String createTableSql =
                    "CREATE TABLE PRODUCT" +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " PRICE          INT     NOT NULL)";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(createTableSql);
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        try {
            String dropSql = "DELETE FROM PRODUCT";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(dropSql);
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void afterAll() {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DROP TABLE PRODUCT");
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void addServletTest() throws IOException {
        when(request.getParameter("name")).thenReturn(BOARD_GAMES.get(0).getName());
        when(request.getParameter("price")).thenReturn(BOARD_GAMES.get(0).getPrice());
        AddProductServlet servlet = new AddProductServlet();

        servlet.doGet(request, response);

        Assert.assertEquals(writer.toString(), "OK\n");
    }

    void addAll(final List<Goods> items) throws SQLException {
        final String sql = "INSERT INTO PRODUCT (NAME, PRICE) VALUES " +
                items.stream()
                .map(goods -> "(\"" + goods.getName() + "\"," + goods.getPrice() + ")")
                .collect(Collectors.joining(", "));
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }

    @Test
    public void getServletTest() throws IOException, SQLException {
        addAll(BOARD_GAMES);
        GetProductsServlet servlet = new GetProductsServlet();

        servlet.doGet(request, response);

        final String expected = "<html><body>\n" +
                BOARD_GAMES.stream()
                        .map(goods -> goods.getName() + "\t" + goods.getPrice() + "</br>\n")
                        .collect(Collectors.joining()) +
                "</body></html>\n";
        Assert.assertEquals(writer.toString(), expected);
    }

    @Test
    public void queryServletMaxTest() throws SQLException, IOException {
        addAll(BOARD_GAMES);
        when(request.getParameter("command")).thenReturn("max");
        QueryServlet servlet = new QueryServlet();

        servlet.doGet(request, response);

        final String maxPrice = BOARD_GAMES.stream()
                .map(goods -> Integer.valueOf(goods.getPrice()))
                .max(Comparator.naturalOrder())
                .map(String::valueOf)
                .orElse("");
        final String expected = "<html><body>\n" +
                "<h1>Product with max price: </h1>\n" +
                BOARD_GAMES.stream()
                        .filter(goods -> goods.getPrice().equals(maxPrice))
                        .map(goods -> goods.getName() + "\t" + goods.getPrice() + "</br>\n")
                        .collect(Collectors.joining()) +
                "</body></html>\n";
        Assert.assertEquals(writer.toString(), expected);
    }

    @Test
    public void queryServletMinTest() throws SQLException, IOException {
        addAll(BOARD_GAMES);
        when(request.getParameter("command")).thenReturn("min");
        QueryServlet servlet = new QueryServlet();

        servlet.doGet(request, response);

        final String maxPrice = BOARD_GAMES.stream()
                .map(goods -> Integer.valueOf(goods.getPrice()))
                .min(Comparator.naturalOrder())
                .map(String::valueOf)
                .orElse("");
        final String expected = "<html><body>\n" +
                "<h1>Product with min price: </h1>\n" +
                BOARD_GAMES.stream()
                        .filter(goods -> goods.getPrice().equals(maxPrice))
                        .map(goods -> goods.getName() + "\t" + goods.getPrice() + "</br>\n")
                        .collect(Collectors.joining()) +
                "</body></html>\n";
        Assert.assertEquals(writer.toString(), expected);
    }

    @Test
    public void queryServletSumTest() throws SQLException, IOException {
        addAll(BOARD_GAMES);
        when(request.getParameter("command")).thenReturn("sum");
        QueryServlet servlet = new QueryServlet();

        servlet.doGet(request, response);

        final int sumPrice = BOARD_GAMES.stream()
                .mapToInt(goods -> Integer.parseInt(goods.getPrice()))
                .sum();
        final String expected = "<html><body>\n" +
                "Summary price: \n" +
                sumPrice + "\n" +
                "</body></html>\n";
        Assert.assertEquals(writer.toString(), expected);
    }

    @Test
    public void queryServletCountTest() throws SQLException, IOException {
        addAll(BOARD_GAMES);
        when(request.getParameter("command")).thenReturn("count");
        QueryServlet servlet = new QueryServlet();

        servlet.doGet(request, response);

        final String expected = "<html><body>\n" +
                "Number of products: \n" +
                BOARD_GAMES.size() + "\n" +
                "</body></html>\n";
        Assert.assertEquals(writer.toString(), expected);
    }
}
