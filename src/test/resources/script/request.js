var requestParams = {
    headers: {
        spider: "unclezs"
    },
    method: "PATCH",
    url: "http://httpbin.org/patch"
};
html = utils.request(JSON.stringify(requestParams));
utils.out(html);
