package mm.pndaza.tipitakamyanmar.utils;
import java.util.*;
import java.util.*;

public class SQLBuilder {
    private StringBuilder query;
    private String table;
    private final List<String> columns;
    private final List<String> values;
    private final Map<String, String> conditions;
    private final Map<String, String> updates;
    private final Map<String, String> joins;

    private enum QueryType { SELECT, INSERT, UPDATE, DELETE }
    private QueryType queryType;

    public SQLBuilder() {
        query = new StringBuilder();
        columns = new ArrayList<>();
        values = new ArrayList<>();
        conditions = new HashMap<>();
        updates = new HashMap<>();
        joins = new HashMap<>();
    }

    public SQLBuilder table(String table) {
        this.table = table;
        return this;
    }

    public SQLBuilder from(String table) {
        this.table = table;
        return this;
    }

    public SQLBuilder into(String table) {
        this.table = table;
        return this;
    }

    public SQLBuilder select(String... columns) {
        this.queryType = QueryType.SELECT;
        this.columns.clear();
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    public SQLBuilder insert(String... columns) {
        this.queryType = QueryType.INSERT;
        this.columns.clear();
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    public SQLBuilder values(Object... values) {
        this.values.clear();
        for (Object value : values) {
            if (value == null) {
                this.values.add("NULL");
            } else if (value instanceof String) {
                this.values.add("'" + value.toString().replace("'", "''") + "'"); // SQL escape for quotes
            } else {
                this.values.add(value.toString());
            }
        }
        return this;
    }

    public SQLBuilder update(String... columns) {
        this.queryType = QueryType.UPDATE;
        this.columns.clear();
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    public SQLBuilder set(String column, String value) {
        updates.put(column, column + " = '" + value + "'");
        return this;
    }

    public SQLBuilder where(String column, String operator, Object value) {
        String valueStr;
        if (value == null) {
            valueStr = "NULL";
        } else if (value instanceof String) {
            valueStr = "'" + value.toString().replace("'", "''") + "'"; // SQL escape for quotes
        } else {
            valueStr = value.toString();
        }
        conditions.put(column, column + " " + operator + " " + valueStr);
        return this;
    }

    public SQLBuilder innerJoin(String table, String leftColumn, String rightColumn) {
        joins.put(table, "INNER JOIN " + table + " ON " + leftColumn + " = " + rightColumn);
        return this;
    }

    public SQLBuilder delete() {
        this.queryType = QueryType.DELETE;
        return this;
    }

    public String build() {
        query.setLength(0);

        switch (queryType) {
            case SELECT:
                buildSelect();
                break;
            case INSERT:
                buildInsert();
                break;
            case UPDATE:
                buildUpdate();
                break;
            case DELETE:
                buildDelete();
                break;
        }

        return query.toString();
    }

    private void buildSelect() {
        query.append("SELECT ");
        if (columns.isEmpty()) {
            query.append("*");
        } else {
            query.append(String.join(", ", columns));
        }
        query.append(" FROM ").append(table);

        if (!joins.isEmpty()) {
            query.append(" ").append(String.join(" ", joins.values()));
        }

        if (!conditions.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", conditions.values()));
        }
    }

    private void buildInsert() {
        query.append("INSERT INTO ").append(table)
                .append(" (").append(String.join(", ", columns)).append(")")
                .append(" VALUES (").append(String.join(", ", values)).append(")");
    }

    private void buildUpdate() {
        query.append("UPDATE ").append(table)
                .append(" SET ").append(String.join(", ", updates.values()));
        if (!conditions.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", conditions.values()));
        }
    }

    private void buildDelete() {
        query.append("DELETE FROM ").append(table);
        if (!conditions.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", conditions.values()));
        }
    }

    public static void main(String[] args) {
        // Example usage
        SQLBuilder builder = new SQLBuilder();
        String selectQuery = builder.table("users")
                .select("id", "name")
                .where("age", ">", "18")
                .innerJoin("orders", "users.id", "orders.user_id")
                .build();
        System.out.println(selectQuery);

        String insertQuery = builder.table("users")
                .insert("name", "age")
                .values("John Doe", 30)
                .build();
        System.out.println(insertQuery);

        String updateQuery = builder.table("users")
                .update("name", "age")
                .set("name", "Jane Doe")
                .set("age", "32")
                .where("id", "=", "1")
                .build();
        System.out.println(updateQuery);

        String deleteQuery = builder.table("users")
                .delete()
                .where("id", "=", "1")
                .build();
        System.out.println(deleteQuery);
    }
}










