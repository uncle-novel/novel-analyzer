var page = require('webpage').create();
page.settings.loadImages = false;
var cookieStr = "_yep_uuid=c395968a-17d7-e37c-88ba-94f245b64c45; e1=%7B%22pid%22%3A%22qd_P_vipread%22%2C%22eid%22%3A%22qd_G94%22%2C%22l2%22%3A4%2C%22l1%22%3A15%7D; e2=%7B%22pid%22%3A%22qd_P_vipread%22%2C%22eid%22%3A%22%22%2C%22l2%22%3A4%2C%22l1%22%3A15%7D; _csrfToken=1bVJens69OPMqvdJgsS0Bry2U1LDni7qaBuwUAlD; newstatisticUUID=1608791066_10517697; ywguid=1585503310; ywkey=ywqK8YfCTwpY; ywopenid=7387FAAE42281F44434B7CF4977CFD3E; qdrs=0%7C3%7C0%7C0%7C1; showSectionCommentGuide=1; qdgd=1; pageOps=1; e2=%7B%22pid%22%3A%22qd_P_auto_dingyue%22%2C%22eid%22%3A%22qd_M186%22%2C%22l1%22%3A2%7D; rcmClose=1; e1=%7B%22pid%22%3A%22qd_P_my_bookshelf%22%2C%22eid%22%3A%22qd_M185%22%2C%22l1%22%3A2%7D; bc=1012284323%2C3247938%2C2952453%2C1735921%2C1024617405; rcr=1735921%2C1024617405%2C1012284323%2C2952453%2C3247938%2C1024416983%2C1025224742; lrbc=1735921%7C45491650%7C1%2C1024617405%7C625270110%7C0%2C1025224742%7C622420708%7C0"
page.customHeaders = {
    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36",
};
var url2 = 'http://httpbin.org/get';
var url3 = 'http://blog.unclezs.com';
var url = "https://vipreader.qidian.com/chapter/1735921/45491650";
page.open(url3, function (status) {
    if (status !== 'success') {
        console.log('Unable to access network');
    } else {
        console.log(page.content);
    }
    phantom.exit();
});
