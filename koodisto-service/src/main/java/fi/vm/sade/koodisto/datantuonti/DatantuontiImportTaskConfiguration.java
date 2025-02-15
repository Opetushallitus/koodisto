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
import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class DatantuontiImportTaskConfiguration {
    private final DatantuontiImportService datantuontiImportService;
    private static final LocalDate ANCHOR_DATE = LocalDate.of(2025, 2, 16);

    @Bean
    @ConditionalOnProperty(name = "koodisto.tasks.datantuonti.import.enabled", matchIfMissing = false)
    Task<Void> datantuontiImportTask() {
        log.info("Creating koodisto datantuonti import task");
        return Tasks.recurring(new TaskWithoutDataDescriptor("DatantuontiImport"), new Daily(LocalTime.of(2, 15, 0)))
                .execute((taskInstance, executionContext) -> {
                    try {
                        log.info("Running koodisto datantuonti import task");
                        datantuontiImportService.importTempTablesFromS3();
                        if (isBiWeeklyExecutionDay()) {
                            log.info("It is the biweekly datantuonti day, replacing all koodisto data with imported data");
                            datantuontiImportService.replaceData();
                        } else {
                            log.info("It is not the biweekly datantuonti day, not replacing koodisto data");
                        }
                        log.info("Koodisto datantuonti import task completed");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private boolean isBiWeeklyExecutionDay() {
        LocalDate today = LocalDate.now();

        long daysSinceAnchor = ANCHOR_DATE.until(today).getDays();
        return daysSinceAnchor % 14 == 0;
    }
}
