const testsContext = require.context('./specs', true, /\.spec\.js$/);

testsContext.keys().forEach(testsContext);
