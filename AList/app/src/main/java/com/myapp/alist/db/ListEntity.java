/*
 * Copyright (c) 2023 Colin Walters.  All rights reserved.
 */

package com.myapp.alist.db;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.myapp.alist.BasicApplication;
import com.myapp.alist.R;

import java.util.HashMap;

/**
 * Represents one record of the list table.  Name is unique.
 *
 * @author Colin Walters
 * @version 1.0, 17/04/2023
 * @since 1.0
 */
@Entity(tableName = ListEntity.TABLE_NAME, indices = {@Index(value = {"name"}, unique = true)})
public class ListEntity {
    /**
     * The name of the list table.
     */
    public static final String TABLE_NAME = "list";

    /**
     * The name of the id column.
     */
    public static final String _ID = "_id";

    /**
     * The name of the name column.
     */
    public static final String NAME = "name";

    /**
     * The name of the description column.
     */
    public static final String DESCRIPTION = "description";

    /**
     * The name of the category column.
     */
    public static final String CATEGORY = "category";

    /**
     * The name of the status column.
     */
    public static final String STATUS = "status";

    /**
     * The id column.
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = _ID)
    private long id;

    /**
     * The name column.
     */
    @ColumnInfo(name = NAME)
    private String name;

    /**
     * The description column.
     */
    @ColumnInfo(name = DESCRIPTION)
    private String description;

    /**
     * The category column.
     */
    @ColumnInfo(name = CATEGORY)
    private String category;

    /**
     * The status column.
     */
    @ColumnInfo(name = STATUS)
    private String status;

    // Can't have a private constructor because building app gives:
    // error: Entities and Pojos must have a usable public constructor. You can have an empty
    // constructor or a constructor whose parameters match the fields (by name and type).
    /*private ListEntity() {
    }*/
    // Limit constructor to package access.
    ListEntity() {
    }

    /**
     * Create a new {@link ListEntity} from the specified <code>HashMap</code>.
     *
     * @param values a <code>HashMap</code> that contains {@link ListEntity} details to validate
     * @return a new {@link ListEntity} instance
     */
    @Nullable
    public static ListEntity verifyValues(@Nullable final HashMap<String, String> values) {
        boolean ok = true;
        final Context context = BasicApplication.getContext();
        final Resources resources;
        ListEntity listEntity = null;

        if (values == null) {
            throw new RuntimeException("verifyValues error 1");
        }

        if (context == null) {
            throw new RuntimeException("verifyValues error 2");
        } else {
            resources = context.getResources();
        }

        if (resources == null) {
            throw new RuntimeException("verifyValues error 3");
        }

        if (values.containsKey(ListEntity.NAME)) {
            String name = values.get(ListEntity.NAME);
            if (name == null) {
                throw new RuntimeException("verifyValues error 4");
            } else {
                name = name.trim();
                if (TextUtils.isEmpty(name)) {
                    ok = false;
                }
            }
        } else {
            throw new RuntimeException("verifyValues error 5");
        }

        if (values.containsKey(ListEntity.DESCRIPTION)) {
            String description = values.get(ListEntity.DESCRIPTION);
            if (description == null) {
                throw new RuntimeException("verifyValues error 6");
            } else {
                // Check the length to allow "" but not " " etc for the description.
                if (description.length() != 0 && TextUtils.isEmpty(description.trim())) {
                    ok = false;
                }
            }
        } else {
            throw new RuntimeException("verifyValues error 7");
        }

        if (values.containsKey(ListEntity.CATEGORY)) {
            String category = values.get(ListEntity.CATEGORY);
            if (category == null) {
                throw new RuntimeException("verifyValues error 8");
            } else {
                category = category.trim();
                if (TextUtils.isEmpty(category)) {
                    ok = false;
                } else {
                    if (category.compareToIgnoreCase(
                            resources.getStringArray(R.array.category_list)[0]) == 0) {
                        ok = false;
                    }
                }
            }
        } else {
            throw new RuntimeException("verifyValues error 9");
        }

        if (values.containsKey(ListEntity.STATUS)) {
            String status = values.get(ListEntity.STATUS);
            if (status == null) {
                throw new RuntimeException("verifyValues error 10");
            } else {
                status = status.trim();
                if (TextUtils.isEmpty(status)) {
                    ok = false;
                } else {
                    if (status.compareToIgnoreCase(
                            resources.getStringArray(R.array.status_list)[0]) == 0) {
                        ok = false;
                    }
                }
            }
        } else {
            throw new RuntimeException("verifyValues error 11");
        }

        if (ok) {
            listEntity = new ListEntity();

            listEntity.setName(values.get(ListEntity.NAME));
            listEntity.setDescription(values.get(ListEntity.DESCRIPTION));
            listEntity.setCategory(values.get(ListEntity.CATEGORY));
            listEntity.setStatus(values.get(ListEntity.STATUS));
        }

        return listEntity;
    }

    /**
     * Get the id of the list.
     */
    public long getId() {
        return id;
    }

    /**
     * Set the id of the list.
     */
    public void setId(final long id) { this.id = id; }

    /**
     * Get the name of the list.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the list.
     */
    // This needs to be public.
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the description of the list.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the list.
     */
    // This needs to be public.
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Get the category of the list.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Set the category of the list.
     */
    // This needs to be public.
    public void setCategory(final String category) {
        this.category = category;
    }

    /**
     * Get the status of the list.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the status of the list.
     */
    // This needs to be public.
    public void setStatus(final String status) {
        this.status = status;
    }
}
