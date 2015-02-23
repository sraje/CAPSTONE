Feature: Login feature

Scenario: As a user, I should be able to associate my identity with a bike
	Given I am about to login
	When I enter valid credentials and press login
	Then I should be taken to the Bikesmart home screen
	

