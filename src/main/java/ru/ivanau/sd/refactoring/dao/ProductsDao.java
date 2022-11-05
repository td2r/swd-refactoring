package ru.ivanau.sd.refactoring.dao;

import ru.ivanau.sd.refactoring.Goods;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProductsDao {
    private static final String PRODUCT_TABLE_SIGNATURE =
            "PRODUCT(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            " NAME           TEXT    NOT NULL, " +
            " PRICE          INT     NOT NULL)";

    private static final Function<ResultSet, List<Goods>> HANDLE_GOODS_LIST = rs -> {
        List<Goods> goods = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) break;
                goods.add(goodsFromRsIterator(rs));
            } catch (SQLException e) {
                throw new DaoException(e);
            }
        }
        return goods;
    };

    private static final Function<ResultSet, Optional<Goods>> HANDLE_OPTIONAL_GOODS = rs -> {
        try {
            return rs.next() ? Optional.of(goodsFromRsIterator(rs)) : Optional.empty();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    };

    private static final Function<ResultSet, Integer> HANDLE_INTEGER = rs -> {
        try {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    };

    private static Goods goodsFromRsIterator(ResultSet rs) throws SQLException {
        return new Goods(rs.getString("name"), rs.getInt("price"));
    }

    private final String connectionUrl;

    public ProductsDao(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    private void executeUpdate(final String sql) {
        try (final Connection connection = DriverManager.getConnection(connectionUrl);
             final Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private<T> T executeQuery(final String sql, final Function<ResultSet, T> resultHandler) {
        try (final Connection connection = DriverManager.getConnection(connectionUrl);
             final Statement stmt = connection.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {
            return resultHandler.apply(rs);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public void createTable() {
        executeUpdate("CREATE TABLE " + PRODUCT_TABLE_SIGNATURE);
    }

    public void createTableIfNotExists() {
        executeUpdate("CREATE TABLE IF NOT EXISTS " + PRODUCT_TABLE_SIGNATURE);
    }

    public void insert(List<Goods> goods) {
        executeUpdate("INSERT INTO PRODUCT (NAME, PRICE) VALUES " +
                goods.stream()
                        .map(item -> "(\"" + item.getName() + "\"," + item.getPrice() + ")")
                        .collect(Collectors.joining(",")));
    }

    public List<Goods> selectAll() {
        return executeQuery("SELECT * FROM PRODUCT", HANDLE_GOODS_LIST);
    }

    public Optional<Goods> selectMax() {
        return executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1", HANDLE_OPTIONAL_GOODS);
    }

    public Optional<Goods> selectMin() {
        return executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1", HANDLE_OPTIONAL_GOODS);
    }

    public Integer selectSum() {
        return executeQuery("SELECT SUM(price) FROM PRODUCT", HANDLE_INTEGER);
    }

    public Integer selectCount() {
        return executeQuery("SELECT COUNT(*) FROM PRODUCT", HANDLE_INTEGER);
    }

    public void clearProducts() {
        executeUpdate("DELETE FROM PRODUCT");
    }

    public void dropProducts() {
        executeUpdate("DROP TABLE PRODUCT");
    }
}
