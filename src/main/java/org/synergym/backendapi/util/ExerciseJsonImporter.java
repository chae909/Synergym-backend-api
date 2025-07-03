// package org.synergym.backendapi.util;
// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.PropertyNamingStrategies;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.stereotype.Component;
// import org.synergym.backendapi.dto.ExerciseDTO;
// import org.synergym.backendapi.service.ExerciseService;

// import java.io.File;
// import java.util.List;

// @Component
// public class ExerciseJsonImporter implements CommandLineRunner {

//     @Autowired
//     private ExerciseService exerciseService;

//     public void importFromJson(String jsonFilePath) throws Exception {
//         ObjectMapper mapper = new ObjectMapper();
//         mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
//         File file = new ClassPathResource(jsonFilePath).getFile();
//         List<ExerciseDTO> exercises = mapper.readValue(
//                 file,
//                 new TypeReference<List<ExerciseDTO>>() {}
//         );
//         for (ExerciseDTO dto : exercises) {
//             exerciseService.createExercise(dto);
//         }
//     }

//     @Override
//     public void run(String... args) throws Exception {
//         importFromJson("seed/naver_exercise_cleaned_cloudinary.json");
//     }
// }