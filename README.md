![工具](https://img.shields.io/badge/百度AI-Utils-pink.svg)![github](https://img.shields.io/badge/lightingsui-github-cyan.svg)![logo](https://img.shields.io/badge/GitHub-lightingsui-red.svg?style=social&logo=github)

## 封装百度人脸识别客户端

在封装百度人脸识别客户端，实现对人脸以及人脸所对应的用户的增删改查。

### 工具用法

在message.properties配置文件中，配置关于百度应用的信息。配置的内容如下

```properties
# 百度人脸识别
baidu.app_id=百度应用ID
baidu.api_key=应用账号
baidu.secret_key=应用密码
```

新建一个百度应用，即可查看到以上信息

然后调用AipFaceUtil中的getAipFace方法即可获取到一个百度应用客户端。

但是，在FaceRecognitionUtil中，已经获取到了百度应用客户端，只需要使用其中的方法便可以实现对用户以及用户人脸的操作。

### FaceRecognitionUtil中方法的介绍

#### 1、uploadPace

向人脸库中增加一个用户信息（包含人脸）

#### 2、searchFace

传入用户人脸图片的Base64编码，查找评分最高的用户信息并返回

#### 3、deleteFace

删除用户信息（包含人脸），传入的是用户标识

#### 4、changeUserIdentified

修改用户标识信息
