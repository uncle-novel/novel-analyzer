if (result.startsWith("javascript")) {
    result = utils.match(result, "regex:javascript:Chapter[(](.+?),(.+?)[)];##https://www.5tns.com/read/$2/$1");
}
result;
