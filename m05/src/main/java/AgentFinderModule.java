import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * Created by soomin on 2015. 12. 23..
 */
public class AgentFinderModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AgentFinder.class)
            //.annotatedWith(Names.named("primary"))
            .to(AgentFinderImpl.class).in(Singleton.class);
        bind(HollyWoodService.class);
    }
}
