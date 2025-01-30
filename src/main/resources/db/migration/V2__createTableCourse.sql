CREATE TABLE Course (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    status enum('ACTIVE', 'INACTIVE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
    inactivatedAt datetime DEFAULT NULL,
    name varchar(255) NOT NULL,
    code varchar(255) NOT NULL,
    instructor varchar(255) NOT NULL,
    description text,
    PRIMARY KEY (id),
    CONSTRAINT UC_Code UNIQUE (code)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;