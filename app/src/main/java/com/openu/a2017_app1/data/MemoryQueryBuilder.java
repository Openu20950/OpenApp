package com.openu.a2017_app1.data;

import com.openu.a2017_app1.models.IModel;
import com.openu.a2017_app1.models.LocationPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Raz on 30/12/2016.
 */

public class MemoryQueryBuilder<T extends IModel> implements QueryBuilder<T> {

    public static final int MAX_RESULTS = 1000;
    private static Map<String, SimpleComparator> comparators;

    static {
        comparators = new HashMap<>();
        comparators.put("==", new SimpleComparator() {
            @Override
            public boolean compare(Comparable o1, Comparable o2) {
                return (o1 == null ? (o2 == null) : (o1.compareTo(o2) == 0));
            }
        });
        comparators.put("!=", new SimpleComparator() {
            @Override
            public boolean compare(Comparable o1, Comparable o2) {
                return !(o1 == null ? (o2 == null) : (o1.compareTo(o2) == 0));
            }
        });
        comparators.put(">", new SimpleComparator() {
            @Override
            public boolean compare(Comparable o1, Comparable o2) {
                return o1.compareTo(o2) > 0;
            }
        });
        comparators.put(">=", new SimpleComparator() {
            @Override
            public boolean compare(Comparable o1, Comparable o2) {
                return o1.compareTo(o2) >= 0;
            }
        });
        comparators.put("<", new SimpleComparator() {
            @Override
            public boolean compare(Comparable o1, Comparable o2) {
                return o1.compareTo(o2) < 0;
            }
        });
        comparators.put("<=", new SimpleComparator() {
            @Override
            public boolean compare(Comparable o1, Comparable o2) {
                return o1.compareTo(o2) <= 0;
            }
        });
    }

    private List<T> table;
    private int limit;
    private int skip;
    private String sortBy;
    private boolean sortAsc;

    /* package */ MemoryQueryBuilder(List<T> table) {
        this.table = table;
        this.skip = 0;
        this.limit = MAX_RESULTS;

    }

    @Override
    public T get() {
        if (this.skip < 0 || this.skip >= table.size()) {
            return null;
        }
        sort();
        return table.get(this.skip);
    }

    @Override
    public T find(Object id) {
        for (T model: table) {
            Object modelId = model.getAttribute(model.getPrimaryKey());
            if (modelId.equals(id)) {
                return model;
            }
        }
        return null;
    }

    @Override
    public void findAsync(Object id, FindListener callback) {
        if (callback == null) { return; }
        callback.onItemFound(find(id));
    }

    @Override
    public List<T> getAll() {
        sort();
        int to = Math.min(this.skip + this.limit, table.size());
        return Collections.unmodifiableList(table.subList(this.skip, to));
    }

    @Override
    public void getAllAsync(GetAllListener<T> listener) {
        if (listener == null) return;
        listener.onItemsReceived(getAll());
    }

    @Override
    public long count() {
        return Math.min(this.limit, table.size());
    }

    @Override
    public double average(String field) {
        int count = 0;
        double sum = 0;
        for (T item: getAll()) {
            sum += (Double) item.getAttribute(field);
            count++;
        }
        return count == 0 ? 0 : sum / count;
    }

    @Override
    public QueryBuilder<T> where(String field, String operator, Object value) {
        if (!comparators.containsKey(operator)) {
            new IllegalArgumentException("operator \"" + operator + "\" is not supported!");
        }

        List<T> filteredTable = new ArrayList<>();
        SimpleComparator comperator = comparators.get(operator);
        for (T model : table) {
            Object modelValue = model.getAttribute(field);
            if (!(modelValue instanceof Comparable<?>)) {
                new IllegalStateException("The field is not comparable!");
            }
            if (comperator.compare((Comparable)modelValue, (Comparable)value)) {
                filteredTable.add(model);
            }
        }
        this.table = filteredTable;
        return this;
    }

    @Override
    public QueryBuilder<T> where(String field, Object value) {
        return where(field, "==", value);
    }

    @Override
    public QueryBuilder<T> whereNear(String field, LocationPoint value, double radius) {
        List<T> filteredTable = new ArrayList<>();
        for (T model : table) {
            Object modelValue = model.getAttribute(field);
            if (!(modelValue instanceof LocationPoint)) {
                new IllegalStateException("The field must be a location!");
            }
            if (value.distanceTo((LocationPoint)modelValue) <= radius) {
                filteredTable.add(model);
            }
        }
        this.table = filteredTable;
        return this;
    }

    @Override
    public QueryBuilder<T> whereContains(String field, String substring) {
        List<T> filteredTable = new ArrayList<>();
        for (T model : table) {
            Object modelValue = model.getAttribute(field);
            if (!(modelValue instanceof String)) {
                new IllegalStateException("The field must be a string!");
            }
            if (((String)modelValue).contains(substring)) {
                filteredTable.add(model);
            }
        }
        this.table = filteredTable;
        return this;
    }

    @Override
    public QueryBuilder<T> limit(int results) {
        if (results <= 0) {
            this.limit = MAX_RESULTS;
        } else {
            this.limit = results;
        }
        return this;
    }

    @Override
    public QueryBuilder<T> skip(int results) {
        if (results <= 0) {
            throw new IllegalArgumentException("results must be grater then zero");
        }
        this.skip = results;
        return this;
    }

    @Override
    public QueryBuilder<T> orderByAscending(String field) {
        this.sortBy = field;
        this.sortAsc = true;
        return this;
    }

    @Override
    public QueryBuilder<T> orderByDescending(String field) {
        this.sortBy = field;
        this.sortAsc = false;
        return this;
    }

    private void sort() {
        final String field = MemoryQueryBuilder.this.sortBy;
        if (field == null) {
            return;
        }

        java.util.Comparator<T> comparator;
        if (this.sortAsc) {
            comparator = new java.util.Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    Object o1value = o1.getAttribute(field);
                    Object o2value = o2.getAttribute(field);
                    if (o1value == null && o2value == null) {
                        return 0;
                    }
                    return o1value == null? ((Comparable)o2value).compareTo(o1value) : ((Comparable)o1value).compareTo(o2value);
                }
            };
        } else {
            comparator = new java.util.Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    Object o1value = o1.getAttribute(field);
                    Object o2value = o2.getAttribute(field);
                    if (o1value == null && o2value == null) {
                        return 0;
                    }
                    return -(o1value == null? ((Comparable)o2value).compareTo(o1value) : ((Comparable)o1value).compareTo(o2value)); // A minus sign
                }
            };
        }

        Collections.sort(table, comparator);
    }

    private interface SimpleComparator {
        boolean compare(Comparable o1, Comparable o2);
    }
}
