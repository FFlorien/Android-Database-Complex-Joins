package be.florien.databasecomplexjoins.primitivefield;

import android.database.Cursor;

import be.florien.databasecomplexjoins.architecture.DBPrimitiveField;

public class NullField extends DBPrimitiveField<Void> {

    public NullField(String fieldName) {
        super(fieldName);
    }

    @Override
    protected void extractRowValue(Cursor cursor, int column) {
        mCurrentObject = null;
        setComplete();
    }

}
