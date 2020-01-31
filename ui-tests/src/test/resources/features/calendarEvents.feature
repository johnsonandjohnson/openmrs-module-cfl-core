Feature: Patient's calendar events
  As a user I should be able to see proper messages on the calendar

  @WithPatients
  Scenario: View today's Adherence report daily on the calendar
    Given I log in
    Then I navigate to patient "[PATIENT] [ONE]" messages
    Then element having xpath "//h3[contains(text(),'Calendar Overview')]" should be present
    Then I wait and click on element having xpath "//button[contains(text(),'Manage messages')]"
    Then element having xpath "//h2[contains(text(),'Manage messages')]" should be present
    Then I pick best contact time for 2 minutes in the future
    Then I wait and click on element having xpath "//button[contains(text(),'Save')]"
    Then I click on element having xpath "//a[contains(@href,'Adherence report daily')]"
    Then I click on element having xpath "(//div[contains(@id,'Adherence report daily')]//input[@name='Call'])[1]"
    Then I click on element having xpath "//button[contains(text(),'Save')]"
    Then I refresh page
    Then today's calendar tile contains "Adherence report daily"