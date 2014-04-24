
package be.florien.databasecomplexjoins.primitivefield;

import android.database.Cursor;

import be.florien.databasecomplexjoins.architecture.DBPrimitiveField;

public class StringField extends DBPrimitiveField<String> {

    public StringField(String fieldName) {
        super(fieldName);
    }

    @Override
    public void extractRowValue(Cursor cursor, int column) {
        mCurrentObject = cursor.getString(column);
        setComplete();
    }

}
