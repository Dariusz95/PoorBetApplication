package com.poorbet.oddstraining;

import com.poorbet.oddstraining.pipeline.TrainingPipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@RequiredArgsConstructor
@ConfigurationPropertiesScan
public class OddsTrainingApplication implements CommandLineRunner {

	private final TrainingPipeline trainingPipeline;

	public static void main(String[] args) {
		SpringApplication.run(OddsTrainingApplication.class, args);
	}

	@Override
	public void run(String... args) {
		trainingPipeline.run();
	}

}
