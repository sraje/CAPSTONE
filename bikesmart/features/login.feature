Feature: Login feature

Scenario: As a user, I should be able to associate my identity with a bike
	Given I am on the login screen
	When I enter valid credentials and press login
	Then I should be taken to the Bikesmart home screen
	
Scenario: As a user, I should be able to disassociate my identity with a bike (logout)
	Given I have logged into Bikesmart
	When I press logout
	Then I should be taken back to the Bikesmart login screen

