package org.icij.datashare.user;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.Adapter;

public class UserPolicyVerifier {
    private final Enforcer enforcer;
    private static final String DEFAULT_POLICY_FILE = "src/main/resources/casbin/model.conf";
    public UserPolicyVerifier(UserPolicyRepository repository) {

        Adapter adapter = new UserPolicyRepositoryAdapter(repository);
        Model model = new Model();
        model.loadModel(DEFAULT_POLICY_FILE);
        this.enforcer = new Enforcer(model, adapter);
    }

    public Enforcer getEnforcer() {
        return enforcer;
    }

}
