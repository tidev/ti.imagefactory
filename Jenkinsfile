#!groovy
library 'pipeline-library'

def isMaster = env.BRANCH_NAME.equals('stable')

buildModule {
	sdkVersion = '11.1.1.GA'
	npmPublish = isMaster
	iosLabels = 'osx && xcode-12'
}
