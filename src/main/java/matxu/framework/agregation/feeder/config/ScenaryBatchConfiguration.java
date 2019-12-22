package matxu.framework.agregation.feeder.config;

import matxu.framework.agregation.feeder.processors.ScenaryItemProcessor;
import matxu.framework.agregation.feeder.listener.JobCompletionNotificationListener;
import matxu.framework.agregation.feeder.model.Scenary;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableBatchProcessing
public class ScenaryBatchConfiguration {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Scenary> reader() {
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
    public ScenaryItemProcessor processor() {
        return new ScenaryItemProcessor();
    }

    @Bean
    public MongoItemWriter<Scenary> writer() {
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
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }
    // end::jobstep[]


}
