package org.mvnsearch.boot.xtermjs.commands.sql;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL query result
 *
 * @author linux_china
 */
public class QueryResult {

	private String SQL;

	private long elapsed;

	private List<String> columns = new ArrayList<String>();

	private List<String> columnTypes = new ArrayList<String>();

	private List<List<Object>> rows = new ArrayList<List<Object>>();

	public String getSQL() {
		return SQL;
	}

	public void setSQL(String SQL) {
		this.SQL = SQL;
	}

	public long getElapsed() {
		return elapsed;
	}

	public void setElapsed(long elapsed) {
		this.elapsed = elapsed;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public void addColumn(String name) {
		this.columns.add(name);
	}

	public List<String> getColumnTypes() {
		return columnTypes;
	}

	public void setColumnTypes(List<String> columnTypes) {
		this.columnTypes = columnTypes;
	}

	public void addColumnType(String columnType) {
		this.columnTypes.add(columnType);
	}

	public List<List<Object>> getRows() {
		return rows;
	}

	public void setRows(List<List<Object>> rows) {
		this.rows = rows;
	}

	public void addRow(List<Object> row) {
		this.rows.add(row);
	}

	@Override
	public String toString() {
		return "columns:" + columns.size() + ", rows: " + rows.size();
	}

	@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
	public String getTable() {
		List<Integer> columnsWidth = new ArrayList<Integer>();
		for (String column : columns) {
			columnsWidth.add(column.length());
		}
		for (List<Object> row : rows) {
			for (int i = 0; i < row.size(); i++) {
				String cellValue = getCellValue(i, row.get(i));
				int cellWidth = cellValue.getBytes().length;
				if (cellWidth > columnsWidth.get(i)) {
					columnsWidth.set(i, cellWidth);
				}
			}
		}
		List<String> lines = new ArrayList<String>();
		StringBuilder header = new StringBuilder();
		for (Integer width : columnsWidth) {
			header.append(StringUtils.rightPad("+", width + 3, "-"));
		}
		header.append("+");
		lines.add(header.toString());
		StringBuilder columnBuilder = new StringBuilder();
		for (int i = 0; i < columns.size(); i++) {
			columnBuilder.append("| " + StringUtils.rightPad(columns.get(i), columnsWidth.get(i) + 1));
		}
		columnBuilder.append("|");
		lines.add(columnBuilder.toString());
		lines.add(header.toString());
		for (List<Object> row : getRows()) {
			StringBuilder rowBuilder = new StringBuilder();
			for (int i = 0; i < row.size(); i++) {
				String cellValue = getCellValue(i, row.get(i));
				int bytelen = cellValue.getBytes().length;
				int charlen = cellValue.toCharArray().length;
				rowBuilder
					.append("| " + StringUtils.rightPad(cellValue, columnsWidth.get(i) + 1 - (bytelen - charlen)));
			}
			rowBuilder.append("|");
			lines.add(rowBuilder.toString());
		}
		lines.add(header.toString());
		return StringUtils.join(lines, "\r\n");
	}

	private String getCellValue(int i, Object value) {
		if (columnTypes.get(i).toLowerCase().contains("lob")) {
			return "LOB";
		}
		else {
			return value == null ? "" : value.toString().trim();
		}
	}

	public String getStatics() {
		if (rows.isEmpty()) {
			return "Empty set (" + elapsed / 1000.0 + " sec)";
		}
		else if (rows.size() == 1) {
			return "1 row in set (" + elapsed / 1000.0 + " sec)";
		}
		else {
			return rows.size() + " rows in set (" + elapsed / 1000.0 + " sec)";
		}
	}

}