package org.mvnsearch.boot.xtermjs.commands.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvnsearch.boot.xtermjs.commands.CustomizedCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * database commands
 *
 * @author linux_china
 */
@ShellComponent("db: execute database commands")
public class DatabaseCommands implements CustomizedCommand {

	@Autowired(required = false)
	private DataSource ds;

	@Override
	public String[] getNames() {
		return new String[] { "db-select", "select", "db-update", "db-insert", "db-delete", "db-desc" };
	}

	@Override
	public @Nullable Object execute(@NotNull String command, @Nullable String arguments) throws Exception {
		if (command.equals("db-select") || command.equals("select")) {
			return select(trimSemicolon("select " + arguments));
		}
		else {
			return execute(trimSemicolon(command.replace("db-", "") + " " + arguments));
		}
	}

	public String select(String sql) throws Exception {
		QueryResult rowResult = new QueryResult();
		rowResult.setSQL(sql);
		try (Connection conn = ds.getConnection()) {
			long start = System.currentTimeMillis();
			Statement statement = conn.createStatement();
			ResultSet resultset = statement.executeQuery(sql);
			ResultSetMetaData metaData = resultset.getMetaData();
			int columnCount = metaData.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				rowResult.addColumn(metaData.getColumnLabel(i));
				rowResult.addColumnType(metaData.getColumnTypeName(i));
			}
			while (resultset.next()) {
				List<Object> row = new ArrayList<>();
				for (int i = 1; i <= rowResult.getColumns().size(); i++) {
					row.add(resultset.getObject(i));
				}
				rowResult.addRow(row);
			}
			resultset.close();
			statement.close();
			long end = System.currentTimeMillis();
			rowResult.setElapsed(end - start);
		}
		if (rowResult.getRows().size() == 0) {
			return rowResult.getStatics();
		}
		else {
			return rowResult.getTable() + "\r\n" + rowResult.getStatics();
		}

	}

	public String execute(String sqlUpdate) throws Exception {
		UpdateResult updateResult = new UpdateResult();
		updateResult.setSql(sqlUpdate);
		sqlUpdate = trimSemicolon(sqlUpdate);
		try (Connection conn = ds.getConnection()) {
			long start = System.currentTimeMillis();
			Statement statement = conn.createStatement();
			updateResult.setRows(statement.executeUpdate(sqlUpdate));
			statement.close();
			long end = System.currentTimeMillis();
			updateResult.setElapsed(end - start);
			conn.close();
			return updateResult.getStatics();
		}
	}

	public String trimSemicolon(String sql) {
		if (sql.endsWith(";")) {
			sql = sql.substring(0, sql.length() - 1);
		}
		return sql;
	}

}
