/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

/**
 * Data access object (DAO) for {@link ListEntity}.
 *
 * @author Colin Walters
 * @version 1.0, 17/04/2023
 * @since 1.0
 */
@Dao
public interface ListDao {
    // Returns all of the columns.  If columns needed to be excluded, @Ignore could be used in the
    // ListEntity class before the column declaration.
    /**
     * Selects all of the items in the list table.
     *
     * @return  the <code>LiveData</code> list of {@link ListEntity} items
     */
    @Query("SELECT * FROM List")
    LiveData<List<ListEntity>> selectAllQuery();

    /*
     * From SQLite website, ON CONFLICT clause:
     *
     * ABORT
     * When an applicable constraint violation occurs, the ABORT resolution algorithm aborts the
     * current SQL statement with an SQLITE_CONSTRAINT error and backs out any changes made by the
     * current SQL statement; but changes caused by prior SQL statements within the same transaction
     * are preserved and the transaction remains active. This is the default behavior and the
     * behavior specified by the SQL standard.
     *
     * FAIL
     * When an applicable constraint violation occurs, the FAIL resolution algorithm aborts the
     * current SQL statement with an SQLITE_CONSTRAINT error. But the FAIL resolution does not back
     * out prior changes of the SQL statement that failed nor does it end the transaction. For
     * example, if an UPDATE statement encountered a constraint violation on the 100th row that it
     * attempts to update, then the first 99 row changes are preserved but changes to rows 100 and
     * beyond never occur.
     *
     */
    // If not unique, a RuntimeException is thrown by Room.

    /**
     * Inserts an item into the list table.  @Insert(onConflict = OnConflictStrategy.ABORT) is the
     * default.
     *
     * @param item a new {@link ListEntity} item
     * @return the row ID of the inserted item
     */
    @SuppressWarnings("DefaultAnnotationParam")
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(final ListEntity item);

    // WARNING: RawQuery methods can only be used for read queries. For write queries, use
    // RoomDatabase.getOpenHelper().getWritableDatabase().
    // Tried using RawQuery for DELETE.  It did delete records but return int count was zero.
    /**
     * Execute a raw query.  Cannot pass in a list of column names so <code>SELECT</code> must use
     * raw query.
     *
     * @param query the SQL statement to execute
     * @return a <code>LiveData</code> list of selected {@link ListEntity}
     * items in the database
     */
    @RawQuery(observedEntities = ListEntity.class)
    LiveData<List<ListEntity>> doRawQuery(final SupportSQLiteQuery query);

    // Note: Only int not long is supported by Room.
    // Note: If no item is selected, id passed in is RecyclerView.NO_ID (-1) and zero is returned.
    /**
     * Update the {@link ListEntity} item. The item is identified by the row
     * ID.  @Update(onConflict = OnConflictStrategy.ABORT) is the default.
     *
     * @param item the {@link ListEntity} item to update
     * @return the number of items updated
     */
    @SuppressWarnings("DefaultAnnotationParam")
    @Update(onConflict = OnConflictStrategy.ABORT)
    int update(final ListEntity item);

    /**
     * Delete the {@link ListEntity} item(s).
     *
     * @param listEntities the list of {@link ListEntity} item(s) to delete
     * @return the number of {@link ListEntity} items deleted
     */
    @Delete
    int delete(final List<ListEntity> listEntities);
}
