package br.com.alura.ProjetoAlura.registration;

import br.com.alura.ProjetoAlura.course.Course;
import br.com.alura.ProjetoAlura.course.CourseRepository;
import br.com.alura.ProjetoAlura.user.Role;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private RegistrationRepository registrationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createRegistration__should_return_created_when_registration_request_is_valid() throws Exception {
        NewRegistrationDTO newRegistrationDTO = new NewRegistrationDTO();
        newRegistrationDTO.setStudentEmail("student@student.com");
        newRegistrationDTO.setCourseCode("c-jb");

        User student = new User("Student", "student@student.com", Role.STUDENT, "password123");
        Course course = new Course("Curso Java Básico", "c-jb", "ot@instructor.com", "Curso básico de Java");

        when(userRepository.findByEmail("student@student.com")).thenReturn(Optional.of(student));
        when(courseRepository.findByCode("c-jb")).thenReturn(Optional.of(course));
        when(registrationRepository.existsByUserAndCourse(student, course)).thenReturn(false);

        mockMvc.perform(post("/registration/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRegistrationDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void createRegistration__should_return_not_found_when_student_not_found() throws Exception {
        NewRegistrationDTO newRegistrationDTO = new NewRegistrationDTO();
        newRegistrationDTO.setStudentEmail("nonexistent@student.com");
        newRegistrationDTO.setCourseCode("c-jb");

        when(userRepository.findByEmail("nonexistent@student.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/registration/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRegistrationDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Aluno não encontrado com o email: nonexistent@student.com"));
    }

    @Test
    void createRegistration__should_return_not_found_when_course_not_found() throws Exception {
        NewRegistrationDTO newRegistrationDTO = new NewRegistrationDTO();
        newRegistrationDTO.setStudentEmail("student@student.com");
        newRegistrationDTO.setCourseCode("nonexistent-course");

        User student = new User("Student", "student@student.com", Role.STUDENT, "password123");
        when(userRepository.findByEmail("student@student.com")).thenReturn(Optional.of(student));
        when(courseRepository.findByCode("nonexistent-course")).thenReturn(Optional.empty());

        mockMvc.perform(post("/registration/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRegistrationDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Curso não encontrado com o código: nonexistent-course"));
    }

    @Test
    void createRegistration__should_return_bad_request_when_course_is_inactive() throws Exception {
        NewRegistrationDTO newRegistrationDTO = new NewRegistrationDTO();
        newRegistrationDTO.setStudentEmail("student@student.com");
        newRegistrationDTO.setCourseCode("c-jb");

        User student = new User("Student", "student@student.com", Role.STUDENT, "password123");
        Course course = new Course("Curso Java Básico", "c-jb", "ot@instructor.com", "Curso básico de Java");
        course.inactivated();

        when(userRepository.findByEmail("student@student.com")).thenReturn(Optional.of(student));
        when(courseRepository.findByCode("c-jb")).thenReturn(Optional.of(course));

        mockMvc.perform(post("/registration/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRegistrationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Não é possível se matricular em um curso inativo."));
    }

    @Test
    void createRegistration__should_return_bad_request_when_already_registered() throws Exception {
        NewRegistrationDTO newRegistrationDTO = new NewRegistrationDTO();
        newRegistrationDTO.setStudentEmail("student@student.com");
        newRegistrationDTO.setCourseCode("c-jb");

        User student = new User("Student", "student@student.com", Role.STUDENT, "password123");
        Course course = new Course("Curso Java Básico", "c-jb", "ot@instructor.com", "Curso básico de Java");

        when(userRepository.findByEmail("student@student.com")).thenReturn(Optional.of(student));
        when(courseRepository.findByCode("c-jb")).thenReturn(Optional.of(course));
        when(registrationRepository.existsByUserAndCourse(student, course)).thenReturn(true);

        mockMvc.perform(post("/registration/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRegistrationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("O aluno já está matriculado neste curso."));
    }
}