drop table batch_job_configuration;

CREATE TABLE batch_job_configuration (
  job_configuration_id   INT(10) PRIMARY KEY NOT NULL,
  application_name       VARCHAR(255)                 NOT NULL,
  job_name               VARCHAR(255)                 NOT NULL,
  job_incrementer        VARCHAR(255),
  job_configuration_type INT                          NOT NULL
);

CREATE TABLE batch_configuration_value (
  id                   INT(10) PRIMARY KEY NOT NULL,
  job_configuration_id INT(10)                      NOT NULL,
  value_key            VARCHAR(255)                 NOT NULL,
  configuration_value  VARCHAR(255),
  FOREIGN KEY (job_configuration_id) REFERENCES batch_job_configuration (job_configuration_id)
);


CREATE TABLE batch_configuration_parameter (
  id                   INT(10) PRIMARY KEY NOT NULL,
  job_configuration_id INT(10)                      NOT NULL,
  parameter_name       VARCHAR(255)                 NOT NULL,
  parameter_value      VARCHAR(255)                 NOT NULL,
  parameter_type       INT                          NOT NULL,
  FOREIGN KEY (job_configuration_id) REFERENCES batch_job_configuration (job_configuration_id)
);

