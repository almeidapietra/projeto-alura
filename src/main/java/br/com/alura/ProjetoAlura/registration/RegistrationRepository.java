package br.com.alura.ProjetoAlura.registration;

import br.com.alura.ProjetoAlura.course.Course;
import br.com.alura.ProjetoAlura.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    boolean existsByUserAndCourse(User user, Course course);

    @Query(nativeQuery = true, value = """
            SELECT 
                c.name as courseName,
                c.code as courseCode,
                u.name as instructorName,
                c.instructor as instructorEmail,
                COUNT(r.id) as totalRegistrations
            FROM course c
            LEFT JOIN registration r ON r.course_id = c.id
            LEFT JOIN user u ON BINARY u.email = BINARY c.instructor
            GROUP BY c.id, c.name, c.code, u.name, c.instructor
            ORDER BY COUNT(r.id) DESC
            """)
    List<Object[]> generateRegistrationReport();
}
