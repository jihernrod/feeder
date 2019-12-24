package matxu.framework.agregation.feeder.config;

import matxu.framework.agregation.feeder.processors.ScenaryItemProcessor;
import matxu.framework.agregation.feeder.listener.JobCompletionNotificationListener;
import matxu.framework.agregation.feeder.model.Scenary;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;

@Configuration
@EnableBatchProcessing
public class ScenaryBatchConfiguration {

    public Map<String, JobParameter> NOT_SET =null;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader<Scenary> reader(@Value("#{jobParameters}")  Map<String, JobParameter> jobParameters) {
        return new FlatFileItemReaderBuilder<Scenary>()
                .name("scenaryItemReader")
                .resource(new ClassPathResource("scenary.csv"))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Scenary>() {{
                    setTargetType(Scenary.class);
                }})
                .build();
    }

    @Bean
    @StepScope
    public ScenaryItemProcessor processor(@Value("#{jobParameters}")  Map<String, JobParameter> jobParameters) {
        return new ScenaryItemProcessor();
    }

    @Bean
    @StepScope
    public MongoItemWriter<Scenary> writer(@Value("#{jobParameters}")  Map<String, JobParameter> jobParameters) {
        MongoItemWriter<Scenary> writer = new MongoItemWriter<Scenary>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("scenary");
        return writer;
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(MongoItemWriter<Scenary> writer) {
        return stepBuilderFactory.get("step1")
                .<Scenary, Scenary> chunk(10)
                .reader(reader(NOT_SET))
                .processor(processor(NOT_SET))
                .writer(writer(NOT_SET))
                .build();
    }
    // end::jobstep[]


}
