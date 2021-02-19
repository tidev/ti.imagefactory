/* global danger, fail, message */

// requires
const junit = require('@seadub/danger-plugin-junit').default;
const dependencies = require('@seadub/danger-plugin-dependencies').default;
const moduleLint = require('@seadub/danger-plugin-titanium-module').default;
const fs = require('fs');
const path = require('path');
const ENV = process.env;

// Add links to artifacts we've stuffed into the ENV.ARTIFACTS variable
async function linkToArtifacts() {
	if (ENV.ARTIFACTS && (ENV.BUILD_STATUS === 'SUCCESS' || ENV.BUILD_STATUS === 'UNSTABLE')) {
		const artifacts = ENV.ARTIFACTS.split(';');
		if (artifacts.length !== 0) {
			const artifactsListing = '- ' + artifacts.map(a => danger.utils.href(`${ENV.BUILD_URL}artifact/${a}`, a)).join('\n- ');
			message(`:floppy_disk: Here are the artifacts produced:\n${artifactsListing}`);
		}
	}
}

async function checkLintLog() {
	const lintLog = path.join(__dirname, 'lint.log');
	if (!fs.existsSync(lintLog)) {
		return;
	}
	const contents = fs.readFileSync(lintLog, 'utf8');
	fail('`npm run lint` failed, please check messages below for output.');
	message(`:bomb: Here's the output of \`npm run lint\`:\n\`\`\`${contents}\`\`\``);
}

async function main() {
	// do a bunch of things in parallel
	// Specifically, anything that collects what labels to add or remove has to be done first before...
	await Promise.all([
		junit({ pathToReport: './TESTS-*.xml' }),
		dependencies({ type: 'npm' }),
		linkToArtifacts(),
		checkLintLog(),
		moduleLint(),
	]);
}
main()
	.then(() => process.exit(0))
	.catch(err => {
		fail(err.toString());
		process.exit(1);
	});
