CFL Automated Test Suite
==================================

The suite is built using `selenium-cucumber : Automation Testing Using Java`

`selenium-cucumber` is a behavior driven development (BDD) approach to write automation test script to test Web.
It enables you to write and execute automated acceptance/unit tests.
It is cross-platform, open source and free.
Automate your test cases with minimal coding.
[More Details](http://seleniumcucumber.info/)

Documentation
-------------
* [Installation](https://github.com/selenium-cucumber/selenium-cucumber-java/blob/master/doc/installation.md)
* [Predefined steps](https://github.com/selenium-cucumber/selenium-cucumber-java/blob/master/doc/canned_steps.md)

Accessibility
-------------

A step for checking the accessibility of the current page has been added. In order to use, add an appropriate 
`Then` step:

```code 
Then Check Accessibility: max X violations
```

Where **X** is the maximum number of violations that will not fail the build.

Currently [axe 2](https://axe-core.org/) is being used as the accessibility check engine.

Running
-------

Run the following commands in `ui-tests` directory with parameters specifying a browser, a profile and a webdriver's path (make sure it's adequate to your local environment).
The webdrivers need to be accessible at the locations specified in webdriver's paths.
They can be downloaded from e.g.

# Firefox

https://github.com/mozilla/geckodriver/releases

# Chrome

https://chromedriver.chromium.org/

# Safari

Driver is installed along with the browser

# Edge

https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/

Example:

* for Firefox
```bash
mvn clean test -Dbrowser=firefox -Pui-tests -Dwebdriver.gecko.driver=/home/user/geckodriver
```
* for Chrome
```bash
mvn clean test -Dbrowser=chrome -Pui-tests -Dwebdriver.chrome.driver=/home/user/chromedriver
```
* for Safari
```bash
mvn clean test -Dbrowser=safari -Pui-tests -Dwebdriver.safari.driver=/home/user/safaridriver
```
* for Edge
```bash
mvn clean test -Dbrowser=edge -Pui-tests -Dwebdriver.edge.driver=/home/user/MicrosoftWebDriver.exe
```

You can run browsers Firefox and Chrome in headless mode (tests are performed without opened browser window)
```bash
# for Firefox
mvn clean test -Dbrowser=firefox -Pui-tests -Dwebdriver.gecko.driver=/home/user/geckodriver -Dheadless=true

# for Chrome
mvn clean test -Dbrowser=chrome -Pui-tests -Dwebdriver.chrome.driver=/home/user/chromedriver -Dheadless=true
```

Feature Hooks
-------
It is possible for the scenarios to initialize the database before executing them. We're using `env.Hooks` to configure
actions to be made before/after executing specific scenarios. After defining hook in `env.Hooks` file, annotate the 
scenario properly to connect them. The most popular case is pre-registering patients, described below.

#### Defined Hooks
##### @WithPatients
Flow
1. Delete all patients from `data/patients.json` in case there are some leftovers from previous executions
2. Register all patients from `data/patients.json`
3. Cleanup by deleting all patients from `data/patients.json` 

Important notes
* The patient identifiers defined within the file has to be unique across the whole system (and file itself),
 so make sure dummy patients have those set to values that do not fits the schema which is used to generate them,
 like `000000' for OpenMRS ID. In case of mentioned OpenMRS ID, don't forget that it has to pass the
 [Check Digit Algorithm](https://wiki.openmrs.org/display/docs/Check+Digit+Algorithm)
 (You can validate your identifier on the website.)
