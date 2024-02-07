package com.study.gateway.database;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;

@Configuration
@EnableR2dbcRepositories
public class R2DatabaseConfig {

//    @Bean
//    @Override
//    public ConnectionFactory connectionFactory() {
//        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
//                .option(ConnectionFactoryOptions.DATABASE, "test")
//                .option(ConnectionFactoryOptions.HOST, "localhost")
//                .option(ConnectionFactoryOptions.PORT, 5432)
//                .option(ConnectionFactoryOptions.USER, "postgres")
//                .option(ConnectionFactoryOptions.PASSWORD, "11223344")
//                .option(ConnectionFactoryOptions.DRIVER, "postgresql")
//                .option(ConnectionFactoryOptions.PROTOCOL,"postgresql")
//                .build();
//        return ConnectionFactories.get(options);
//    }
}
