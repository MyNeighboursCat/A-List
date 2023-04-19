package com.myapp.alist.db;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ListDao_Impl implements ListDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ListEntity> __insertionAdapterOfListEntity;

  private final EntityDeletionOrUpdateAdapter<ListEntity> __deletionAdapterOfListEntity;

  private final EntityDeletionOrUpdateAdapter<ListEntity> __updateAdapterOfListEntity;

  public ListDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfListEntity = new EntityInsertionAdapter<ListEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `list` (`_id`,`name`,`description`,`category`,`status`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ListEntity value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getDescription() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getDescription());
        }
        if (value.getCategory() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getCategory());
        }
        if (value.getStatus() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getStatus());
        }
      }
    };
    this.__deletionAdapterOfListEntity = new EntityDeletionOrUpdateAdapter<ListEntity>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `list` WHERE `_id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ListEntity value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfListEntity = new EntityDeletionOrUpdateAdapter<ListEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `list` SET `_id` = ?,`name` = ?,`description` = ?,`category` = ?,`status` = ? WHERE `_id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ListEntity value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getDescription() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getDescription());
        }
        if (value.getCategory() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getCategory());
        }
        if (value.getStatus() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getStatus());
        }
        stmt.bindLong(6, value.getId());
      }
    };
  }

  @Override
  public long insert(final ListEntity item) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfListEntity.insertAndReturnId(item);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int delete(final List<ListEntity> listEntities) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total +=__deletionAdapterOfListEntity.handleMultiple(listEntities);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int update(final ListEntity item) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total +=__updateAdapterOfListEntity.handle(item);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<ListEntity>> selectAllQuery() {
    final String _sql = "SELECT * FROM List";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"List"}, false, new Callable<List<ListEntity>>() {
      @Override
      public List<ListEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "_id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<ListEntity> _result = new ArrayList<ListEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final ListEntity _item;
            _item = new ListEntity();
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            _item.setName(_tmpName);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            _item.setDescription(_tmpDescription);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            _item.setCategory(_tmpCategory);
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            _item.setStatus(_tmpStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<ListEntity>> doRawQuery(final SupportSQLiteQuery query) {
    final SupportSQLiteQuery _internalQuery = query;
    return __db.getInvalidationTracker().createLiveData(new String[]{"list"}, false, new Callable<List<ListEntity>>() {
      @Override
      public List<ListEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _internalQuery, false, null);
        try {
          final List<ListEntity> _result = new ArrayList<ListEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final ListEntity _item;
            _item = __entityCursorConverter_comMyappAlistDbListEntity(_cursor);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }
    });
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private ListEntity __entityCursorConverter_comMyappAlistDbListEntity(Cursor cursor) {
    final ListEntity _entity;
    final int _cursorIndexOfId = CursorUtil.getColumnIndex(cursor, "_id");
    final int _cursorIndexOfName = CursorUtil.getColumnIndex(cursor, "name");
    final int _cursorIndexOfDescription = CursorUtil.getColumnIndex(cursor, "description");
    final int _cursorIndexOfCategory = CursorUtil.getColumnIndex(cursor, "category");
    final int _cursorIndexOfStatus = CursorUtil.getColumnIndex(cursor, "status");
    _entity = new ListEntity();
    if (_cursorIndexOfId != -1) {
      final long _tmpId;
      _tmpId = cursor.getLong(_cursorIndexOfId);
      _entity.setId(_tmpId);
    }
    if (_cursorIndexOfName != -1) {
      final String _tmpName;
      if (cursor.isNull(_cursorIndexOfName)) {
        _tmpName = null;
      } else {
        _tmpName = cursor.getString(_cursorIndexOfName);
      }
      _entity.setName(_tmpName);
    }
    if (_cursorIndexOfDescription != -1) {
      final String _tmpDescription;
      if (cursor.isNull(_cursorIndexOfDescription)) {
        _tmpDescription = null;
      } else {
        _tmpDescription = cursor.getString(_cursorIndexOfDescription);
      }
      _entity.setDescription(_tmpDescription);
    }
    if (_cursorIndexOfCategory != -1) {
      final String _tmpCategory;
      if (cursor.isNull(_cursorIndexOfCategory)) {
        _tmpCategory = null;
      } else {
        _tmpCategory = cursor.getString(_cursorIndexOfCategory);
      }
      _entity.setCategory(_tmpCategory);
    }
    if (_cursorIndexOfStatus != -1) {
      final String _tmpStatus;
      if (cursor.isNull(_cursorIndexOfStatus)) {
        _tmpStatus = null;
      } else {
        _tmpStatus = cursor.getString(_cursorIndexOfStatus);
      }
      _entity.setStatus(_tmpStatus);
    }
    return _entity;
  }
}
