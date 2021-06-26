var t=utils.match(source, "//a[contains(text(),'查看完整目录')]/@href")
url=utils.absUrl(url,t)
source=utils.get(url)
result=utils.matchList(source,"//ul[@class='chapter']/li/a")
