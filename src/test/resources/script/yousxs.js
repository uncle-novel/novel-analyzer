skeyPage = utils.get('https://www.yousxs.com/js/bootstrap.min.js');
skey = utils.match(skeyPage, "regex:ey\"]=\"(.+?)\"##$1");
result = utils.match(source, "regex:url: '(https://.+?skey=)'##$1") + skey;


skeyPage = utils.get('https://www.yousxs.com/js/bootstrap.min.js');skey = utils.match(skeyPage, "regex:ey\"]=\"(.+?)\"##$1");result = utils.match(source, "regex:url: '(https://.+?skey=)'##$1") + skey;


