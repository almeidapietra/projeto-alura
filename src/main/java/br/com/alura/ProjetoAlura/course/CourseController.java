package br.com.alura.ProjetoAlura.course;

import br.com.alura.ProjetoAlura.user.Role;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;
import br.com.alura.ProjetoAlura.util.ErrorItemDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    @PostMapping("/course/{code}/inactive")
    public ResponseEntity<?> inactivateCourse(@PathVariable("code") String courseCode, @Valid @RequestBody InactivateCourseDTO request) {

        String inactivationReason = request.getInactivationReason();

        if (inactivationReason == null || inactivationReason.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("inactivationReason", "É necessário incluir o motivo da inativação"));
        }

        Optional<Course> optionalCourse = courseRepository.findByCode(courseCode);

        if (optionalCourse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorItemDTO("courseCode", "Curso não encontrado com o código: " + courseCode));
        }

        Course course = optionalCourse.get();

        if (course.getStatus() == Status.INACTIVE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("status", "Curso já está inativo"));
        }

        course.setInactivationReason(inactivationReason);
        course.inactivated();
        courseRepository.save(course);

        return ResponseEntity.ok(course);
    }

}