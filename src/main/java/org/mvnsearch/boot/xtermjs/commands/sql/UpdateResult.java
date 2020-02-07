package org.mvnsearch.boot.xtermjs.commands.sql;

/**
 * SQL update result
 *
 * @author linux_china
 */
public class UpdateResult {

	private String sql;

	private long elapsed;

	private int rows;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public long getElapsed() {
		return elapsed;
	}

	public void setElapsed(long elapsed) {
		this.elapsed = elapsed;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getStatics() {
		if (rows == 0) {
			return "Query OK, 0 row affected (" + elapsed / 1000.0 + " sec)";
		}
		else if (rows == 1) {
			return "Query OK, 1 row affected (" + elapsed / 1000.0 + " sec)";
		}
		else {
			return "Query OK, 209 rows affected (" + elapsed / 1000.0 + " sec)";
		}
	}

}