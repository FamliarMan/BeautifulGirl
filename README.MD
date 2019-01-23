# 开发指南
## 本项目仅做个人学习练手使用，禁止传播盈利，如若私自传播，本人不负任何法律责任

## 如何新增一个爬虫
### 1.继承接口
* 如果该网站要爬取的是图片类网站，请实现WebPictureSource接口
* 如果该网站要爬去的是视频网站，请继承WebVideoSource接口
* 如果该网站支持搜索功能，还请另外实现SearchSource接口

### 2.添加注解
爬虫完成后请加上注解@WebSource,如下：
```
@WebSource(false,0)
class FanliSpider : WebPictureSource{
```
其中，第一个参数代表访问该网站是否需要vpn，第二个参数用来排序，数字越大，越靠后。

经过上面两步后即可完成，无需再写任何其他代码。

## 各接口实现介绍
具体实现请直接看代码，注意，所有的网页html的获取都封装好了，直接调用 **RetrofitManager.getWebsiteHtml**

