Feature: Patient's messages functionality
  As a user I should be able to go through patient's message functionalities

  Scenario: Go through messages
    Given I log in
    Then element having id "apps" should be present
    Then I navigate to patient "Caregiver Test" dashboard
    Then element having class "patient-header" should be present
    When I click on element having id "messagesapp.basicMessages.patientDashboardLink"
    Then element having xpath "//h3[contains(text(),'Calendar Overview')]" should be present
    Then I wait and click on element having xpath "//button[contains(text(),'Manage messages')]"
    And I click on element having xpath "//a[contains(@href,'Adherence report daily')]"
    And I click on element having xpath "//p[contains(text(),'Adherence report daily')]"
    And I wait 5 seconds for element having xpath "//input[@name='Deactivate service']" to be enabled
    And I click on element having xpath "//input[@name='Deactivate service']"
    And I click on element having xpath "//div[contains(@id,'Adherence report daily')]/div[2]/form/div/label/input[@name='Deactivate service']"
    And I click on element having xpath "//button[contains(text(),'Save')]"
    Then element having xpath "//h3[contains(text(),'Calendar Overview')]" should be present
    And element having xpath "//a[contains(text(),'Patient')]" should be present
    And element having xpath "//a[contains(text(),'Caregiver -')]" should be present
    Then I wait and click on element having xpath "//button[contains(text(),'Manage messages')]"
    #todo remove manual refresh
    Then I wait 5 seconds for element having xpath "//button[contains(text(),'Calendar Overview')]" to be enabled
    And I refresh page
    Then I wait 5 seconds for element having xpath "//button[contains(text(),'Calendar Overview')]" to be enabled
    Then element having xpath "//div[@class='time-section']/span[contains(text(),'Patient')]" should be present
    Then element having xpath "//div[@class='time-section']/span[contains(text(),'Caregiver')]" should be present
    When I click on element having xpath "//input[contains(@class,'ant-time-picker-input')]"
    And I click on element having xpath "//li[contains(@class,'ant-time-picker')]"
    And I click on element having xpath "//div[contains(@class,'ant-time-picker-panel-column-2')]/div/div/input"
    And I click on element having xpath "//div[contains(@class,'ant-time-picker-panel-column-2')]/div/div[2]/div/ul/li[contains(@class,'ant-time-picker')]"
    And I click on element having xpath "//button[contains(text(),'Save')]"
    And I click on element having xpath "//button[contains(text(),'Calendar Overview')]"
    Then element having xpath "//h3[contains(text(),'Calendar Overview')]" should be present
    Then I wait and click on element having xpath "//button[contains(text(),'Manage messages')]"
    #todo remove manual refresh
    Then I wait 5 seconds for element having xpath "//button[contains(text(),'Calendar Overview')]" to be enabled
    And I refresh page
    Then I wait 5 seconds for element having xpath "//button[contains(text(),'Calendar Overview')]" to be enabled
    Then element having xpath "//div[contains(text(),'Message Type')]" should be present
    And element having xpath "//div[contains(text(),'Patient')]" should be present
    And element having xpath "//div[contains(text(),'Caregiver')]" should be present
    And element having xpath "//div[contains(text(),'Actions')]" should be present
    And element having xpath "//div[contains(text(),'Adherence report daily')]/following-sibling::div" should have text as "DEACTIVATED"
    And element having xpath "//div[contains(text(),'Adherence report daily')]/following-sibling::div[2]" should have text as "DEACTIVATED"
    When I refresh page
    And I click on element having xpath "//a[contains(@href,'Adherence report daily')]"
    And I click on element having xpath "//p[contains(text(),'Adherence report daily')]"
    And I wait 5 seconds for element having xpath "//input[@name='Call']" to be enabled
    And I click on element having xpath "//input[@name='Call']"
    And I click on element having xpath "//div[contains(@id,'Adherence report daily')]/div[2]/form/div/label/input[@name='Call']"
    And I click on element having xpath "//button[contains(text(),'Save')]"
    #todo remove manual refresh
    Then I wait 5 seconds for element having xpath "//button[contains(text(),'Manage messages')]" to be enabled
    And I refresh page
    Then I wait 5 seconds for element having xpath "//button[contains(text(),'Manage messages')]" to be enabled
    Then element having xpath "//h3[contains(text(),'Calendar Overview')]" should be present
    And element having class "fc-day-grid-event" should be present
    When I click on element having css ".u-pl-1_5em:nth-child(2) input"
    When I click on element having css ".u-pl-1_5em:nth-child(3) input"
    When I click on element having css ".u-pl-1_5em:nth-child(4) input"
    When I click on element having css ".u-pl-1_5em:nth-child(5) input"
    When I click on element having css ".u-pl-1_5em:nth-child(6) input"
    Then element having class "fc-day-grid-event" should not be present
    When I wait 5 seconds for element having xpath "//a[contains(text(),'Caregiver -')]" to be enabled
    When I forcefully click on element having xpath "//a[contains(text(),'Caregiver -')]"
    Then element having class "fc-day-grid-event" should not be present
    When I forcefully click on element having css ".u-pl-1_5em:nth-child(2) input"
    And I refresh page
    Then element having class "fc-day-grid-event" should be present

  Scenario: Close browser
    Then I close browser