import com.google.inject.Inject;

/**
 * Created by soomin on 2015. 12. 23..
 */
public class HollyWoodService {
    private AgentFinder agentFinder;

    @Inject
    public HollyWoodService(AgentFinder agentFinder) {
        this.agentFinder = agentFinder;
    }

    public AgentFinder getAgentFinder() {
        return agentFinder;
    }
}
