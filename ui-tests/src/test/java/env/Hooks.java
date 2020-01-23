package env;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.junit.Assume;
import service.RegistrationService;

public class Hooks {

    @Before("@WithPatients")
    public void registerPatient(Scenario scenario) {
        System.out.println("Creating patient for scenario: " + scenario.getName());
        RegistrationService.initializePatients();
    }

    @After("@WithPatients")
    public void unregisterPatient(Scenario scenario) {
        System.out.println("Deleting patient for scenario: " + scenario.getName());
        RegistrationService.unregisterPatients();
    }

    @Before("@skip_scenario")
    public void skip_scenario(Scenario scenario) {
        //skip testing scenario marked with @skip_scenario tag
        System.out.println("SKIP SCENARIO: " + scenario.getName());
        Assume.assumeTrue(false);
    }
}
