# cloudflare-scrape-Android  
A tool to bypass the Cloudflare DDOS page.  

#### Notice
`scrape-v8` and `scrape-rhino` is discarded, please use `scrape-webview`.

Inspired by [cloudflare-scrape](https://github.com/Anorov/cloudflare-scrape).  
## GET START  
### Download

- [JCenter][1]

[1]: https://bintray.com/zhkrb/cloudflare-scrape-android/scrape-v8/

#### Maven

```xml
<dependency>
  <groupId>com.zhkrb.cloudflare-scrape-android</groupId>
  <artifactId>scrape-webview</artifactId>
  <version>0.0.2</version>
  <type>pom</type>
</dependency>
```

#### Gradle via JCenter

``` groovy
implementation 'com.zhkrb.cloudflare-scrape-android:scrape-webview:0.0.2'
```

### Example  
```java
    Cloudflare cf = new Cloudflare(Activity, url);
    cf.setUser_agent(UA);
    cf.setCfCallback(new CfCallback() {
        @Override
        public void onSuccess(List<HttpCookie> cookieList, boolean hasNewUrl, String newUrl) {
            something...
        }
          
        @Override
        public void onFail(int code,String msg) {
            something...
        }
    });
    cf.getCookies();
 
```  
PS: When you want to use cookieList for String or convert to other type, you needed use `ConvertUtil` to conversion.  
PS: If url redirect to new url, hasNewUrl will return true and 3rd parameter return new url
   
If you need to use jsoup  
```java
Map<String, String> cookies = ConvertUtil.List2Map(cookies);
```  
## Dependent project  
[eclipsesource/J2V8](https://github.com/eclipsesource/J2V8)  
[rhino](https://github.com/mozilla/rhino)

## issues and test
If you find any issues, please file a bug and provide url after checking for duplicates so I can fix it. 

We also provide a demo for you to test, you can go to [release](https://github.com/zhkrb/cloudflare-scrape-Android/releases) to get it

