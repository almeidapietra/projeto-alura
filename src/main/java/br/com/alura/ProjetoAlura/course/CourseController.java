package br.com.alura.ProjetoAlura.course;

import br.com.alura.ProjetoAlura.user.Role;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;
import br.com.alura.ProjetoAlura.util.ErrorItemDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.Optional;

@RestController
public class CourseController {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseController(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/course/new")
    public ResponseEntity<?> createCourse(@Valid @RequestBody NewCourseDTO newCourse) {
        Optional<User> optionalInstructor = userRepository.findByEmail(newCourse.getInstructorEmail());

        if (optionalInstructor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("instructorEmail",
                            "Instrutor não encontrado com o email: " + newCourse.getInstructorEmail()));
        }

        User instructor = optionalInstructor.get();

        if (!Role.INSTRUCTOR.equals(instructor.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorItemDTO("role",
                            "O email fornecido não pertence a um instrutor."));
        }

        if (courseRepository.existsByCode(newCourse.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("code", "Código já cadastrado no sistema"));
        }

        Course course = new Course(
                newCourse.getName(),
                newCourse.getCode(),
                newCourse.getInstructorEmail(),
                newCourse.getDescription()
        );

        courseRepository.save(course);

        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }
}