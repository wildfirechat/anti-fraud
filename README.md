# 野火反诈骗服务
互联网运营的即时通讯服务器时常会有诈骗活动发生，此服务作用是监听到有命中敏感词消息，发送通知给管理员，并提供对话用户提高警惕。客户可以进一步二开此服务，比如对命中的敏感词分门别类的进行提示，用来保护用户免受诈骗危害。

## IM服务
1. 配置IM服务的敏感词命中后回调地址为本服务，当本服务收到IM服务转发过来的命中敏感词的消息后，才会通知管理员并发送警示提醒。
2. 需要在IM服务添加敏感词，只有用户发言命中敏感词后，才会把消息转发给此服务。

## 修改配置
找到配置文件```application.yml```，修改下面配置。
```
# 服务监听端口
server:
  port: 8895

# IM相关配置
im:
  # IM服务管理端口地址
  admin_url: http://192.168.2.5:18080
  # IM服务管理密钥
  admin_secret: 37923
  # 收到敏感词后的提示语
  notification_text: 近期发现多起诈骗案件，包括不限于以下形式：投资、博彩、兼职刷单、模特、卖*、约*、谈感情、冒充客服、冒充公检法等方式骗取钱财。请您注意，在这个软件上谈钱的一定是骗子，请您坚决拒绝金钱来往!!!

# 转发通知管理员或管理团队
forward:
  # 转发目标会话的类型，0是单聊，1是群组
  conversation_type: 1
  # 转发目标会话
  conversation_target: manger_group
```

## 编译
```
mvn package
```

## 运行
在```target```目录找到```anti-fraud-XXXX.jar```，拷贝到具有公网IP的服务器上，然后执行下面语句：
```
java -jar anti-fraud-XXXX.jar
```
>> 在linux机器上，为了防止关掉终端后退出，可以用nohup命令执行，例如 ```nohup java -jar anti-fraud-XXXX.jar &```
