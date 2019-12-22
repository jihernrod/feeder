package matxu.framework.agregation.feeder.processors;

import matxu.framework.agregation.feeder.model.Scenary;
import org.springframework.batch.item.ItemProcessor;

public class ScenaryItemProcessor implements ItemProcessor<Scenary, Scenary> {

    @Override
    public Scenary process(final Scenary scenary) throws Exception {
        return scenary;
    }

}
