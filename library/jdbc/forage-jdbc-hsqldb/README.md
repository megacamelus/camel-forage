# HSQLDB JDBC Configuration

## Database connection settings
```
jdbc.url=jdbc:hsqldb:mem:testdb
jdbc.username=sa
jdbc.password=
```

## Connection pool settings
```
jdbc.pool.initial.size=5
jdbc.pool.min.size=2
jdbc.pool.max.size=20
jdbc.pool.acquisition.timeout.seconds=5
jdbc.pool.validation.timeout.seconds=3
jdbc.pool.leak.timeout.minutes=10
jdbc.pool.idle.validation.timeout.minutes=3
```

## Transaction settings
```
jdbc.transaction.timeout.seconds=30
```