package matxu.framework.agregation.feeder.config;

import matxu.framework.agregation.feeder.processors.ScenaryItemProcessor;
import matxu.framework.agregation.feeder.config.listener.JobCompletionNotificationListener;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;

@Configuration
@ComponentScan("matxu.framework.agregation.feeder.config.db")
@ComponentScan("matxu.framework.agregation.feeder.config.listener")
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
    public FlatFileItemReader<Scenary> reader_1(@Value("#{jobParameters}")  Map<String, JobParameter> jobParameters) {
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
    public ScenaryItemProcessor processor_1(@Value("#{jobParameters}")  Map<String, JobParameter> jobParameters) {
        return new ScenaryItemProcessor();
    }

    @Bean
    @StepScope
    public MongoItemWriter<Scenary> writer_1(@Value("#{jobParameters}")  Map<String, JobParameter> jobParameters) {
        MongoItemWriter<Scenary> writer = new MongoItemWriter<Scenary>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("scenary");
        return writer;
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean(name="batch_2")
    public Job batch_2(JobCompletionNotificationListener listener, Step loadScenary_2) {
        return jobBuilderFactory.get("batch_2")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(loadScenary_2)
                .end()
                .build();
    }

    @Bean
    public Step loadScenary_2(MongoItemWriter<Scenary> writer) {
        return stepBuilderFactory.get("loadScenary_2")
                .<Scenary, Scenary> chunk(10)
                .reader(reader_1(NOT_SET))
                .processor(processor_1(NOT_SET))
                .writer(writer_1(NOT_SET))
                .build();
    }
    // end::jobstep[]


}
