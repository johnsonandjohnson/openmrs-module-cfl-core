package env;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import gherkin.formatter.model.Result;
import org.junit.Assume;

import java.lang.reflect.Field;
import java.util.List;

import static env.DriverUtil.getDefaultDriver;

public class Hooks {
	

	@Before("@skip_scenario")
	public void skip_scenario(Scenario scenario){
		//skip testing scenario marked with @skip_scenario tag
		System.out.println("SKIP SCENARIO: " + scenario.getName());
		Assume.assumeTrue(false);
	}
}
