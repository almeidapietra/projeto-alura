package br.com.alura.ProjetoAlura.course;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import java.util.Optional;

@WebMvcTest(CourseController.class)
class CourseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CourseRepository courseRepository;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCourse__should_return_created_when_course_request_is_valid() throws Exception {
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setName("Curso Java Básico");
        newCourseDTO.setCode("c-jb");
        newCourseDTO.setDescription("Curso básico de Java");
        newCourseDTO.setInstructorEmail("ot@instructor.com");
        User instructor = new User("Otto", "ot@instructor.com", Role.INSTRUCTOR, "password123");
        when(userRepository.findByEmail("ot@instructor.com")).thenReturn(Optional.of(instructor));
        when(courseRepository.existsByCode("c-jb")).thenReturn(false);
        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void createCourse__should_return_bad_request_when_code_is_invalid() throws Exception {
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setName("Curso Java Básico");
        newCourseDTO.setCode("cjb123");
        newCourseDTO.setDescription("Curso básico de Java");
        newCourseDTO.setInstructorEmail("ot@instructor.com");
        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("code"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void createCourse__should_return_bad_request_when_instructor_not_found() throws Exception {
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setName("Curso Java Básico");
        newCourseDTO.setCode("c-jb");
        newCourseDTO.setDescription("Curso básico de Java");
        newCourseDTO.setInstructorEmail("nonexistent@instructor.com");
        when(userRepository.findByEmail("nonexistent@instructor.com")).thenReturn(Optional.empty());
        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("instructorEmail"))
                .andExpect(jsonPath("$.message").value("Instrutor não encontrado com o email: nonexistent@instructor.com"));
    }

    @Test
    void createCourse__should_return_forbidden_when_user_is_not_instructor() throws Exception {
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setName("Curso Java Básico");
        newCourseDTO.setCode("c-jb");
        newCourseDTO.setDescription("Curso básico de Java");
        newCourseDTO.setInstructorEmail("student@student.com");
        User student = new User("Student", "student@student.com", Role.STUDENT, "password123");
        when(userRepository.findByEmail("student@student.com")).thenReturn(Optional.of(student));
        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.field").value("role"))
                .andExpect(jsonPath("$.message").value("O email fornecido não pertence a um instrutor."));
    }

    @Test
    void createCourse__should_return_bad_request_when_code_already_exists() throws Exception {
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setName("Curso Java Básico");
        newCourseDTO.setCode("c-jb");
        newCourseDTO.setDescription("Curso básico de Java");
        newCourseDTO.setInstructorEmail("ot@instructor.com");
        User instructor = new User("Otto", "ot@instructor.com", Role.INSTRUCTOR, "password123");
        when(userRepository.findByEmail("ot@instructor.com")).thenReturn(Optional.of(instructor));
        when(courseRepository.existsByCode("c-jb")).thenReturn(true);
        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("code"))
                .andExpect(jsonPath("$.message").value("Código já cadastrado no sistema"));
    }

    // Testes para inativação de curso
    @Test
    void inactivateCourse__should_return_no_content_when_course_exists() throws Exception {
        Course course = new Course("Curso Java Básico", "c-jb", "ot@instructor.com", "Curso básico de Java");
        when(courseRepository.findByCode("c-jb")).thenReturn(Optional.of(course));

        InactivateCourseDTO dto = new InactivateCourseDTO();
        dto.setInactivationReason("Curso desatualizado");

        mockMvc.perform(post("/course/c-jb/inactive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void inactivateCourse__should_return_not_found_when_course_does_not_exist() throws Exception {
        when(courseRepository.findByCode("nonexistent-course")).thenReturn(Optional.empty());

        InactivateCourseDTO dto = new InactivateCourseDTO();
        dto.setInactivationReason("Curso desatualizado");

        mockMvc.perform(post("/course/nonexistent-course/inactive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.field").value("courseCode"))
                .andExpect(jsonPath("$.message").value("Curso não encontrado com o código: nonexistent-course"));
    }

    @Test
    void inactivateCourse__should_return_bad_request_when_course_already_inactive() throws Exception {
        Course course = new Course("Curso Java Básico", "c-jb", "ot@instructor.com", "Curso básico de Java");
        course.inactivated();

        when(courseRepository.findByCode("c-jb")).thenReturn(Optional.of(course));

        InactivateCourseDTO dto = new InactivateCourseDTO();
        dto.setInactivationReason("Curso desatualizado");

        mockMvc.perform(post("/course/c-jb/inactive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("status"))
                .andExpect(jsonPath("$.message").value("Curso já está inativo"));
    }

    @Test
    void inactivateCourse__should_return_bad_request_when_reason_is_empty() throws Exception {
        Course course = new Course("Curso Java Básico", "c-jb", "ot@instructor.com", "Curso básico de Java");
        when(courseRepository.findByCode("c-jb")).thenReturn(Optional.of(course));

        InactivateCourseDTO dto = new InactivateCourseDTO();
        dto.setInactivationReason("");

        mockMvc.perform(post("/course/c-jb/inactive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("inactivationReason"))
                .andExpect(jsonPath("$[0].message").value("must not be blank"));
    }

    @Test
    void inactivateCourse__should_set_inactive_status_and_date() throws Exception {
        Course course = new Course("Curso Java Básico", "c-jb", "ot@instructor.com", "Curso básico de Java");
        when(courseRepository.findByCode("c-jb")).thenReturn(Optional.of(course));

        InactivateCourseDTO dto = new InactivateCourseDTO();
        dto.setInactivationReason("Curso desatualizado");

        mockMvc.perform(post("/course/c-jb/inactive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        assert(course.getStatus().equals(Status.INACTIVE));
        assert(course.getInactivatedAt() != null);
        assert(course.getInactivationReason().equals("Curso desatualizado"));
    }
}