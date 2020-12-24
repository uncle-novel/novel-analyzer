var system = require('system');
if (system.args.length === 1) {
    console.log('Try to pass some args when invoking this script!');
} else {
    system.args.forEach(function (arg, i) {
        console.log(i + ': ' + arg);
    });
}
var url = system.args[1];
var referer = system.args[2];
var cookie = system.args[3];
var userAgent = system.args[4];
phantom.exit();
