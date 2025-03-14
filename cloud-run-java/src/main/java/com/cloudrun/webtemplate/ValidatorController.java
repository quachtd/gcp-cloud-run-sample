package com.cloudrun.webtemplate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.google.cloud.run.v2.EnvVar;
import com.google.cloud.run.v2.JobName;
import com.google.cloud.run.v2.JobsClient;
import com.google.cloud.run.v2.RunJobRequest;
import com.google.cloud.run.v2.RunJobRequest.Overrides;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;


@Controller
public class ValidatorController {
  private static final Logger logger = LoggerFactory.getLogger(WebTemplateController.class);

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private Environment environment;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @GetMapping("/validator")
	public String greeting(
    @RequestParam(name="opt", required=false, defaultValue="nothing") String opt, 
    @RequestParam(name="param", required=false, defaultValue="nothing") String param, 
    @RequestParam(name="env", required=false, defaultValue="local") String env, 
    @RequestParam(name="bucket", required=false, defaultValue="cellmart-importlog") String bucket, 
    Model model) {
    
    String output = "none";

    if (opt.equals("job")) {
      logger.info("execute job");

      String jobName;
      if (param.equals("nothing")) {
        jobName = "job-quickstart";
        param = jobName;
      } else
        jobName = param;

      try (JobsClient jobsClient2 = JobsClient.create()) {
        List<EnvVar> envVars = Arrays.asList(
          EnvVar.newBuilder().setName("TABLE").setValue("service_rule").build()
        );
        //String[] overrideArgs = {"arg1=value1", "arg2=value2"};        
        Overrides.Builder overridesBuilder = Overrides.newBuilder();
        overridesBuilder
          .addContainerOverridesBuilder()
          .addAllEnv(envVars);
          //.addAllArgs(Arrays.asList(overrideArgs));

        RunJobRequest request =
            RunJobRequest.newBuilder()
                .setName(JobName.of("cellmartsanbox", "us-west1", jobName).toString())
                //.setValidateOnly(true)
                //.setEtag("etag3123477")
                .setOverrides(
                  overridesBuilder.build()
                ).build();
        //Execution response = jobsClient2.runJobAsync(request).get();
        jobsClient2.runJobAsync(request);

      } catch (Exception ex) {
        output = "ERROR:" + ex.getMessage();
        logger.error(output, ex);
      }

      /*
      try (JobsClient jobsClient = JobsClient.create()) {        
        JobName job = JobName.of("cellmartsanbox", "us-west1", jobName);
        
        RunJobRequest.Builder runJobRequestBuilder = RunJobRequest.newBuilder();
        String[] overrideArgs = {"arg1=value1", "arg2=value2"};
        Overrides.Builder overridesBuilder = Overrides.newBuilder();
        overridesBuilder.addContainerOverridesBuilder().addAllArgs(Arrays.asList(overrideArgs));
        //overridesBuilder.setTimeout(Duration.newBuilder().setSeconds(600).build()); // Timeout of 10 minutes
  
        runJobRequestBuilder.setOverrides(overridesBuilder.build());
        runJobRequestBuilder.setName(job.toString());
  
        RunJobRequest runJobRequest = runJobRequestBuilder.build();
        
        
        jobsClient.runJobAsync(runJobRequest);
        output = "Job exec asyn!";
      } catch (Exception ex) {
        output = "ERROR:" + ex.getMessage();
      }
      */
    }

    if (opt.equals("db2rest")) {
      logger.info("call rest endpoint");
      String http_endpoint = environment.getProperty("db2rest_endpoint");
      String url = http_endpoint + "/v1/rdbms/cellmart/";

      if (param.equals("nothing")) {
        url = url + "import_log?limit=1";
      } else {
        url = url + param;
      }
      param = url;

      try {
        output = restTemplate.getForObject(url, String.class);
      } catch (Exception ex) {
        output = "ERROR:" + ex.getMessage();
      }
    }

    if (!env.equals("local") && opt.equals("storage.write")) {
      logger.info("call storage.write");
      try {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        String bucketName = bucket;
        String objectName = "file1.txt";
        String content = "storage object content";

        param = "path:" + bucketName + "/" + objectName;

        BlobId blobId = BlobId.of(bucketName, "file1.txt");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, content.getBytes());
        output = "write success!";
      } catch (Exception ex) {
        output = "ERROR:" + ex.getMessage();
      }
    }

    if (!env.equals("local") && opt.equals("storage.read")) {
      logger.info("call storage.read");
      try {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        String bucketName = bucket;
        String objectName = "file1.txt";

        param = "path:" + bucketName + "/" + objectName;

        Blob blob = storage.get(BlobId.of(bucketName, objectName));
        if (blob != null) {
          String content = new String(blob.getContent());
          output = content;
        }
      } catch (Exception ex) {
        output = "ERROR:" + ex.getMessage();
      }
    }

    if (env.equals("local") && opt.equals("storage.write")) {
      logger.info("call local storage.write");

      if (param.equals("nothing"))
        param = "/import_log/file2.txt";

      String filePath = param;

      try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        writer.write("Hello, World!");
        writer.newLine(); // Add a new line
        writer.write("This is a test file.");
        output = "write success!";
      } catch (IOException e) {
        output = "ERROR:" + e.getMessage();
      }
    }

    if (env.equals("local") && opt.equals("storage.read")) {
      logger.info("call local storage.read");

      if (param.equals("nothing"))
        param = "/import_log/file2.txt";

      String filePath = param;

      try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        StringBuilder content = new StringBuilder();
        while ((line = reader.readLine()) != null) {
          content.append(line);
        }
        output = content.toString();
      } catch (IOException e) {
        output = "ERROR:" + e.getMessage();
      }
    }

    if (opt.equals("jdbc")) {    
      logger.info("call jdbc");
      
      env = environment.getProperty("spring.datasource.url");

      String table = "import_log";
      if (!param.equals("nothing")) {
        table = param;
      } else
        param = table;

      // Insert a record
      //jdbcTemplate.update("INSERT INTO users (name) VALUES (?)", "John Doe");

      // Query the table
      String sql = "select count(1) from %s;";
      sql = String.format(sql, table);
      
      try {
        String result = jdbcTemplate.queryForObject(sql, String.class);
        output = "Number of records: " + result;
      } catch (DataAccessException ex) {
        output = "ERROR:" + ex.getMessage();
      }      
    }

    if (opt.equals("rules")) {
      logger.info("call rest EngineRule");

      if (param.equals("nothing"))
        param = "1";

      String http_endpoint = environment.getProperty("rules_endpoint");
      String url = http_endpoint + "/rules/" + param;

      param = "url:" + url;

      try {
        output = restTemplate.getForObject(url, String.class);
      } catch (Exception ex) {
        output = "ERROR:" + ex.getMessage();
      }
    }

    model.addAttribute("opt", opt);
    model.addAttribute("myparam", param);
    model.addAttribute("env", env);
    model.addAttribute("output", output);

		return "validator";
	}
}
