package com.saae.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.saae.model.Mark;
import com.saae.model.Student;
import com.saae.model.Subject;
import com.saae.model.User;
import com.saae.repository.MarkRepository;
import com.saae.repository.StudentRepository;
import com.saae.repository.SubjectRepository;
import com.saae.repository.UserRepository;

@RestController
@RequestMapping("/api/setup")
public class DatabaseSetupController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String setupPage() {
        return "setup";
    }

    @PostMapping("/hash-passwords")
    @ResponseBody
    public Map<String, Object> hashAllPasswords() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. Create or Update default users with user-specific passwords
            createOrUpdateDefaultUser("admin", "admin123", "ADMIN", "System Administrator", "admin@saae.com");
            createOrUpdateDefaultUser("faculty", "faculty123", "FACULTY", "Faculty User", "faculty@saae.com");
            createOrUpdateDefaultUser("student", "student123", "STUDENT", "Student User", "student@saae.com");

            // 2. Hash any plain-text passwords
            List<User> users = userRepository.findAll();
            int hashUpdatedCount = 0;

            for (User user : users) {
                String pwd = user.getPassword();
                // If password is plain text (not starting with $2a$, $2b$, $2y$)
                if (pwd != null && !pwd.startsWith("$2a$") && !pwd.startsWith("$2b$") && !pwd.startsWith("$2y$")) {
                    user.setPassword(passwordEncoder.encode(pwd));
                    userRepository.save(user);
                    hashUpdatedCount++;
                }
            }

            response.put("success", true);
            response.put("message",
                    "System Initialized! Admin/admin123, Faculty/faculty123, Student/student123 are ready. "
                            + hashUpdatedCount + " passwords hashed.");
            response.put("totalUsers", users.size());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error during setup: " + e.getMessage());
        }

        return response;
    }

    private void createOrUpdateDefaultUser(String username, String password, String role, String fullName,
            String email) {
        Optional<User> existing = userRepository.findByUsername(username);
        User user;
        if (existing.isPresent()) {
            user = existing.get();
        } else {
            user = new User();
            user.setUsername(username);
        }

        // Always set the requested password (as plain text first, the loop will hash
        // it)
        // This ensures the passwords match what the user wants: admin123, etc.
        user.setPassword(password);
        user.setRole(role);
        user.setFullName(fullName);
        user.setEmail(email);
        userRepository.save(user);
    }

    @PostMapping("/mock-data")
    @ResponseBody
    public Map<String, Object> generateMockData() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Ensure base users are available
            hashAllPasswords();

            // Create more Subjects
            Subject cs1 = createOrGetSubject("CS101", "Data Structures", "Computer Science", 4, "1");
            Subject cs2 = createOrGetSubject("CS102", "Algorithms", "Computer Science", 4, "2");
            Subject cs3 = createOrGetSubject("CS201", "Operating Systems", "Computer Science", 3, "3");
            Subject cs4 = createOrGetSubject("CS202", "Database Systems", "Computer Science", 3, "4");

            Subject ee1 = createOrGetSubject("EE201", "Circuit Theory", "Electrical", 3, "1");
            Subject ee2 = createOrGetSubject("EE202", "Digital Electronics", "Electrical", 3, "2");
            Subject ee3 = createOrGetSubject("EE301", "Power Systems", "Electrical", 4, "3");
            Subject ee4 = createOrGetSubject("EE302", "Control Systems", "Electrical", 4, "4");

            Subject me1 = createOrGetSubject("ME101", "Thermodynamics", "Mechanical", 3, "1");
            Subject me2 = createOrGetSubject("ME102", "Fluid Mechanics", "Mechanical", 3, "2");
            Subject me3 = createOrGetSubject("ME201", "Manufacturing", "Mechanical", 4, "3");
            Subject me4 = createOrGetSubject("ME202", "Machine Design", "Mechanical", 4, "4");

            Subject ce1 = createOrGetSubject("CE101", "Structural Analysis", "Civil", 3, "3");
            Subject ce2 = createOrGetSubject("CE102", "Geotechnical Eng", "Civil", 3, "4");

            Subject ma1 = createOrGetSubject("MA101", "Calculus", "General", 4, "1");
            Subject ma2 = createOrGetSubject("MA102", "Linear Algebra", "General", 4, "2");

            List<Subject> allSubjects = List.of(cs1, cs2, cs3, cs4, ee1, ee2, ee3, ee4, me1, me2, me3, me4, ce1, ce2,
                    ma1, ma2);

            // Create Faculty Users
            createOrUpdateDefaultUser("prof.smith", "prof123", "FACULTY", "Prof. Alan Smith", "smith@saae.com");
            createOrUpdateDefaultUser("dr.jones", "dr123", "FACULTY", "Dr. Sarah Jones", "jones@saae.com");
            createOrUpdateDefaultUser("prof.chen", "chen123", "FACULTY", "Prof. Li Chen", "chen@saae.com");
            createOrUpdateDefaultUser("dr.wilson", "wilson123", "FACULTY", "Dr. James Wilson", "wilson@saae.com");

            // Create Students
            // Computer Science
            Student st1 = createStudent("Alice Smith", "alice", "alice123", "CS202401", "Computer Science", "4");
            Student st2 = createStudent("Bob Johnson", "bob", "bob123", "CS202402", "Computer Science", "4");
            Student st3 = createStudent("Charlie Black", "charlie", "charlie123", "CS202403", "Computer Science", "4");

            // Electrical
            Student st4 = createStudent("Diana Prince", "diana", "diana123", "EE202401", "Electrical", "4");
            Student st5 = createStudent("Edward Norton", "edward", "edward123", "EE202402", "Electrical", "4");
            Student st6 = createStudent("Fiona Apple", "fiona", "fiona123", "EE202403", "Electrical", "4");

            // Mechanical
            Student st7 = createStudent("George Clooney", "george", "george123", "ME202401", "Mechanical", "4");
            Student st8 = createStudent("Hannah Abbott", "hannah", "hannah123", "ME202402", "Mechanical", "4");
            Student st9 = createStudent("Ian Wright", "ian", "ian123", "ME202403", "Mechanical", "4");

            // Civil
            Student st10 = createStudent("Jack Sparrow", "jack", "jack123", "CE202401", "Civil", "4");
            Student st11 = createStudent("Kelly Clarkson", "kelly", "kelly123", "CE202402", "Civil", "4");
            Student st12 = createStudent("Leo DiCaprio", "leo", "leo123", "CS202404", "Computer Science", "4");
            Student st13 = createStudent("Mila Kunis", "mila", "mila123", "EE202404", "Electrical", "4");
            Student st14 = createStudent("Noah Centineo", "noah", "noah123", "ME202404", "Mechanical", "4");
            Student st15 = createStudent("Olivia Rodrigo", "olivia", "olivia123", "CE202404", "Civil", "4");
            Student st16 = createStudent("Peter Parker", "peter", "peter123", "CS202405", "Computer Science", "4");
            Student st17 = createStudent("Quinn Fabray", "quinn", "quinn123", "EE202405", "Electrical", "4");
            Student st18 = createStudent("Riley Reid", "riley", "riley123", "ME202405", "Mechanical", "4");
            Student st19 = createStudent("Sam Smith", "sam", "sam123", "CE202405", "Civil", "4");
            Student st20 = createStudent("Taylor Swift", "taylor", "taylor123", "CS202406", "Computer Science", "4");

            List<Student> students = List.of(st1, st2, st3, st4, st5, st6, st7, st8, st9, st10, st11, st12, st13, st14,
                    st15, st16, st17, st18, st19, st20);

            // Assign marks to students based on their department and semester history
            for (Student student : students) {
                for (Subject subject : allSubjects) {
                    // Only assign subjects related to their department or general subjects
                    boolean isDeptSubject = subject.getDepartment().equals(student.getDepartment())
                            || subject.getDepartment().equals("General");

                    if (isDeptSubject) {
                        // Check if mark already exists
                        if (markRepository.findByStudent_Id(student.getId()).stream()
                                .noneMatch(m -> m.getSubject().getId().equals(subject.getId()))) {

                            Mark mark = new Mark();
                            mark.setStudent(student);
                            mark.setSubject(subject);
                            mark.setTotalMarks(100);
                            mark.setSemester(subject.getSemester());

                            // Variation in scores for better analytics
                            double baseScore = 65;
                            // Alice is a top performer
                            if (student.getUser().getUsername().equals("alice"))
                                baseScore = 85;
                            // Bob is struggling
                            if (student.getUser().getUsername().equals("bob"))
                                baseScore = 35;
                            // Edward is average but weak in Circuits (EE201)
                            if (student.getUser().getUsername().equals("edward")
                                    && subject.getSubjectCode().equals("EE201"))
                                baseScore = 30;
                            // Diana excels in Electrical
                            if (student.getUser().getUsername().equals("diana")
                                    && student.getDepartment().equals("Electrical"))
                                baseScore = 90;

                            double score = baseScore + (Math.random() * 15);
                            if (score > 100)
                                score = 100;

                            mark.setMarksObtained(Math.round(score));
                            markRepository.save(mark);
                        }
                    }
                }
            }

            // Also ensure the primary 'student' account gets a profile and marks
            Optional<User> studentUser = userRepository.findByUsername("student");
            if (studentUser.isPresent()) {
                Student primarySt;
                Optional<Student> existingPrimary = studentRepository.findByRollNumber("SAAE001");
                if (existingPrimary.isEmpty()) {
                    primarySt = new Student();
                    primarySt.setUser(studentUser.get());
                    primarySt.setName(studentUser.get().getFullName());
                    primarySt.setRollNumber("SAAE001");
                    primarySt.setDepartment("Computer Science");
                    primarySt.setCurrentSemester("4");
                    primarySt = studentRepository.save(primarySt);
                } else {
                    primarySt = existingPrimary.get();
                }

                // Give primary student some CS marks if not already present
                for (Subject subject : List.of(cs1, cs2, cs3, cs4, ma1, ma2)) {
                    if (markRepository.findByStudent_Id(primarySt.getId()).stream()
                            .noneMatch(m -> m.getSubject().getId().equals(subject.getId()))) {
                        Mark mark = new Mark();
                        mark.setStudent(primarySt);
                        mark.setSubject(subject);
                        mark.setTotalMarks(100);
                        mark.setMarksObtained(Math.round(75 + (Math.random() * 20)));
                        mark.setSemester(subject.getSemester());
                        markRepository.save(mark);
                    }
                }
            }

            response.put("success", true);
            response.put("message", "Generated mock subjects, students, and marks.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error generating data: " + e.getMessage());
        }
        return response;
    }

    private Subject createOrGetSubject(String code, String name, String dept, int credits, String sem) {
        Optional<Subject> optS = subjectRepository.findBySubjectCode(code);
        if (optS.isEmpty()) {
            Subject s = new Subject();
            s.setSubjectCode(code);
            s.setSubjectName(name);
            s.setDepartment(dept);
            s.setCreditHours(credits);
            s.setSemester(sem);
            return subjectRepository.save(s);
        }
        return optS.get();
    }

    private Student createStudent(String name, String username, String pass, String roll, String dept, String sem) {
        Optional<Student> optS = studentRepository.findByRollNumber(roll);
        if (optS.isPresent())
            return optS.get();

        createOrUpdateDefaultUser(username, pass, "STUDENT", name, username + "@example.com");
        User user = userRepository.findByUsername(username).get();

        Student newSt = new Student();
        newSt.setUser(user);
        newSt.setName(name);
        newSt.setRollNumber(roll);
        newSt.setDepartment(dept);
        newSt.setCurrentSemester(sem);
        return studentRepository.save(newSt);
    }
}
