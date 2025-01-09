package exercise4.indexing.utils;


import exercise4.indexing.primary.PrimaryIndex;
import exercise4.indexing.secondary.SecondaryIndex;

import java.util.List;
import java.util.Set;

public class Table {
    private final Schema schema;
    private final PrimaryIndex primaryIndex;
    private final SecondaryIndex[] secondaryIndexes;

    public Table(Schema schema, PrimaryIndex primaryIndex) {
        this.schema = schema;
        this.primaryIndex = primaryIndex;
        this.secondaryIndexes = new SecondaryIndex[schema.columnCount()];
    }

    /**
     * Set the secondary index for the respective column, replacing any existing index.
     * Rows already in the table must be added to the index.
     */
    public void setSecondaryIndex(int columnIndex, SecondaryIndex index) {
        this.primaryIndex.scan().forEach(entry -> index
                .insert(entry.getValue().getColumn(columnIndex), entry.getKey()));
        this.secondaryIndexes[columnIndex] = index;
    }

    /**
     * Insert a row into the table by assigning a new TID and adding it to all primary/secondary indexes.
     */
   /* public void insert(Row row) {
        // Assign a new TID using the primary index
        long tid = primaryIndex.insert(row);

        // Update all secondary indices
        for (int col = 0; col < row.size(); col++) {
            Object value = row.get(col);
            if (secondaryIndexes[col] != null) {
                secondaryIndexes[col].insert(value, tid);
            }
        }
    }*/


    public boolean remove(ResultSet resultSet) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Perform a point query on the table. Returns the ResultSet of all rows with the respective column value at the given column.
     */
   /* public ResultSet pointQueryAtColumn(int columnIndex,Object value) {
        if (secondaryIndexes[columnIndex] == null) {
            throw new IllegalArgumentException("No secondary index on column " + columnIndex);
        }

        // Fetch TIDs from the secondary index
        List<Long> tids = secondaryIndexes[columnIndex].get(value);

        // Convert TIDs to rows using the primary index
        return ResultSet.fromTIDs(primaryIndex, tids);
    }*/


    /**
     * Perform a range query on the table. Returns the ResultSet of all rows with column value at columnIndex in the interval [from, to).
     */
    public ResultSet rangeQueryAtColumn(int columnIndex, Object from, Object to) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
}