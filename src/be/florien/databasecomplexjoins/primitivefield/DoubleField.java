
package be.florien.databasecomplexjoins.primitivefield;

import android.database.Cursor;

import be.florien.databasecomplexjoins.architecture.DBPrimitiveField;

public class DoubleField extends DBPrimitiveField<Double> {

    public DoubleField(String fieldName) {
        super(fieldName);
    }

    @Override
    public void extractRowValue(Cursor cursor, int column) {
        mCurrentObject = cursor.getDouble(column);
        setComplete();
    }

}
