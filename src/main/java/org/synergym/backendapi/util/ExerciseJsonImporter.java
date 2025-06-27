// package org.synergym.backendapi.util;

// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
// import org.synergym.backendapi.dto.ExerciseDTO;
// import org.synergym.backendapi.service.ExerciseService;
// import com.fasterxml.jackson.databind.PropertyNamingStrategies;

// import java.io.File;
// import java.util.List;

// @Component
// public class ExerciseJsonImporter {

//     @Autowired
//     private ExerciseService exerciseService;

//     public void importFromJson(String jsonFilePath) throws Exception {
//         ObjectMapper mapper = new ObjectMapper();
//         mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
//         List<ExerciseDTO> exercises = mapper.readValue(
//                 new File(jsonFilePath),
//                 new TypeReference<List<ExerciseDTO>>() {}
//         );
//         for (ExerciseDTO dto : exercises) {
//             exerciseService.createExercise(dto);
//         }
//     }
// }