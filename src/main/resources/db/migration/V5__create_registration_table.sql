CREATE TABLE Registration (
                              id bigint(20) NOT NULL AUTO_INCREMENT,
                              user_id bigint(20) NOT NULL,
                              course_id bigint(20) NOT NULL,
                              registration_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              PRIMARY KEY (id),
                              CONSTRAINT FK_Registration_User FOREIGN KEY (user_id) REFERENCES user(id),
                              CONSTRAINT FK_Registration_Course FOREIGN KEY (course_id) REFERENCES course(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;