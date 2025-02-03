package fi.vm.sade.koodisto.export;

import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.TaskWithoutDataDescriptor;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.FixedDelay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ExportTaskConfiguration {
    private final ExportService exportService;

    @Bean
    @ConditionalOnProperty(name = "koodisto.tasks.export.enabled", matchIfMissing = false)
    Task<Void> createSchemaTask() {
        log.info("Creating koodisto export task");
        return Tasks.recurring(new TaskWithoutDataDescriptor("Data export: create schema"), FixedDelay.ofHours(1))
                .execute((taskInstance, executionContext) -> {
                    try {
                        log.info("Running koodisto export task");
                        exportService.createSchema();
                        exportService.generateExportFiles();
                        exportService.copyExportFilesToLampi();
                        log.info("Koodisto export task completed");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
