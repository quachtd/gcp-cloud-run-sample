## Build and deploy
```tfvars
gcloud run jobs deploy job-quickstart \
    --source . \
    --tasks 1 \
    --set-env-vars SLEEP_MS=10000 \
    --set-env-vars FAIL_RATE=0.1 \
    --max-retries 3 \
    --region us-west1 \
    --project=cellmartsandbox
```

## Reference
(https://cloud.google.com/run/docs/quickstarts/jobs/build-create-java)

