package ru.ivanau.sd.refactoring;

public class Goods {
    private final String name;
    private final int price;

    public Goods(final String name, final int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
