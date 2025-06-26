package org.synergym.backendapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.synergym.backendapi.util.ExerciseJsonImporter;


@SpringBootTest
public class ExerciseJsonImportTest {
    
    @Autowired
    private ExerciseJsonImporter importer;

    @Test   
    void importExercises() throws Exception {
        importer.importFromJson("src/main/resources/naver_exercise_cleaned.json");
    }

}
