package com.eryansky.codegen.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.eryansky.codegen.vo.Column;
import com.eryansky.codegen.vo.Table;

/**
 * Mysql Metadata读取
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-7-15 下午1:39:48 
 * @version 1.0
 */
public class MysqlDataSource extends DataSource {


	public MysqlDataSource(Connection conn, String catalog, String schema) {
		super(conn, catalog, schema);
	}

	@Override
	public List<Column> getColumns(String namePattern) throws SQLException {
		List<Column> columns = new ArrayList<Column>();
		ResultSet rs = null;
		try {
			DatabaseMetaData dmd = conn.getMetaData();
			rs = dmd.getColumns(catalog, schema, namePattern, "");
			while (rs.next()) {
				Column col = new Column();
				col.setColumnName(rs.getString("COLUMN_NAME"));
				col.setJdbcType(rs.getString("TYPE_NAME"));
				col.setLength(rs.getInt("COLUMN_SIZE"));
				col.setNullable(rs.getBoolean("NULLABLE"));
				col.setDigits(rs.getInt("DECIMAL_DIGITS"));
				col.setDefaultValue(rs.getString("COLUMN_DEF"));
				col.setComment(rs.getString("REMARKS"));
				columns.add(col);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			close(null, rs);
		}
		return columns;
	}

	/**
	 * 获取主键
	 * 
	 * @param rs
	 * @return
	 * @throws java.sql.SQLException
	 */
	@SuppressWarnings("unused")
	private List<Column> getPks(ResultSet rs) throws SQLException {
		List<Column> pks = new ArrayList<Column>();
		while (rs.next()) {
			Column pk = new Column();
			pk.setColumnName(rs.getString("COLUMN_NAME"));
			pks.add(pk);
		}
		return pks;
	}

	@Override
	public List<Table> getTables(String namePattern) throws SQLException {
		List<Table> tables = new ArrayList<Table>();
		ResultSet rs = null;
		try {
			DatabaseMetaData dmd = conn.getMetaData();// 获取数据库的MataData信息
			rs = dmd.getTables(catalog, schema, namePattern, DEFAULT_TYPES);
			while (rs.next()) {
				Table table = new Table();
				table.setTableName(rs.getString("TABLE_NAME"));
				table.setSchema(rs.getString("TABLE_SCHEM"));
				table.setCatalog(rs.getString("TABLE_CAT"));
				table.setTableType(rs.getString("TABLE_TYPE"));
				table.setRemark(rs.getString("REMARKS"));
				tables.add(table);
				// System.out.println(rs.getString("TABLE_CAT") + "\t"
				// + rs.getString("TABLE_SCHEM") + "\t"
				// + rs.getString("TABLE_NAME") + "\t"
				// + rs.getString("TABLE_TYPE"));

			}

		} catch (SQLException e) {
			throw e;
		} finally {
			close(null, rs);
		}
		return tables;
	}

	@Override
	public List<Column> getForeignKeys(String namePattern) throws SQLException {
		return null;
	}

	@Override
	public List<Column> getPrimaryKey(String namePattern) throws SQLException {
		List<Column> primaryKey = new ArrayList<Column>();
		ResultSet rs = null;
		try {
			DatabaseMetaData dmd = conn.getMetaData();// 获取数据库的MataData信息
			rs = dmd.getPrimaryKeys(catalog, schema, namePattern);
			while (rs.next()) {
				Column pk = new Column();
				pk.setColumnName(rs.getString("COLUMN_NAME"));
				primaryKey.add(pk);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			close(null, rs);
		}
		return primaryKey;
	}

}
