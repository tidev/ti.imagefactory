'use strict';

module.exports = config => {
	config.set({
		basePath: '../..',
		frameworks: [ 'jasmine' ],
		files: [
			'test/unit/specs/**/*spec.js'
		],
		reporters: [ 'mocha', 'junit' ],
		plugins: [
			'karma-*'
		],
		titanium: {
			sdkVersion: config.sdkVersion || '9.0.0.v20200205142057'
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
