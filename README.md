# Android Database: Complex Joins

ORM(ish) library for Android to quickly and easily make the link between a new or existing database and an Object model to manipulate in your application.

Mains goals of Android Database: Complex Joins (AD:CJ) are to provide an easy way to join multiple tables with only one request to the database, transpose the datas from the database in [POJO](http://en.wikipedia.org/wiki/POJO), do all the classic [CRUD](http://en.wikipedia.org/wiki/CRUD) operations and handle a large amount of use-case, all the while being performant and easy to implement.

**This is still a work in progress. More features are on their ways.**

## Features
* Retrieve datas from one or multiple tables in one request
* Handle one to one, one to many and many to one relations with inner Object and List of Object supported
* Parse joined tables in one request, thus reducing the database access
* Adaptive to your Object model

## The basics

Your model should have its fields' name as follow:

* the table corresponding field's name
* the outer table's name if you want to retrieve the data of another table
* or the outer table's alias (useful if you query the same table multiple times)

Your table representation should implement:

* an empty constructor calling super(ModelObject.class, "table_name")
* getId(): return the column name for the ID
* getJoinToInnerTable(DBTable<?> innerTable): should determine which table is innerTable representating and give the appropriate join statement
* selectId(): method calling selectId(String) or selectid(String,String)
* any other selection you'd like to do on this table

## Quick example

### The tables

|employee|Represent a person working in the company|
|----|-----|
|id|The unique identifier for this book|
|name|This employee name|
|company_id|This employee's company|

|company|Represent a company|
|----|-----|
|id|The unique identifier for this company|
|name|This company name|
|adress|This company adress|

### The Object model

```java
public class Employee{
    public int id;
    public String name;
	public Company company;
}
```
***Please note:***

* The company information is under the name company, and not company_id. We could also have retrieved the company_id as a int if it was needed

```java
public class Company{
	public int id;
    public String name;
    public String adress;
	public List<Employee> employee;
}
```

***Please note:***

* The employees informations are stored in a List. This doesn't require more code as AD:CJ will automatically make the difference between list and object when parsing the datas from the database

### The Table representation

The user is free to do one select per field or one select per use-case. Only selectId() is required.

Employee with one select per field:

```java
public class EmployeeTable extends DBTable<Employee>{

	public static final String TABLE_NAME = "employee";
	
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_COMPANY_ID = "company_id";

	public EmployeeTable(){
		super(Employee.class, TABLE_NAME);
	}
	
	@Override
	public String getId(){
		return COLUMN_ID;
	}
	
	@Override
	public String getJoinToInnerTable(DBTable<?> table){
		if(table instanceof CompanyTable){
			return getJoinOnRef(table, COLUMN_COMPANY_ID, true);
		}
		return null;
	}
	
	@Override
	public EmployeeTable selectId(){
		selectId(COLUMN_ID);
		return this;
	}
	
	public EmployeeTable selectName(){
		selectString(COLUMN_NAME);
		return this;
	}
	
	public EmployeeTable selectCompany(CompanyTable table){
		selectTable(table);
		return this;
	}
}
```

Company with one select per use-case:

```java
public class CompanyTable extends DBTable<Company>{

	public static final String TABLE_NAME = "company";
	
	public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADRESS = "adress";

	public CompanyTable(){
		super(Company.class, TABLE_NAME);
	}
	
	@Override
	public String getId(){
		return COLUMN_ID;
	}
	
	@Override
	public String getJoinToInnerTable(DBTable<?> table){
		if(table instanceof CompanyTable){
			return getJoinOnId(table, EmployeeTable.COLUMN_COMPANY_ID, true);
		}
		return null;
	}
	
	@Override
	public CompanyTable selectId(){
		selectId(COLUMN_ID);
		return this;
	}
	
	public CompanyTable selectAll(EmployeeTable table){
    	selectId(COLUMN_ID);
        selectString(COLUMN_NAME);
        selectString(COLUMN_ADRESS);
		selectTable(table);
		return this;
	}
}
```

### The building

```java
    public List<T> query(DBTable<T> table) {
        try {
			EmployeeTable table = new EmployeeTable().selectId().selectName()
				.selectCompany(
					new CompanyTable().selectId().selectName());
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(table.getJoinComplete());
            Cursor cursor = queryBuilder.query(
            	mDBHelper.getDatabase(), 
            	table.getProjection(), 
            	table.getWhere(), 
            	null, 
            	null, 
            	null, 
            	table.getOrderBy());
            return table.getResult(cursor);
        } finally {
            mDBHelper.close();
        }
    }
```


## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
