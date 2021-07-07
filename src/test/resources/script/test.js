var t=utils.match(source, "//a[contains(text(),'查看完整目录')]/@href")
url=utils.absUrl(url,t)
source=utils.get(url)
result=utils.matchList(source,"//ul[@class='chapter']/li/a")


var a = "分卷阅读2";
var b = "分卷阅读1";
parseInt(a.name.replace("分卷阅读","")) - parseInt((b.name.replace("分卷阅读","")))
