package fi.vm.sade.koodisto.datantuonti;

import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.TaskWithoutDataDescriptor;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.Daily;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalTime;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class DatantuontiImportTaskConfiguration {
    private final DatantuontiImportService datantuontiImportService;

    @Bean
    @ConditionalOnProperty(name = "koodisto.tasks.datantuonti.import.enabled", matchIfMissing = false)
    Task<Void> createSchemaTask() {
        log.info("Creating koodisto datantuonti import task");
        return Tasks.recurring(new TaskWithoutDataDescriptor("DatantuontiImport"), new Daily(LocalTime.of(2, 15, 0)))
                .execute((taskInstance, executionContext) -> {
                    try {
                        log.info("Running koodisto datantuonti import task");
                        datantuontiImportService.importTempTablesFromS3();
                        log.info("Koodisto datantuonti import task completed");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
