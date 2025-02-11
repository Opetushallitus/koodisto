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

import java.time.LocalTime;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class DatantuontiExportTaskConfiguration {
    private final DatantuontiExportService datantuontiExportService;

    @Bean
    @ConditionalOnProperty(name = "koodisto.tasks.datantuonti.export.enabled", matchIfMissing = false)
    Task<Void> createSchemaTask() {
        log.info("Creating koodisto datantuonti export task");
        return Tasks.recurring(new TaskWithoutDataDescriptor("DatantuontiExport"), new Daily(LocalTime.of(0, 15, 0)))
                .execute((taskInstance, executionContext) -> {
                    log.info("Running koodisto datantuonti export task");
                    String secondsFromEpoch = datantuontiExportService.createSchemaAndReturnTransactionTimestampFromEpoch();
                    datantuontiExportService.generateExportFiles(secondsFromEpoch);
                    log.info("Koodisto datantuonti export task completed");
                });
    }
}
