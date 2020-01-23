package env;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import service.RegistrationService;

public class Hooks {

    @Before("@WithPatients")
    public void registerPatient(Scenario scenario) {
        RegistrationService.initializePatients();
    }

    @After("@WithPatients")
    public void unregisterPatient(Scenario scenario) {
        RegistrationService.unregisterPatients();
    }
}
