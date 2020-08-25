package com.planetbiru.pushserver.database;

import java.util.Map;

/**
 * QueryBuilder is SQL command builder based on the database type.
 * @author Kamshory, MT
 *
 */
public class QueryBuilder 
{
	private static final String DATABASE_TYPE_NOT_SUPPORTED = "Database type is not supported";
	/**
	 * Database type : mysql
	 */
	private static final String DB_TYPE_MYSQL = "mysql";

	/**
	 * Database type : postgresql
	 */
	private static final String DB_TYPE_POSTGRESQL = "postgresql";

	/**
	 * Database type : mariadb
	 */
	private static final String DB_TYPE_MARIADB = "mariadb";
	
	/**
	 * QueryBuilder buffer
	 */
	private String queryBuffer = "";
	
	/**
	 * QueryBuilder limit
	 */
	private long limit = 0;
	
	/**
	 * QueryBuilder offset
	 */
	private long offset = 0;
	
	/**
	 * Database type
	 */
	private String databaseType = DB_TYPE_MARIADB;
	
	/**
	 * String containing limit and offset based on the database type of the connection
	 */
	private String limitOffset = "";
	
	/**
	 * true if query hash limit
	 */
	private boolean hasLimit = false;	
	
	/**
	 * Default constructor.
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder() throws DatabaseTypeException
	{
		this.queryBuffer = "";
		this.hasLimit = false;
		if(!this.databaseType.equals(DB_TYPE_MYSQL) && !this.databaseType.equals(DB_TYPE_MARIADB) && !this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
	}
	/**
	 * Construct an object and initialize database type.
	 * @param databaseType Type of the database
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder(String databaseType) throws DatabaseTypeException
	{
		this.queryBuffer = "";
		this.databaseType = databaseType;
		this.hasLimit = false;
		if(!this.databaseType.equals(DB_TYPE_MYSQL) && !this.databaseType.equals(DB_TYPE_MARIADB) && !this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
	}
	/**
	 * Escape string for database query.
	 * @param input String to be escaped
	 * @return Escaped string
	 * @throws DatabaseTypeException if database type is not supported
	 * @throws NullPointerException if query is null
	 */
	public String escapeSQL(String input) throws DatabaseTypeException
	{
		if(input == null)
		{
			return "";
		}
		String s = input;		
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			s = s.replaceAll("\\u005c", "\\\\\\\\");
			s = s.replaceAll("\\n", "\\\\n");
		    s = s.replaceAll("\\r", "\\\\r");
		    s = s.replaceAll("\\00", "\\\\0");
		    s = s.replace("'", "\\'");
		    s = s.replace("\"", "\\\"");
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			s = s.replaceAll("\\u005c", "\\\\\\\\");
		    s = s.replaceAll("\\00", "\\\\0");
		    s = s.replace("'", "''");
		    s = s.replace("\"", "\\\"");
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
	    return s;	
	}
	/**
	 * Remove escape character from a string.
	 * @param input String to be normalized
	 * @return Normal string
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public String deescapeSQL(String input) throws DatabaseTypeException
	{
		String s = input;
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			s = s.replaceAll("\\\\n", "\\n");
			s = s.replaceAll("\\\\r", "\\r");
			s = s.replaceAll("\\\\00", "\\0");
			s = s.replace("\\'", "'");	
		    s = s.replace("\\\"", "\"");
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			s = s.replaceAll("\\\\00", "\\0");
			s = s.replace("''", "'");
		    s = s.replace("\\\"", "\"");
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return s;	
	}
	/**
	 * Clear buffer to start create a new query.
	 * @return QueryBuilder object
	 */
	public QueryBuilder newQuery()
	{
		this.queryBuffer = "";
		this.hasLimit = false;
		return this;
	}
	/**
	 * End SQL with semicolon. When this method called, query can not be continued.
	 * @return QueryBuilder object
	 */
	public QueryBuilder endQueryBuilder()
	{
		this.queryBuffer += ";";
		return this;				
	}
	/**
	 * Clear buffer and start a new query. Be careful to create native query because each of database system has different structure. 
	 * @param query Initial query string 
	 * @return QueryBuilder object
	 */
	public QueryBuilder newQuery(String query)
	{
		this.queryBuffer = query;
		this.hasLimit = false;
		return this;
	}
	/**
	 * Create query SELECT. This function will add string SELECT and some fields to the buffer.
	 * @return QueryBuilder object
	 */
	public QueryBuilder insert()
	{
		this.queryBuffer += "insert ";
		return this;
	}
	/**
	 * Add INTO to the buffer followed by table name
	 * @param query Table name
	 * @return QueryBuilder object
	 */
	public QueryBuilder into(String query)
	{
		this.queryBuffer += "into "+query+"\r\n";
		return this;
	}
	/**
	 * Add field to the buffer
	 * @param query Field
	 * @return QueryBuilder object
	 */
	public QueryBuilder fields(String query)
	{
		this.queryBuffer += ""+query+"\r\n";
		return this;
	}
	/**
	 * Add values to the buffer
	 * @param query Values
	 * @return QueryBuilder object
	 */
	public QueryBuilder values(String query)
	{
		this.queryBuffer += "values "+query+"\r\n";
		return this;
	}
	/**
	 * Set data to be inserted
	 * @param data Data to be inserted
	 * @return QueryBuilder object
	 * @throws NullPointerException if any null pointer
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder dataInsert(Map <String, String> data) throws DatabaseTypeException
	{
		String field = "";
		String value = "";
		String[] fields = new String[data.size()];
		String[] values = new String[data.size()];
		int i = 0;
		
		
		for (Map.Entry<String, String> pair : data.entrySet()) {
	        field = pair.getKey();
	        value = pair.getValue();
	        value = this.escapeSQL(value);
	        fields[i] = field;
	        values[i] = "'"+value+"'";
		}
	    this.fields("("+String.join(", ", fields)+")");
	    this.values("("+String.join(", ", values)+")");
		return this;
	}
	/**
	 * Set data to be updated
	 * @param data Data to be updated
	 * @return QueryBuilder object
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder dataUpdate(Map <String, String> data) throws DatabaseTypeException
	{
		String field = "";
		String value = "";
		String[] fields = new String[data.size()];
		int i = 0;
		for (Map.Entry<String, String> pair : data.entrySet()) {
	        field = pair.getKey();
	        value = pair.getValue();
	        value = this.escapeSQL(value);
	        fields[i] = field+" = "+"'"+value+"'";
		}
	    this.set(String.join(", ", fields));
		return this;
	}
	/**
	 * Add AS to the buffer followed by alias name
	 * @param query Alias name
	 * @return QueryBuilder object
	 */
	public QueryBuilder alias(String query)
	{
		this.queryBuffer += String.format("as %s%n", query);
		return this;
	}
	/**
	 * Add SELECT to the buffer followed by field, alias, constant or function
	 * @param query Field, alias or function
	 * @return QueryBuilder object
	 */
	public QueryBuilder select(String query)
	{
		this.queryBuffer += String.format("select %s%n", query);
		return this;
	}
	/**
	 * Create query DELETE to the buffer
	 * @return QueryBuilder object
	 */
	public QueryBuilder delete()
	{
		this.queryBuffer += "delete \r\n";
		return this;
	}
	/**
	 * Create query UPDATE. This function will add UPDATE to the buffer followed by table name.
	 * @param query Table name where data will be updated. 
	 * @return QueryBuilder object 
	 */
	public QueryBuilder update(String query)
	{
		this.queryBuffer += "update "+query+"\r\n";
		return this;
	}
	/**
	 * Create query SET field value. This function will add SET to the buffer followed by field and new value.
	 * @param query Field where data will be updated
	 * @return QueryBuilder object
	 */
	public QueryBuilder set(String query)
	{
		this.queryBuffer += "set "+query+"\r\n";
		return this;
	}
	/**
	 * Create query FROM table or view. This function will add FROM  to the buffer followed by table name or view name.
	 * @param query Table name or view name
	 * @return QueryBuilder object
	 */
	public QueryBuilder from(String query)
	{
		this.queryBuffer += "from "+query+"\r\n";
		return this;
	}
	/**
	 * Create query JOIN. This function will add JOIN to the buffer followed by table name or view name and condition.
	 * @param query Table name or view name and condition
	 * @return QueryBuilder object
	 */
	public QueryBuilder join(String query)
	{
		this.queryBuffer += "join "+query+"\r\n";
		return this;
	}
	/**
	 * Create query LEFT JOIN. This function will add LEFT JOIN to the buffer followed by table name or view name and conditio.
	 * @param query Table name or view name and condition
	 * @return QueryBuilder object
	 */
	public QueryBuilder leftJoin(String query)
	{
		this.queryBuffer += "left join "+query+"\r\n";
		return this;
	}
	/**
	 * Create query RIGHT JOIN. This function will add RIGHT JOIN to the buffer followed by table name or view name and condition.
	 * @param query Table name or view name and condition
	 * @return QueryBuilder object
	 */
	public QueryBuilder rightJoin(String query)
	{
		this.queryBuffer += "right join "+query+"\r\n";
		return this;
	}
	/**
	 * Create query INNER JOIN. This function will add INNER JOIN to the buffer followed by table name or view name and condition.
	 * @param query Table name or view name and condition
	 * @return QueryBuilder object
	 */
	public QueryBuilder innerJoin(String query)
	{
		this.queryBuffer += "inner join "+query+"\r\n";
		return this;
	}
	/**
	 * Create query OUTER JOIN. This function will add OUTER JOIN to the buffer followed by table name or view name and condition.
	 * @param query Table name or view name and condition
	 * @return QueryBuilder object
	 */
	public QueryBuilder outerJoin(String query)
	{
		this.queryBuffer += "outer join "+query+"\r\n";
		return this;
	}
	/**
	 * Create query ON. This function will add ON to the buffer followed by table name or view name and condition.
	 * @param query Table name or view name and condition
	 * @return QueryBuilder object
	 */	
	public QueryBuilder on(String query)
	{
		this.queryBuffer += "on "+query+"\r\n";
		return this;
	}
	/**
	 * Create query WHERE. This function will add WHERE to the buffer followed by condition.
	 * @param query Condition to filter result
	 * @return QueryBuilder object
	 */
	public QueryBuilder where(String query)
	{
		this.queryBuffer += "where "+query+"\r\n";
		return this;
	}
	/**
	 * Create query GROUP BY. This function will add GROUP BY to the buffer followed by field to group the result to the buffer. Be careful! Each database system has different way to group the result.
	 * @param query Field to group the result
	 * @return QueryBuilder object
	 */
	public QueryBuilder groupBy(String query)
	{
		this.queryBuffer += "group by "+query+"\r\n";
		return this;
	}
	/**
	 * Create query ORDER BY. This function will add ORDER BY to the buffer followed by field to order the result. Be careful! Some database system can sort by alias but the others can not.
	 * @param query Sort order the result by field and direction
	 * @return QueryBuilder object
	 */
	public QueryBuilder orderBy(String query)
	{
		this.queryBuffer += "order by "+query+"\r\n";
		return this;
	}
	/**
	 * Set the limit of the result. This function will add LIMIT and OFFSET to the buffer according to database system.
	 * @param query Maximum number of the result will be returned by SELECT query
	 * @return QueryBuilder object
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder limit(long query) throws DatabaseTypeException
	{
		this.limit = query;
		this.hasLimit = true;
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			this.limitOffset = String.format("limit %d, %d", this.offset, this.limit);
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			this.limitOffset = String.format("limit %d offset %d", this.limit, this.offset);
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return this;
	}
	/**
	 * Set the offset of the result. This function will add LIMIT and OFFSET to the buffer according to database system.
	 * @param query Offset of the result will be returned by SELECT query
	 * @return QueryBuilder object
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder offset(long query) throws DatabaseTypeException
	{
		this.offset = query;
		this.hasLimit = true;
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			this.limitOffset = "limit "+this.offset+", "+this.limit+"";
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			this.limitOffset = "limit "+this.limit+" offset "+this.offset+"";
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return this;
	}
	/**
	 * This function will create query to execute a procedure to the buffer according to database system.
	 * @param procedure Procedure name
	 * @param params Parameters of the procedure
	 * @return QueryBuilder object
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder executeProcedure(String procedure, String params) throws DatabaseTypeException
	{
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			this.queryBuffer = String.format("call %s(%s)", procedure, params);
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			this.queryBuffer = String.format("select %s(%s)", procedure, params);
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return this;
	}
	/**
	 * This function will create query to execute a function to the buffer according to database system.
	 * @param function Function name
	 * @param params Parameters of the function
	 * @return QueryBuilder object
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder executeFunction(String function, String params) throws DatabaseTypeException
	{
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			this.queryBuffer = "select "+function+"("+params+")\r\n";
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			this.queryBuffer = "select "+function+"("+params+")\r\n";
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return this;
	}
	/**
	 * Lock table
	 * @param query Table name followed by the operation to be locked
	 * @return QueryBuilder object
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder lockTable(String query) throws DatabaseTypeException
	{
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			this.queryBuffer = "lock table "+query;
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			this.queryBuffer = "lock table "+query;
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return this;
	}
	/**
	 * Lock tables
	 * @param query Table name followed by the operation to be locked
	 * @return QueryBuilder object
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder lockTables(String query) throws DatabaseTypeException
	{
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			this.queryBuffer = "lock tables "+query;
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			this.queryBuffer = "lock tables "+query;
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return this;
	}
	/**
	 * Unlock tables
	 * @return QueryBuilder object
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder unlockTables() throws DatabaseTypeException
	{
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			this.queryBuffer = "unlock tables";
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			this.queryBuffer = "unlock tables";
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return this;
	}
	/**
	 * This function will create query to start transaction to the buffer according to database system.
	 * @return QueryBuilder object
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder startTransaction() throws DatabaseTypeException
	{
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			this.queryBuffer = "start transaction\r\n";
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			this.queryBuffer = "start transaction\r\n";
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return this;		
	}
	/**
	 * This function will create query to commit transaction to the buffer according to database system.
	 * @return QueryBuilder object
	 */
	public QueryBuilder commit()
	{
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			this.queryBuffer = "commit\r\n";
		}
		if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			this.queryBuffer = "commit\r\n";
		}
		return this;		
	}
	/**
	 * This function will create query to roll back transaction to the buffer according to database system.
	 * @return QueryBuilder object
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder rollback() throws DatabaseTypeException
	{
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			this.queryBuffer = "rollback\r\n";
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			this.queryBuffer = "rollback\r\n";
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return this;		
	}
	/**
	 * This function will create query to get last auto increment ID create by the connection to the buffer according to database system.
	 * @return QueryBuilder object
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder lastID() throws DatabaseTypeException
	{
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			this.queryBuffer = "select last_insert_id()\r\n";
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			this.queryBuffer = "select lastval()\r\n";
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return this;		
	}
	/**
	 * Truncate table. This command will delete all records and set autoincrement (if any) to 1
	 * @param tableName table name
	 * @return QueryBuilder object
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder truncate(String tableName) throws DatabaseTypeException
	{
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			this.queryBuffer = "truncate "+tableName;
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			this.queryBuffer = "truncate "+tableName;
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return this;				
	}
	/**
	 * Get column from schematic to get column name
	 * @return SQL to get column name field
	 */
	public QueryBuilder columnName()
	{
		this.queryBuffer = "column_name";
		return this;	
	}
	/**
	 * Get column listNum of selected table
	 * @param databaseName Database name
	 * @param tableName Table name
	 * @return SQL to get listNum of column name of a table on specified database
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder getColumnList(String databaseName, String tableName) throws DatabaseTypeException
	{
		return this.getColumnList(databaseName, tableName, "");				
	}
	/**
	 * Get column listNum of selected table
	 * @param databaseName Database name
	 * @param tableName Table name
	 * @param filter Filter
	 * @return SQL to get listNum of column name of a table on specified database
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public QueryBuilder getColumnList(String databaseName, String tableName, String filter) throws DatabaseTypeException
	{
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			this.queryBuffer = "SELECT COLUMN_NAME FROM information_schema.columns WHERE table_schema='"+databaseName+"' and table_scheme='"+tableName+"' "+filter;
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			this.queryBuffer = "SELECT * FROM information_schema.columns WHERE table_name = '"+tableName+"' "+filter;
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return this;				
	}
	/**
	 * Build SQL command from the buffer started from newQueryBuilder().
	 * @return Final query string to the database
	 */
	@Override
	public String toString()
	{
		try 
		{
			
			return this.buildQueryString();
		} 
		catch (DatabaseTypeException e) 
		{
			return "";
		}	
	}
	/**
	 * Build SQL command from the buffer started from newQueryBuilder().
	 * @return Final query string to the database
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public String buildQueryString() throws DatabaseTypeException
	{
		String query = "";
		if(this.hasLimit)
		{
			if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
			{
				query = this.queryBuffer + this.limitOffset + "";
			}
			else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
			{
				query = this.queryBuffer + this.limitOffset + "";
			}
			else 
			{
				throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
			}
		}
		else
		{
			query = this.queryBuffer; 
		}
		return query;
	}
	/**
	 * Build SQL command from the buffer started from newQueryBuilder() and ended by semicolon.
	 * @param semiColon true to add semicolon
	 * @return Final query string to the database
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public String buildQueryString(boolean semiColon) throws DatabaseTypeException
	{
		String query = "";
		if(this.hasLimit)
		{
			if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
			{
				query = this.queryBuffer + this.limitOffset + "";
			}
			else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
			{
				query = this.queryBuffer + this.limitOffset + "";
			}
			else 
			{
				throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
			}
		}
		else
		{
			query = this.queryBuffer; 
		}
		if(semiColon)
		{
			query += ";";
		}
		return query;
	}
	/**
	 * Get function name to get current timestamp
	 * @return Function name to get current timestamp
	 */
	public String now()
	{
		String query = "";
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB) || this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			query = "now()";
		}
		return query;
	}
	/**
	 * Get reserved word for current date
	 * @return Reserved word for current date
	 */
	public String currentDate()
	{
		String query = "";
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB) || this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			query = "CURRENT_DATE";
		}
		return query;
	}
	/**
	 * Get reserved word for current time
	 * @return Reserved word for current time
	 */
	public String currentTime()
	{
		String query = "";
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB) || this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			query = "CURRENT_TIME";
		}
		return query;
	}
	/**
	 * Get reserved word for current timestamp
	 * @return Reserved word for current timestamp
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public String currentTimestamp() throws DatabaseTypeException
	{
		String query = "";
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB) || this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			query = "CURRENT_TIMESTAMP";
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return query;
	}
	/**
	 * Current date time with microsecond
	 * @param precision Second precision
	 * @return Current date time with microsecond
	 * @throws DatabaseTypeException if database type is not supported
	 */
	public String now(int precision) throws DatabaseTypeException
	{
		String query = "";
		if(this.databaseType.equals(DB_TYPE_MYSQL) || this.databaseType.equals(DB_TYPE_MARIADB))
		{
			query = "now("+precision+")";
		}
		else if(this.databaseType.equals(DB_TYPE_POSTGRESQL))
		{
			query = "CURRENT_TIMESTAMP";
		}
		else 
		{
			throw new DatabaseTypeException(DATABASE_TYPE_NOT_SUPPORTED);
		}
		return query;		
	}
}
