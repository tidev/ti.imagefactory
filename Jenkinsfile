#!groovy
library 'pipeline-library'

def isMaster = env.BRANCH_NAME.equals('stable')

buildModule {
	sdkVersion = '9.3.2.GA'
	npmPublish = isMaster
	iosLabels = 'osx && xcode-12'
}
