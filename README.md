# Android开发项目仿快手--拍客(Pike)
######注：项目仍处于开发阶段，本项目仅供学习使用，未经本人授权，请勿进行商业用途
***
    一款基于MVP(Model-View-Presenter)设计模式开发的Adroid APP项目
***
#### 项目简介
      主要是提供一个短视频社区,大家可以分享生活中的趣事 , 市面上所有的短视频App全部都是主打优质内容，用户
    主要充当观众的角色，观看其中的内容。
      拍客就从主打拍摄切入，以优质的MV效果，让用户参与到创作中来，让用户在搞笑片客里成为主角,这同时也激发
    了用户自身的对于APP的传播 ! 
***
####项目截图


<img src="/screenshots/pager1.jpg" alt="screenshot" title="screenshot" width="270" height="486" />
<img src="/screenshots/pager2.jpg" alt="screenshot" title="screenshot" width="270" height="486" />
<img src="/screenshots/pager3.jpg" alt="screenshot" title="screenshot" width="270" height="486" />
<img src="/screenshots/pager4.jpg" alt="screenshot" title="screenshot" width="270" height="486" />



#### 基本功能
- 相机录制方面使用第三方相机框架,强制使用横屏录制

- 添加第三方登陆功能(QQ登陆)、微信分享(分享朋友圈和分享好友列表功能)

- 2016/11/3 添加登陆、注册、主页显示、自定义相机以及短视频(默认10s)的录制和保存功能
***
#### 主要技术点
***
#### 技术难点与解决方案
- 自定义相机出现 at android.media.MediaRecorder.start(Native Method)以及预览被旋转90度
      解决方案：使用第三方解决
      
- 视频录制结果旋转了90度
      解决方案：添加代码 mRecorder.setOrientationHint(90);

- 视频更新成功，需要实现进度条问题(解决)
     解决方案:使用后台Service进行上传视频操作,减少在主页面进行的耗时操作
     
- 自定义相机跳转到其他页面后，返回出现黑屏,无法预览   
     解决方案:使用第三方相机框架
*** 
###项目中使用的主要技术及框架
- 框架
      1. ButterKnife  注解绑定获取控件功能

      2. Picasso   网络和本地图片的加载功能
       
      3. okhttp   网络连接功能
      
      4.LandscapeVideoCamera 横屏相机
      
- 技术：
      1. RxJava
      2. RxAndroid
      3. MVP设计模式
      4. DataBinding
      5. 约束布局

##License

Copyright 2016 [mmengchen](https://github.com/mmengchen "mmengchen")

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.