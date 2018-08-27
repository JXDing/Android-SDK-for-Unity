# Unity | 基于Android的自定义SDK构建方案

> 最近在思考一件事：为什么要写文章？如果写的文章没有实质性内容，那这样的文章是没意义的，甚至还有副作用。写文章应该考虑两个方面：一方面是对自己所研究问题的总结归纳；另一方面是帮助他人解决类似问题。好啦，言归正传，进入今天的主题：在Android平台下构建SDK供Unity调用。

文 / 丁建雄

图 / [Berni Beudel](https://500px.me/community/user-details/500px608384l) & 丁建雄

![无往之路](https://upload-images.jianshu.io/upload_images/2413926-8cacfb213ce1f634.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 1 主要目的

用Android Studio设计一套SDK，这套SDK可以在Unity3D环境下调用。在Unity端实现对Android方法的调用，同时，在Android端实现对Unity方法的回调。

## 2 背景知识

SDK是software development kit的缩写，顾名思义，是一套软件开发工具集。SDK最大的好处就是可以对基础的代码进行封装，将很多配置、平台兼容、固定用法的部分打包起来，只专注于提供逻辑方法。这样，顶层应用开发人员就不需要过多地去关注底层的实现，而只需要专注于逻辑的开发，功能的实现。然后，整个软件开发过程就可以严格地流程化，底层开发与应用功能开发互不影响，实现并行开发，这样就大大提高了软件开发的合作效率。

同时，由于SDK的封装特性，在一定程度上可以简化多语言（比如C#，Java等）的交叉引用过程，让多语言交叉编译过程变得简单，你只需要专注于逻辑的调用，至于兼容性和交叉编译问题就不用操心了。（应用层的程序猿都知道，兼容性与配置环境是多么痛苦的一件事。）

Android Studio是基于Java的Android集成开发环境，类似于微软的Visual Studio。

Unity3D简称Unity是一款2D/3D游戏开发引擎，大名鼎鼎的《王者荣耀》、《炉石传说》等是其代表作品。其编程语言主要是C#，同时对于大多数平台Mac OS、Linux、Windows、Android、iOS等都具有很强的兼容性（一套代码逻辑，自由切换目标平台，生成对应平台的应用程序）。

当前网上大部分类似的博客都是互相抄袭，而且根本走不通，会出现各种error。我不清楚对于低版本的开发环境，是否能走得通，但是在笔者的开发环境下，是走不通的。因此，本文在经过多次失败与成功的实践之后，给出了一种行之有效的SDK构建方案。

笔者采用的开发环境：Android Studio 3.1.4，Unity 2018.1.1f1。

## 3 开发过程

整个开发过程涉及两个开发平台的联调，因此，本文就两个平台下的开发过程分别阐述。

### 3.1 Android Studio开发SDK

在Android平台下主要涉及工程的创建、Library模块的创建、多余项目删除、配置文件、三方库导入、Android代码编写等步骤。

#### 3.1.1 创建工程

- File-> New->New Project，由于我们导出的是Library，因此应用名称随意填，不过要注意：生成的Package name总是小写的，笔者这里采用默认的应用名My Application。

- 点击Next，API等级选择3.0以上版本，笔者选择的是7.0。接着一路默认Next直到Finish。这一步完成，你的视图应该是这样子的：

![1](https://upload-images.jianshu.io/upload_images/2413926-e4cb275b7a28a0d7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 3.1.2 创建Library模块

- 切换到Project视图下

![2](https://upload-images.jianshu.io/upload_images/2413926-68b207d195925750.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 新建模块 在MyApplication文件夹右击-> New-> Module

- 选择Android Library

- 命名Library name注意，由于Package name都是小写，因此最好全部小写以求统一，这样不会出错。这里命名为sdklib，最小SDK选择3.0以上版本，笔者这里选择7.0版本。

![3](https://upload-images.jianshu.io/upload_images/2413926-006809de2ddd4390.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 点击Finish完成构建，这个时候，新加进来的lib会自动build，等待build完成，如下图。*如果出现build卡住的情况，可能是由于某些联网下载链接无法连接（在墙外），这个时候，可以在build的log中找到对应的下载链接，直接去网页下载（前提是你有VPN能够翻墙）。下载好之后直接双击打开，程序包会自动解压到对应位置。然后，重新build一下就能通过了。*

![4](https://upload-images.jianshu.io/upload_images/2413926-302e78b83a9f5a70.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 这步完成后，你的视图应该是这样的：

![5](https://upload-images.jianshu.io/upload_images/2413926-e9f70aee193c3ccf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 接下来，创建Library的基础类（activity），创建层次如下图：

![6](https://upload-images.jianshu.io/upload_images/2413926-afa18ae5fa08e99e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- Activity Name设置为MainClass，把勾选的Layout File去掉（由于我们只需要SDK的方法，并不需要布局信息）

![7](https://upload-images.jianshu.io/upload_images/2413926-0b5eb92549a700fa.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 3.1.3 删除冗余项

由于我们构建了完整的项目，但是，对于导出SDK而言，并不需要APP项。同时，自动创建的Library中，测试项也是不需要的。因此，需要依次删除这些冗余项。

- 删除app项目，File-> Project Structure，选中app项目，然后选择左上角 “-” 删除app项目，点击OK结束。稍等一会儿，程序会重新编译，若出现error先不用管。

![8](https://upload-images.jianshu.io/upload_images/2413926-f6b6e638d943ae80.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 删除app文件夹，app文件夹右击delete即可。

![9](https://upload-images.jianshu.io/upload_images/2413926-718d8f3b98495455.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 删除多余的Test文件，将src文件夹下的两个Test文件夹删除。

![10](https://upload-images.jianshu.io/upload_images/2413926-3ced59aa1110757b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 这时，你的项目层次应该入下图所示：

![11](https://upload-images.jianshu.io/upload_images/2413926-078e3ff5ab7e6b4e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 3.1.4 配置文件

很多博客里面都强调要在AndroidManifest.xml里面添加各种语句，实际上呢？亲测，根本不需要！请注意，我们设计的是SDK，并不是一个完整的APP。

- 打开sdklib的AndroidManifest.xml，如下：

![12](https://upload-images.jianshu.io/upload_images/2413926-c50dee750e139f87.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 如果你是严格按照我的步骤来操作的，那么现在AndroidManifest.xml下面的内容应该是这样的：

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.sunny.sdklib">

    <application>
        <activity android:name=".MainClass"></activity>
    </application>

</manifest>
```

- 非常简洁，而且不用改动任何内容。当然，如果你需要一些定制化的权限或者改动默认属性，可以自行添加。

#### 3.1.5 Unity库导入

我们希望用Android制作的SDK不仅仅能在Unity中被调用，同时，在Android生成的SDK代码中，也可以调用Unity库中的方法，从而实现交叉引用的目的。因此，引入Unity依赖库是必要的。

- 引入Unity的classes.jar包，该文件在C:\Program Files\Unity\Editor\Data\PlaybackEngines\AndroidPlayer\Variations\mono\Release\Classes目录下。笔者是64位Windows平台，其余平台下大家可以参照此路径稍作调整。复制classes.jar粘贴到lib文件夹下。

![13](https://upload-images.jianshu.io/upload_images/2413926-a07467a3b0402d45.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 导入依赖库，File-> Project Structure-> Dependencies-> "+"，添加“Jar dependency”选择“classes.jar”，如下图所示，点击OK，会自动build，稍等一下。

![14](https://upload-images.jianshu.io/upload_images/2413926-53084ebc38cc4222.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 如果有其余的第三方SDK的jar包，以同样的方式引入。如果第三方中还有.so文件，先不需要导入。至此，项目依赖添加已基本完成。

#### 3.1.6 Android代码

```java
package com.sunny.sdklib;

import android.app.Fragment;
import android.os.Bundle;
import com.unity3d.player.UnityPlayer;

public class MainClass extends Fragment {

    private static final String TAG = "Main";
    private static MainClass Instance = null;
    private String gameObjectName;

    public static MainClass GetInstance(String gameObjectName){
        if (Instance == null){
            Instance = new MainClass();
            Instance.gameObjectName = gameObjectName;
            UnityPlayer.currentActivity.getFragmentManager().beginTransaction().add(Instance, TAG).commit();
        }
        return Instance;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);  // This is important to avoid loss of instance when rotating screen.
    }
    // Example1:CallBack function(call unity methods in Android)
    public void HelloWorld()
    {
        UnityPlayer.UnitySendMessage(gameObjectName,"PluginCallBack","Hello World!");
    }
    // Example2:Android method called in Unity
    public int AndroidAdd(int a, int b)
    {
        return a + b;
    }
}

```

- 几个注意事项：
  1. 很多博客都说要继承UnityPlayerActivity，即使是官方文档也给出了类似的办法，但是这种方法是有风险的，具体原因后面参考资料里面会详细给出。因此，这里继承的是Fragment类。
  2. 这里采用GetInstance方法创建MainClass的单例，相当于初始化唯一的对象。
  3. 设置了两个自定义函数，分别展示的是Unity调用Android和Android调用Unity的双向数据传输操作。

- 将SDK编译导出，IDE右侧Gradle窗口，分别双击assemblyDebug和assemblyRelease，编译出SDK的debug和release版本，如下：

![15](https://upload-images.jianshu.io/upload_images/2413926-7c195ed2074879c6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 如果提示“使用或覆盖了已过时的API”，不用理会，不会出问题。

- 在如下所示的目录中，导出sdklib-debug.aar与sdklib-release.aar（两个版本都可以，不过debug主要调试用，正式发布的时候还是要release版本，具体细节大家可以百度。）

![16](https://upload-images.jianshu.io/upload_images/2413926-681ffa5a0982cb4b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 删除重复的class.aar。将两个aar文件复制到桌面上，用压缩软件打开，内部目录如下。将libs\classes.jar删除（由于安装的时候Unity会再次拷贝classes.jar到项目中从而造成冲突）：

![17](https://upload-images.jianshu.io/upload_images/2413926-d73219ebd389f5d0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 添加三方.so依赖。如果有第三方.so文件，请拷贝至libs目录下。

- 保存修改好的两个aar文件，至此，Android端SDK设计全部完成。

### 3.2 Unity调用SDK

在Unity平台下主要涉及工程创建、SDK导入、脚本编写等。Unity创建基本的工程就不介绍了，比较简单，着重介绍后面两个部分。

#### 3.2.1 SDK导入

- 将debug版本拷贝至Unity工程的Plugins\Android目录下（此目录需要手动创建）。

![18](https://upload-images.jianshu.io/upload_images/2413926-5e90823f100e34fb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 3.2.2 脚本编写

```c++
using UnityEngine;
using UnityEngine.UI;

public class AndroidTest : MonoBehaviour
{
    public Text callbackText = null;
    public Text resultText = null;

    private string className = "com.sunny.sdklib.MainClass"; 
    private AndroidJavaObject pluginObject = null;

    void Start()
    {
#if UNITY_ANDROID && !UNITY_EDITOR
        pluginObject = new AndroidJavaClass(className).CallStatic<AndroidJavaObject>("GetInstance", gameObject.name);
        pluginObject.Call("HelloWorld");
        resultText.text = pluginObject.Call<int>("AndroidAdd", 1, 1).ToString();
#endif
    }

    public void PluginCallBack(string text)
    {
        callbackText.text = text;
    }
}

```

- 在Start中，我们调用了SDK中的HelloWord()和AndroidAdd()两个方法，并且将这两个方法的返回值显示到屏幕的文本中。也就是说，验证SDK是否调用成功，只需要查看屏幕上的文本输出即可。

- 请注意，"#if - #endif" 中包含的部分只能在真机上执行，因此在调试界面是不会显示结果的。

- 真机调试结果如下：

![19](https://upload-images.jianshu.io/upload_images/2413926-793bf9a3bfdf4d1d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 至此，基于Android Studio构建SDK与在Unity调试过程全部完成。

## 4 De-Bug环节

从这次的博客开始，尝试设置De-Bug环节，用于集中解决可能遇到的问题，同时将笔者认为比较重要的知识点和对一些问题的思考一并记录下来。

4.1 真机调试日志输出
由于封装好的SDK只能在真机上起作用，因此，很多log信息没办法实时获取。这个时候，可以在开发者模式下，在终端中使用adb logcat -s Unity语句来实时获取Unity输出的日志信息。使用这种方法，我们就可以在真机调试时，实时得到log信息，进而快速定位和解决问题。

4.2 单例方案
对于SDK方法的调用，可以通过面向对象的方法，采用单例化方案。过程如下：

- 在SDK中进行类方法封装。
- 设置单例化方法GetInstance，唯一化对象。
- 在Unity中生成单例对象。
- 在单例中访问类的各项资源。
- 如此便可以实现SDK资源与Unity资源的通信。

4.3 多SDK调用方案
如果都采用继承UnityPlayerActivity的方法，由于默认这个活动是入口，那么初始化的时候会出现问题（具体问题分析见参考文献）；但是，如果继承Fragment，那么只有当对应的单例生成的时候，才可以调用SDK对应类中的方法，这样，在逻辑管理上更加规范，也可以减少各种莫名其妙error的发生。

4.4 其他error
如果你严格采用我的方法，遇到了本文还不能解决的问题，请在简书或微信上私信联系我；如果你采用其他方法，遇到了各种莫名其妙的error，请不要迟疑，放弃那个方法，采用我的方案仔细走一遍，你定能发现之前存在的问题。

## 5 参考文献

特别感谢博主[逝水追风](https://blog.csdn.net/zhangdi2017/article/details/65629589)，笔者查阅了国内外大量的博客，都走不通，偶然间尝试了他的方法，一次就走通了。

- Unity调用Java方法[官方文档](https://docs.unity3d.com/Manual/AndroidUnityPlayerActivity.html)

- Android Fragment[官方文档](https://developer.android.google.cn/guide/components/fragments?hl=zh-cn)

- 在SDK中继承Fragment而不继承UnityPlayerActivity的原因[匈牙利大神eppz博客](http://eppz.eu/blog/unity-android-plugin-tutorial-1/)

---
END!
