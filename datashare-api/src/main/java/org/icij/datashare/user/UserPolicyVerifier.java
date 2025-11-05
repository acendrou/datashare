package org.icij.datashare.user;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.Adapter;

public class UserPolicyVerifier {
    private static UserPolicyVerifier instance;
    private final Enforcer enforcer;
    private static final String DEFAULT_POLICY_FILE = "src/main/resources/casbin/model.conf";
    private static final boolean ENABLE_CASBIN_LOG = false;

    private UserPolicyVerifier(UserPolicyRepository repository) {
        Adapter adapter = new UserPolicyRepositoryAdapter(repository);
        Model model = new Model();
        model.loadModel(DEFAULT_POLICY_FILE);
        this.enforcer = new Enforcer(model, adapter, ENABLE_CASBIN_LOG);
    }

    public static synchronized UserPolicyVerifier getInstance(UserPolicyRepository repository) {
        if (instance == null) {
            instance = new UserPolicyVerifier(repository);
        }
        return instance;
    }

    public Enforcer getEnforcer() {
        return enforcer;
    }

}
