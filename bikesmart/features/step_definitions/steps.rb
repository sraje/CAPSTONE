## Test Step Definitions

## Starting Points
Given(/^I have logged into Bikesmart$/) do
	if element_exists("EditText id:'latitude_text'") then
		next
	else
		user = CREDS[:valid_user]
		login(user)
	end
end

Given(/^I am on the home screen$/) do
	element_exists("Button id:'logout_button'")
end

Given(/^I am on the login screen$/) do
	element_exists("Button id:'signup_button'")
end

When(/^I press Get Location$/) do
	touch_safe("Button id:'location_button'")
end

## Verification Steps
Then(/^I should be taken back to the Bikesmart login screen$/) do
	element_exists("Button id:'signup_button'")
end

Then(/^I should be taken to the Bikesmart home screen$/) do
	element_exists("Button id:'logout_button'")
end

Then(/^I should be presented with the bike's current location$/) do
	##TODO:
end

## Action Steps
When(/^I enter valid credentials and press login$/) do
	user = CREDS[:valid_user]
	login(user)
end

When(/^I enter invalid credentials and press login$/) do
	user = CREDS[:invalid_user]
	login(user)
end

When(/^I press logout$/) do
	touch_safe("Button id:'logout_button'")
end

When(/^I press Sign Up$/) do
	touch_safe("Button id:'signup_button'")
end

When(/^I enter a valid username and password into the registration fields$/) do
	user = CREDS[:valid_user]
	registration(user)
end

Then(/^I am presented with an error message$/) do
	element_exists_safe("TextView id:'message'")
end

## Helper Functions
def login(user)
	touch_safe("Button id:'login_button'")
	enter_text("EditText id:'username'", user[:username]) 
	enter_text("EditText id:'password'", user[:password])
	touch_safe("Button id:'action_button'")
	element_exists_safe("EditText id:'latitude_text'")
end

def registration(user)
	enter_text("EditText id:'username_edit_text'", user[:username])
	enter_text("EditText id:'password_edit_text'", user[:password])
	enter_text("EditText id:'password_again_edit_text'", user[:password])
	touch_safe("Button id:'action_button'")
end

def element_exists_safe(element)
	sleep(0.5) ##TODO: Animations
	element_exists("EditText id:'latitude_text'")
end

def touch_safe(element)
	sleep(0.5)
	touch(element)
end
