# cloudflare-scrape-Android  
A tool to bypass the Cloudflare DDOS page.  


Inspired by [cloudflare-scrape](https://github.com/Anorov/cloudflare-scrape).  
# GET START  
### Dependent project  
[Mozilla Rhino](https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino/Download_Rhino)  

### Example  
```java
    Cloudflare cf = new Cloudflare(url);
    cf.setUser_agent(UA);
    cf.getCookies(new Cloudflare.cfCallback() {
        @Override
        public void onSuccess(List<HttpCookie> cookieList) {
	    something..
        }

        @Override
        public void onFail() {
            something..
        }
    });
```  
PS: When you want to use cookieList for String, you needed call `Cloudflare.listToString(cookieList)` conversion to String.  
  
If you need to use jsoup  
```java
Map<String, String> cookies = Cloudflare.List2Map(cookies);
```  
## issues
If you find any issues, please file a bug after checking for duplicates so I can fix it.
