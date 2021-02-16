function getHtmlParas(str) {
    var sid = str.split('-');
    var n = sid.length;
    var vid = sid[n - 1].split(".")[0];
    var pid = sid[n - 2];
    return [pid, vid];
}
function checkplay(pid, vid) {
    var jsUrl = utils.match(source, "regex:<script src=\"(/playdata/.+?)\">##$1");
    jsUrl = utils.absUrl(url,jsUrl);
    var jsContent = utils.get(jsUrl);
    var flvArray = utils.match(jsContent,"regex:.+?(\\[.+?]]]).+##$1");
    var preUrl= eval(flvArray)[pid][1][vid];
    return preUrl.split("$")[1];
}
params = getHtmlParas(url);
result = checkplay(params[0], params[1]);
