# remote-logcat
Android Library for remote debugging service (remote logcat)

 [![](https://jitpack.io/v/mobile-dev-pro/remote-logcat.svg)](https://jitpack.io/#mobile-dev-pro/remote-logcat)
 
### How to use the library:
   
1. Add it into the root-level build.gradle to the end of repositories:
   
   ```
   	allprojects {
   		repositories {
   			...
   			maven { url 'https://jitpack.io' }
   		}
   	}
   ```
   
2. Add the dependency into the app-level build.gradle:
   
   ```
   	dependencies {
   	        compile 'com.github.mobile-dev-pro:remote-logcat:VERSION'
   	}

3. Init library in the main application class in onCreate() method:
```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RemoteLog.init(this, "token here");
    }
}
```
4. Add user's name or login for logs (if it needed):
```java
RemoteLog.setUserInfo(
      new UserInfoModel([user's name or user's login here])
);
```
5. Send log:
    * debug:
    ```java
    RemoteLog.d("log tag here", "message text here");
    ```
    * error:
    ```java
    RemoteLog.e("log tag here", "error text here");
    ```
6. See logs on web-page http://api.mobile-dev.pro/applog/
7. [Sign-Up](http://api.mobile-dev.pro/applog/auth/signup/30348ec6af2a3fc99282710806c33a86)
