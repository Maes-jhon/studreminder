package com.example.studreminder;

public class ArchivedItemModel {
    private int id;
    private String title;
    private String subtitle;
    private String tableName;
    private String idColumn;

    public ArchivedItemModel(int id, String title, String subtitle, String tableName, String idColumn) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.tableName = tableName;
        this.idColumn = idColumn;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getTableName() { return tableName; }
    public String getIdColumn() { return idColumn; }
}