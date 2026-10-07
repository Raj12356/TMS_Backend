package com.tms.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${DATABASE_URL:#{null}}")
    private String databaseUrl;

    @Value("${DATABASE_URL_JDBC:#{null}}")
    private String databaseUrlJdbc;

    @Value("${spring.datasource.url:#{null}}")
    private String springDatasourceUrl;

    @Value("${spring.datasource.username:#{null}}")
    private String defaultUsername;

    @Value("${spring.datasource.password:#{null}}")
    private String defaultPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");

        // 1. Check if an explicit JDBC URL is provided
        String rawJdbcUrl = isNotBlank(databaseUrlJdbc) ? databaseUrlJdbc : (isNotBlank(springDatasourceUrl) ? springDatasourceUrl : null);

        // 2. Check if a standard DATABASE_URL is provided (e.g. from Neon or Railway)
        String rawDbUrl = isNotBlank(databaseUrl) ? databaseUrl : System.getenv("DATABASE_URL");

        if (isNotBlank(rawDbUrl) && (rawJdbcUrl == null || !rawJdbcUrl.startsWith("jdbc:"))) {
            configureFromDatabaseUrl(config, rawDbUrl);
        } else if (rawJdbcUrl != null && rawJdbcUrl.startsWith("jdbc:")) {
            config.setJdbcUrl(rawJdbcUrl);
            if (isNotBlank(defaultUsername)) {
                config.setUsername(defaultUsername);
            }
            if (isNotBlank(defaultPassword)) {
                config.setPassword(defaultPassword);
            }
        } else if (isNotBlank(rawDbUrl)) {
            configureFromDatabaseUrl(config, rawDbUrl);
        } else {
            // Fallback for local development
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/neondb");
            config.setUsername(isNotBlank(defaultUsername) ? defaultUsername : "postgres");
            config.setPassword(isNotBlank(defaultPassword) ? defaultPassword : "postgres");
        }

        // Hikari pool settings optimized for Neon serverless PostgreSQL
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setMaxLifetime(60000);
        config.setConnectionTimeout(30000);

        log.info("Initialized DataSource with JDBC URL: {}", maskUrl(config.getJdbcUrl()));
        return new HikariDataSource(config);
    }

    private void configureFromDatabaseUrl(HikariConfig config, String rawUrl) {
        try {
            if (rawUrl.startsWith("jdbc:")) {
                config.setJdbcUrl(rawUrl);
                if (isNotBlank(defaultUsername)) config.setUsername(defaultUsername);
                if (isNotBlank(defaultPassword)) config.setPassword(defaultPassword);
                return;
            }

            String urlToParse = rawUrl.trim();
            if (urlToParse.startsWith("postgres://")) {
                urlToParse = "postgresql://" + urlToParse.substring("postgres://".length());
            }

            URI uri = new URI(urlToParse);
            String userInfo = uri.getUserInfo();
            if (userInfo != null && !userInfo.isBlank()) {
                String[] parts = userInfo.split(":", 2);
                config.setUsername(parts[0]);
                if (parts.length > 1) {
                    config.setPassword(parts[1]);
                }
            } else {
                if (isNotBlank(defaultUsername)) config.setUsername(defaultUsername);
                if (isNotBlank(defaultPassword)) config.setPassword(defaultPassword);
            }

            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath(); // includes leading '/'
            if (path == null || path.isBlank()) {
                path = "/neondb";
            }

            // Extract query parameters, retaining sslmode and omitting unsupported JDBC parameters
            String query = uri.getQuery();
            StringBuilder jdbcQuery = new StringBuilder();
            if (query != null && !query.isBlank()) {
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("channel_binding=")) {
                        continue;
                    }
                    if (jdbcQuery.length() > 0) jdbcQuery.append("&");
                    jdbcQuery.append(param);
                }
            }

            if (!jdbcQuery.toString().contains("sslmode=")) {
                if (jdbcQuery.length() > 0) jdbcQuery.append("&");
                jdbcQuery.append("sslmode=require");
            }

            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path + "?" + jdbcQuery;
            config.setJdbcUrl(jdbcUrl);
        } catch (Exception e) {
            log.error("Failed to parse DATABASE_URL: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Invalid DATABASE_URL configuration: " + e.getMessage(), e);
        }
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty() && !str.startsWith("${");
    }

    private String maskUrl(String url) {
        if (url == null) return "null";
        return url.replaceAll(":[^/@:]+@", ":***@");
    }
}

