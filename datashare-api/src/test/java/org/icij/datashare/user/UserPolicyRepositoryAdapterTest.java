package org.icij.datashare.user;

import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.Adapter;
import org.casbin.jcasbin.persist.file_adapter.FileAdapter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserPolicyRepositoryAdapterTest {

    @Mock
    private UserPolicyRepository repository;

    private UserPolicyRepositoryAdapter adapter;

    public static void testEnforce(Enforcer e, Object sub, Object obj, String act, boolean res) {
        try {
            boolean myRes = e.enforce(sub, obj, act);
            assertEquals(String.format("%s, %s, %s: %b, supposed to be %b", sub, obj, act, myRes, res), res, myRes);
        } catch (Exception ex) {
            throw new RuntimeException(String.format("Enforce Error: %s", ex.getMessage()), ex);
        }
    }
    @Before
    public void setUp() {
        initMocks(this);
        adapter = new UserPolicyRepositoryAdapter(repository);
    }

   @Test
    public void testLoadPolicyLoadsPoliciesIntoModel() {
        UserPolicy policy1 = new UserPolicy("user1", "project1", true, false, false);
        UserPolicy policy2 = new UserPolicy("user2", "project2", false, true, true);
        List<UserPolicy> policies = Arrays.asList(policy1, policy2);
        when(repository.getAll()).thenReturn(policies);

        Model model = new Model();
        String filePath = "src/test/resources/casbin/model.conf";
        model.loadModel(filePath);
        Enforcer enforcer = new Enforcer(model, adapter);

        testEnforce(enforcer, "user1", "project1", "read", true);
        testEnforce(enforcer, "user1", "project1", "write", false);
        testEnforce(enforcer, "user1", "project1", "admin", false);
        testEnforce(enforcer, "user2", "project2", "read", false);
        testEnforce(enforcer, "user2", "project2", "write", true);
        testEnforce(enforcer, "user2", "project2", "admin", true);

        //user 1 is not allowed to read project2
        testEnforce(enforcer, "user1", "project2", "read", false);
    }
}