package com.lingkarkode.belajarpascal;

public class Category {

    public static final int LATIHAN_SOAL_1 = 1;
    public static final int LATIHAN_SOAL_2 = 2;
    public static final int LATIHAN_SOAL_3 = 3;

    private int id;
    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
