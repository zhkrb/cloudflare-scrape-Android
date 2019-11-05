# cloudflare-scrape-Android  
A tool to bypass the Cloudflare DDOS page.  


Inspired by [cloudflare-scrape](https://github.com/Anorov/cloudflare-scrape).  
# GET START  
### Dependent project  
[eclipsesource/J2V8](https://github.com/eclipsesource/J2V8)  
  
If you want to use Mozilla's [Rhino](https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino/Download_Rhino) (Lighter than the v8 engine), use this [branch](https://github.com/zhkrb/cloudflare-scrape-Android/tree/Rhino-Dependent), is rewrite for [NandanDesai](https://github.com/NandanDesai), thanks for his code submission
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
## issues
If you find any issues, please file a bug after checking for duplicates so I can fix it.
