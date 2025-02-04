import * as constructs from "constructs";
import * as cdk from "aws-cdk-lib";
import * as logs from "aws-cdk-lib/aws-logs";
import * as sns from "aws-cdk-lib/aws-sns";
import * as cloudwatch from "aws-cdk-lib/aws-cloudwatch";
import * as cloudwatch_actions from "aws-cdk-lib/aws-cloudwatch-actions";

export type ExpectedLogLineAlarmProps = {
  logGroup: logs.LogGroup;
  alarmTopic: sns.ITopic;
  metricNamespace: string;
  name: string;
  expectedLogLine: string;
  period?: cdk.Duration;
  evaluationPeriods?: number;
};

export class ExpectedLogLineAlarm extends constructs.Construct {
  constructor(
    scope: constructs.Construct,
    id: string,
    {
      logGroup,
      alarmTopic,
      metricNamespace,
      name,
      expectedLogLine,
      period,
      evaluationPeriods,
    }: ExpectedLogLineAlarmProps
  ) {
    super(scope, id);

    const metricFilter = new logs.MetricFilter(this, "MetricFilter", {
      logGroup,
      filterPattern: logs.FilterPattern.allTerms(expectedLogLine),
      metricName: `${name}Successes`,
      metricNamespace,
      metricValue: "1",
    });

    const alarm = new cloudwatch.Alarm(this, "Alarm", {
      alarmName: metricNamespace + name,
      metric: metricFilter.metric({
        statistic: "Sum",
        period: period ?? cdk.Duration.hours(1),
      }),
      comparisonOperator:
        cloudwatch.ComparisonOperator.LESS_THAN_OR_EQUAL_TO_THRESHOLD,
      threshold: 0,
      evaluationPeriods: evaluationPeriods ?? 8,
      treatMissingData: cloudwatch.TreatMissingData.BREACHING,
    });

    alarm.addOkAction(new cloudwatch_actions.SnsAction(alarmTopic));
    alarm.addAlarmAction(new cloudwatch_actions.SnsAction(alarmTopic));
  }
}
