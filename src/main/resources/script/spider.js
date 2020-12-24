var page = require('webpage').create();
var system = require('system');
if (system.args.length === 1) {
    console.log('run args must be provide!');
    //这行代码很重要。凡是结束必须调用。否则phantomjs不会停止
    phantom.exit();
}
//为了提升加载速度，不加载图片
page.settings.loadImages = false;
//超过10秒放弃加载
page.settings.resourceTimeout = 10000;

// 参数

var url = system.args[1];
console.log(url)
var referer = system.args[2];
var cookie = system.args[3];
var userAgent = system.args[4];
var customHeaders = {
    'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36',
    'Referer': url
};
if (referer !== StringUtil.EMPTY) {
    customHeaders['Referer'] = referer;
}
if (cookie !== StringUtil.EMPTY) {
    customHeaders['Cookie'] = cookie;
}
if (userAgent !== StringUtil.EMPTY) {
    customHeaders['User-Agent'] = userAgent;
}
page.customHeaders = customHeaders;
page.open(url, function (status) {
    if (status !== 'success') {
        console.log('failed');
    } else {
        console.log(page.content);
    }
    phantom.exit();
});
