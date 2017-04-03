# cloudflare-scrape-Android  
A tool to bypass the Cloudflare DDOS page.  


Inspired by [cloudflare-scrape](https://github.com/Anorov/cloudflare-scrape).  
# GET START  
### Dependent project  
eclipsesource  
### Example  
```java
cloudflare cf = new cloudflare("eg.example.com");
cf.set("Mozilla/5.0 XXXXXXXXXXX");
List<HttpCookie> httpCookieList = cf.cookies();
```  
If you need to use jsoup  
```java
Map<String, String> cookies = cf.List2Map(cf.cookies());
```  
## If you find any issues, please file a bug after checking for duplicates.