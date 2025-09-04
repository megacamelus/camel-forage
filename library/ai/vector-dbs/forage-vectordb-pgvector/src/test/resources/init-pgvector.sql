-- Initialize pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Create a sample schema for testing
CREATE SCHEMA IF NOT EXISTS test_schema;

-- Grant permissions
GRANT ALL PRIVILEGES ON SCHEMA test_schema TO test_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA test_schema TO test_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA test_schema TO test_user;

-- Set search path
ALTER USER test_user SET search_path = test_schema, public;