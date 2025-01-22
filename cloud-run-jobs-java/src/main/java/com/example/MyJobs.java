package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class MyJobs implements CommandLineRunner {
  // These values are provided automatically by the Cloud Run Jobs runtime.
  private static String CLOUD_RUN_TASK_INDEX =
  System.getenv().getOrDefault("CLOUD_RUN_TASK_INDEX", "0");
  private static String CLOUD_RUN_TASK_ATTEMPT =
  System.getenv().getOrDefault("CLOUD_RUN_TASK_ATTEMPT", "0");

  // User-provided environment variables
  private static String TABLE = System.getenv().getOrDefault("TABLE", "dealers");
  private static String FILE_NAME = System.getenv().getOrDefault("FILE_NAME", "my_file.txt");

  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  private Environment environment;

  public static void main(String[] args) {
      SpringApplication.run(MyJobs.class, args);
  }

  @Override
  public void run(String... args) {
    // job start
    System.out.println(String.format("Started Task #%s", CLOUD_RUN_TASK_INDEX));
    System.out.println("#With this example, we can code from end to end log from the local.");
    
    // GCS: for cloud storage, using volume-mount, so it's just as local file.
    String filePath = "/import_log";
    String fileName = FILE_NAME;
    System.out.println("#File path:" + filePath + "/" + fileName);
    System.out.println("#So, use syntax of standard java api to read file. Don't use Cloud Storage syntax");

    // SQL: sample query sql
    String sql = "select count(1) from %s;";
    sql = String.format(sql, TABLE);
    String output;
    try {
      String result = jdbcTemplate.queryForObject(sql, String.class);
      output = "Number of records: " + result;
    } catch (DataAccessException ex) {
      output = "ERROR:" + ex.getMessage();
    }
    System.out.println("#" + output);

    // validte the exec env
    String env = environment.getProperty("spring.datasource.url");
    System.out.println("Environment: " + env);

    // job end
    System.out.println(String.format("Completed Task #%s", CLOUD_RUN_TASK_INDEX));
  }
  
}
