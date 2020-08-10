jdchain快速上手
====
If you have any questions about this project, please contact us。Any question is welcome.  
The group mail: [jdchain-support@jd.com](jdchain-support@jd.com)
 
如果您对快速上手应用有更好的建议，欢迎给我们发邮件。用户用得好才是真的好。  
官网：[http://ledger.jd.com/](http://ledger.jd.com/)  

master与最新的release分支保持一致。其它分支打tag。

#修改说明：

## 12.版本1.2.1.RELEASE说明
1）样例基于JDChain1.2.1.RELEASE版本构建;

## 11.版本1.1.0.RELEASE说明
1）样例基于JDChain1.1.5.RELEASE版本构建;

## 10.版本1.0.9.RELEASE说明
1）样例基于JDChain1.2.0.RELEASE版本构建;

## 9.版本1.0.8.RELEASE说明
1）样例基于JDChain1.1.4.RELEASE版本构建;

## 8.版本1.0.7.RELEASE说明
1）样例基于JDChain1.1.3.RELEASE版本构建;

## 7.版本1.0.6.RELEASE说明
1）样例基于JDChain1.1.2.RELEASE版本构建;  

## 6.版本1.0.5.RELEASE说明
1）样例基于JDChain1.1.1.RELEASE版本构建;  

## 5.版本1.0.4.RELEASE说明
1）样例基于JDChain1.1.0.RELEASE版本构建;  
2）合约构建采用了全新的插件：contract-maven-plugin；  
3）编写合约默认不允许使用系统package：com.jd.blockchain.contract，可改为：com.jd.chain.contract ；  
4）优化了SDKDemo_Constant.java类，只保留一组公私钥；  
5）根据JDChain的代码优化逻辑，将类名KeyGenCommand改为KeyGenUtils；  
6）针对权限验证，添加了权限测试类：PermissionTest.java ；  
7）SDKTest.java测试类进行了完善；  
8）针对动态添加参与方给出了单元测试样例：SDK_GateWay_Participant_Test_.java；  
9）针对ssh兼容测试给出了单元测试样例：SshKeyTest.java；  
10）删除“快速部署JDChain.pdf”文档，改用全新的界面部署的方式；  

## 4.版本1.0.3.RELEASE说明
1）针对956454822@qq.com的问题，在常见问题.md文件中给出说明；  
2）基于jdchain1.0.0.RELEASE版本给出新的使用样例，本版本基于接口来编程，对合约样例进行了重新梳理，并采用main()函数的方式来执行合约测试用例;  
3）更新了doc文件夹中的JDChain-Start快速使用指南.md文档；  
4）清理了原先的readme.txt文档，将其内容合并至"JDChain-Start快速使用指南.md"中；  
5）新增了“快速部署JDChain.pdf”文档，方便开发使用；   
6）添加了ssh-keygen的整合单元测试SshKeyTest.java； 

## 3.版本1.0.2.RELEASE说明
1）新增了常见问题.md文件；  
2）针对洋渝的使用实践，添加了其使用样例，方便用户学习；

## 2.版本1.0.1.RELEASE说明  
1）将代码结构进行优化调整，方便用户测试；  
2）请同步参考doc文档中的快速入门指南进行构建，主要修改内容为3.3.2章节。

## 1.版本1.0.0.RELEASE说明  
1）快速编译合约，采用test方式快速发布和执行；  



   
   
