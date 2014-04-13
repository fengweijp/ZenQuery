package com.zenquery.model.dao.impl;

import com.zenquery.model.DatabaseConnection;
import com.zenquery.model.Query;
import com.zenquery.model.dao.DatabaseConnectionDAO;
import com.zenquery.model.dao.QueryDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by willy on 13.04.14.
 */
public class JdbcDatabaseConnectionDAO implements DatabaseConnectionDAO {
    private DataSource dataSource;

    private QueryDAO queryDAO;

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setQueryDAO(QueryDAO queryDAO) {
        this.queryDAO = queryDAO;
    }

    public DatabaseConnection find(Integer id) {
        String sql = "SELECT * FROM database_connections WHERE id = ?";

        jdbcTemplate = new JdbcTemplate(dataSource);

        DatabaseConnection databaseConnection =
                jdbcTemplate.query(sql, new Object[] { id }, new DatabaseConnectionMapper()).get(0);

        return databaseConnection;
    }

    public List<DatabaseConnection> findAll() {
        String sql = "SELECT * FROM database_connections";

        jdbcTemplate = new JdbcTemplate(dataSource);

        List<DatabaseConnection> databaseConnections =
                jdbcTemplate.query(sql, new DatabaseConnectionMapper());

        return databaseConnections;
    }

    public void insert(DatabaseConnection databaseConnection) {
        String sql = "INSERT INTO database_connections (name, url, username, password) VALUES (?, ?, ?, ?)";

        jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.update(sql, new Object[] {
                databaseConnection.getName(),
                databaseConnection.getUrl(),
                databaseConnection.getUsername(),
                databaseConnection.getPassword()
        });
    }

    public void update(Integer id, DatabaseConnection databaseConnection) {
        String sql = "UPDATE database_connections SET name = ?, url = ?, username = ?, password = ? WHERE id = ?";

        jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.update(sql, new Object[]{
                databaseConnection.getName(),
                databaseConnection.getUrl(),
                databaseConnection.getUsername(),
                databaseConnection.getPassword(),
                id
        });
    }

    public void delete(Integer id) {
        String sql = "DELETE FROM database_connections WHERE id = ?";

        jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.update(sql);
    }

    private static class DatabaseConnectionMapper implements ParameterizedRowMapper<DatabaseConnection> {
        @Autowired
        private QueryDAO queryDAO;

        public DatabaseConnection mapRow(ResultSet rs, int rowNum) throws SQLException {
            DatabaseConnection databaseConnection = new DatabaseConnection();

            databaseConnection.setId(rs.getInt("id"));
            databaseConnection.setName(rs.getString("name"));
            databaseConnection.setUrl(rs.getString("url"));
            databaseConnection.setUsername(rs.getString("username"));
            databaseConnection.setPassword(rs.getString("password"));

            List<Query> queries = queryDAO.findByDatabaseConnectionId(databaseConnection.getId());
            databaseConnection.setQueries(queries);

            return databaseConnection;
        }
    }
}