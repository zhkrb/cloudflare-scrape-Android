# cloudflare-scrape-Android  
A tool to bypass the Cloudflare DDOS page.  


Inspired by [cloudflare-scrape](https://github.com/Anorov/cloudflare-scrape).  
## GET START  
### Download

- [JCenter][1]

[1]: https://bintray.com/zhkrb/cloudflare-scrape-android/scrape-rhino/

### Maven
Step 1. Add the JitPack repository to your build file
```xml
<repositories>
  <repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
  </repository>
</repositories>
```
Step 2. Add the dependency
```xml
<dependency>
  <groupId>com.zhkrb.cloudflare-scrape-android</groupId>
  <artifactId>scrape-rhino</artifactId>
  <version>0.1.1</version>
  <type>pom</type>
</dependency>
```

### Gradle via JCenter
##### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
    allprojects {
   		repositories {
   			...
   			maven { url 'https://jitpack.io' }
   		}
    }
```    
##### Step 2. Add the dependency
```
compile 'com.zhkrb.cloudflare-scrape-android:scrape-rhino:0.1.1'
```


### Example  
```java
    Cloudflare cf = new Cloudflare(url);
    cf.setUser_agent(UA);
    cf.getCookies(new Cloudflare.cfCallback() {
        @Override
        public void onSuccess(List<HttpCookie> cookieList, boolean hasNewUrl ,String newUrl) {
	    something..
        }

        @Override
        public void onFail() {
            something..
        }
    });
```  
PS: When you want to use cookieList for String, you needed call `Cloudflare.listToString(cookieList)` conversion to String. 
PS: If url redirect to new url, hasNewUrl will return true and 3rd parameter return new url 
  
If you need to use jsoup  
```java
Map<String, String> cookies = Cloudflare.List2Map(cookies);
```  
## Dependent project
[rhino](https://github.com/mozilla/rhino)

## issues
If you find any issues, please file a bug after checking for duplicates so I can fix it.
