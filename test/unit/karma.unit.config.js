'use strict';

const path = require('path');
const fs = require('fs-extra');

function projectManagerHook(projectManager) {
	projectManager.once('prepared', function () {
		const src = path.join(__dirname, 'images', 'flower.jpg.keep');
		const dest = path.join(this.karmaRunnerProjectPath, 'Resources', 'flower.jpg.keep');
		fs.copySync(src, dest);
	});
}
projectManagerHook.$inject = [ 'projectManager' ];

module.exports = config => {
	config.set({
		basePath: '../..',
		frameworks: [ 'jasmine', 'projectManagerHook' ],
		files: [
			'test/unit/specs/**/*spec.js'
		],
		reporters: [ 'mocha', 'junit' ],
		plugins: [
			'karma-*',
			{
				'framework:projectManagerHook': [ 'factory', projectManagerHook ]
			}
		],
		titanium: {
			sdkVersion: config.sdkVersion || '9.2.0.v20200918123430'
		},
		customLaunchers: {
			android: {
				base: 'Titanium',
				browserName: 'Android AVD',
				displayName: 'android',
				platform: 'android'
			},
			ios: {
				base: 'Titanium',
				browserName: 'iOS Emulator',
				displayName: 'ios',
				platform: 'ios'
			}
		},
		browsers: [ 'android', 'ios' ],
		client: {
			jasmine: {
				random: false
			}
		},
		singleRun: true,
		retryLimit: 0,
		concurrency: 1,
		captureTimeout: 300000,
		logLevel: config.LOG_DEBUG
	});
};
