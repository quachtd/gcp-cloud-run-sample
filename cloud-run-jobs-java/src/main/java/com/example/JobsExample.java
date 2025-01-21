
package com.example;

abstract class JobsExample {
  // These values are provided automatically by the Cloud Run Jobs runtime.
  private static String CLOUD_RUN_TASK_INDEX =
      System.getenv().getOrDefault("CLOUD_RUN_TASK_INDEX", "0");
  private static String CLOUD_RUN_TASK_ATTEMPT =
      System.getenv().getOrDefault("CLOUD_RUN_TASK_ATTEMPT", "0");

  // User-provided environment variables
  private static int SLEEP_MS = Integer.parseInt(System.getenv().getOrDefault("SLEEP_MS", "0"));
  private static float FAIL_RATE =
      Float.parseFloat(System.getenv().getOrDefault("FAIL_RATE", "0.0"));

  // Start script
  public static void main(String[] args) {
    System.out.println(
        String.format(
            "Starting Task #%s, Attempt #%s...", CLOUD_RUN_TASK_INDEX, CLOUD_RUN_TASK_ATTEMPT));
    try {
      runTask(SLEEP_MS, FAIL_RATE);
    } catch (RuntimeException | InterruptedException e) {
      System.err.println(
          String.format(
              "Task #%s, Attempt #%s failed.", CLOUD_RUN_TASK_INDEX, CLOUD_RUN_TASK_ATTEMPT));
      // Catch error and denote process-level failure to retry Task
      System.exit(1);
    }
  }

  static void runTask(int sleepTime, float failureRate) throws InterruptedException {
    // Simulate work
    if (sleepTime > 0) {
      Thread.sleep(sleepTime);
    }

    // Simulate errors
    if (failureRate < 0 || failureRate > 1) {
      System.err.println(
          String.format(
              "Invalid FAIL_RATE value: %s. Must be a float between 0 and 1 inclusive.",
              failureRate));
      return;
    }
    if (Math.random() < failureRate) {
      throw new RuntimeException("Task Failed.");
    }
    System.out.println(String.format("Completed Task #%s", CLOUD_RUN_TASK_INDEX));
  }
}
