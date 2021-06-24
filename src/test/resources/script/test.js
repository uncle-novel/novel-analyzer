var reqParams = {
    url: url,
    headers: {
        "Referer": url,
        "Cookie": params.headers.Cookie
    },
    method: "GET"
}
var html = utils.request(JSON.stringify(reqParams));
result = utils.match(html, "//p/allText()")
