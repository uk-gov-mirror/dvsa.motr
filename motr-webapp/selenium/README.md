# MOTR Selenium

Automated browser test suite for MOT Reminders

### Configuration

##### Commandline (for developers)
To run locally against remote env:
```./runTests <env_url> <aws_region> <table_subscription_name> <table_pending_subscription_name> <table_sms_confirmation_name>```

##### Properties file
Tests suite can be configured through Java properties which can be pointed at with the help of following environment variables:
* SELENIUM_DRIVER_PROPERTIES
* SELENIUM_ENV_PROPERTIES

###### SELENIUM_DRIVER_PROPERTIES
Points to the **driver** config file that overrides default properties such as:
```
test.browserName=firefox
# when left empty, takes whatever version available or default in the grid 
test.browserVersion=

# grid to use: selenium, browserstack, or leave this empty to use local browser instance
test.gridEnabled=
test.javascript.enabled=yes
```
###### SELENIUM_ENV_PROPERTIES

Points to the **environment** config file that overrides default properties such as:
```
# url of the environment
test.baseUrl=https://testurl

# AWS region of the dynamoDb
test.dyanmoDb.region=

# dynamoDb table name for subscriptions
test.dynamoDb.table.name=

# dynamoDb table name for pending subscriptions
test.dynamoDb.pendingTable.name=

# whether to store PNG snapshot in case of error
test.screenshots.error.enabled=yes

# path to location where PNG snapshots of errors are stored
test.screenshots.error.folder=/tmp/error

# url pointing to a grid (Browserstack, Saucelabs, Selenium Grid)
test.gridUrl=
```

