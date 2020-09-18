#!groovy
library 'pipeline-library'

def isMaster = env.BRANCH_NAME.equals('master')

buildModule {
	// defaults:
	// nodeVersion = '8.2.1' // Must have version set up on Jenkins master before it can be changed
	sdkVersion = '9.0.0.v20200205142057'
	npmPublish = isMaster
	iosLabels = 'osx && xcode-11'
	// androidAPILevel = '23' // if changed, must install on build nodes
}
