package org.icij.datashare.user;

import org.casbin.jcasbin.main.Enforcer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserPolicyVerifierTest {
    @Mock private UserPolicyRepository repository;
    private UserPolicyVerifier verifier;

    @Before
    public void setUp() {
        initMocks(this);
        UserPolicy policy1 = new UserPolicy("user1", "project1", true, false, false);
        UserPolicy policy2 = new UserPolicy("user2", "project2", false, true, true);
        List<UserPolicy> policies = Arrays.asList(policy1, policy2);
        when(repository.getAll()).thenReturn(policies);
        verifier =  UserPolicyVerifier.getInstance(repository);
    }
    public static void testEnforce(Enforcer enforcer, Object subject, Object obj, String act, boolean expectedResult) {
        try {
            boolean enforcedResult = enforcer.enforce(subject, obj, act);
            assertEquals(String.format("%s, %s, %s: %b, supposed to be %b", subject, obj, act, enforcedResult, expectedResult), expectedResult, enforcedResult);
        } catch (Exception ex) {
            throw new RuntimeException(String.format("Enforce Error: %s", ex.getMessage()), ex);
        }
    }
    @Test
    public void testPermissionEnforcement() {

        Enforcer enforcer = verifier.getEnforcer();

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