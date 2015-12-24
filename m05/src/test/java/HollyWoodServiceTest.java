import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by soomin on 2015. 12. 23..
 */
public class HollyWoodServiceTest {

    private Injector injector = null;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new AgentFinderModule());
        assertThat(injector, is(not(nullValue())));
    }

    @Test
    public void getHollyWoodService() {
        HollyWoodService ho1 = injector.getInstance(HollyWoodService.class);
        assertThat(ho1, is(not(nullValue())));

        HollyWoodService ho2 = injector.getInstance(HollyWoodService.class);

        System.out.println(ho1);
        System.out.println(ho2);
        assertThat(ho1 != ho2, is(true));

        System.out.println(ho1.getAgentFinder());
        System.out.println(ho2.getAgentFinder());
        assertThat(ho1.getAgentFinder() == ho2.getAgentFinder(), is(true));
    }
}
