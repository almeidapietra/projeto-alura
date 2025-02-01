package br.com.alura.ProjetoAlura.registration;

import br.com.alura.ProjetoAlura.course.Course;
import br.com.alura.ProjetoAlura.course.CourseRepository;
import br.com.alura.ProjetoAlura.course.Status;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class RegistrationController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final RegistrationRepository registrationRepository;

    public RegistrationController(UserRepository userRepository, CourseRepository courseRepository, RegistrationRepository registrationRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.registrationRepository = registrationRepository;
    }

    @PostMapping("/registration/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewRegistrationDTO newRegistration) {

        Optional<User> optionalStudent = userRepository.findByEmail(newRegistration.getStudentEmail());
        if (optionalStudent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aluno não encontrado com o email: " + newRegistration.getStudentEmail());
        }
        User student = optionalStudent.get();

        Optional<Course> optionalCourse = courseRepository.findByCode(newRegistration.getCourseCode());
        if (optionalCourse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Curso não encontrado com o código: " + newRegistration.getCourseCode());
        }
        Course course = optionalCourse.get();

        if (course.getStatus() != Status.ACTIVE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Não é possível se matricular em um curso inativo.");
        }

        if (registrationRepository.existsByUserAndCourse(student, course)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("O aluno já está matriculado neste curso.");
        }

        Registration registration = new Registration(student, course);
        registrationRepository.save(registration);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/registration/report")
    public ResponseEntity<List<RegistrationReportItem>> report() {
        List<RegistrationReportItem> items = new ArrayList<>();

        // TODO: Implementar a Questão 4 - Relatório de Cursos Mais Acessados aqui...

        // Dados fictícios abaixo que devem ser substituídos
        items.add(new RegistrationReportItem(
                "Java para Iniciantes",
                "java",
                "Charles",
                "charles@alura.com.br",
                10L
        ));

        items.add(new RegistrationReportItem(
                "Spring para Iniciantes",
                "spring",
                "Charles",
                "charles@alura.com.br",
                9L
        ));

        items.add(new RegistrationReportItem(
                "Maven para Avançados",
                "maven",
                "Charles",
                "charles@alura.com.br",
                9L
        ));

        return ResponseEntity.ok(items);
    }

}
