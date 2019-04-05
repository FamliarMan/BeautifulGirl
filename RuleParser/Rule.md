# 网站提取规则学习
## 1. Html元素提取规则
### 1.1 通过类名查找
```
@class:<name>    查找所有class为name的元素
@class:<name>[0] 查找class为name的第一个元素 （里面的数字可以随意填）
@class:regex<n[.]me>  查找满足该正则的class元素
@class:regex<n[.]me>[0] 查找满足该正则的class元素的第一个
```
### 1.2 通过id查找
```
@id:<content>  查找id为content的元素
@id:regex<n[.]me>  查找id满足该正则的所有元素
@id:regex<n[.]me>[0] 查找id满足该正则的第一个元素
```
### 1.3  通过标签查找
```
@label:<a>  查找标签为a的所有元素
@label:<a>[0]  查找标签为a的第一个元素（里面的数字可以随意填）
```

### 1.4 获取属性
```
@property:<href>  元素的href属性的值，此时提取到的是文本
```
### 1.5. 获取文本内容
```
@text 获取第一个p元素的文本内容
```
## 2. 任意文本提取 
### 2.1  通过正则匹配
```
@regex:<(n([.])me)>[1] 查找满足该正则的第一个文本，并提取第一个括号中的内容
@regexAll:<(n([.])me)>[1] 查找满足该正则的所有文本，并提取第一个括号中的内容
```

## 3. 组合使用
```
@class:<name> -> @id:<content> 查找所有class为name的元素下id为content的元素 
@class:<name> -> @class:<content>[0]  所有class为name的元素下第一个class为content的元素，累加起来返回一个列表
```

## 4. 元素过滤规则
具体规则如下：
```
* @hasClass:<className1,className2>  上个结果节点有class为className1或className2的子节点的保留
* @noClass:<className1,className2> 上个结果节点不包含这个子class节点的保留
* @hasId:<id1,id2> 包含所有id子节点保留
* @noId:<id1,id2> 上个结果节点不包含所有这些子节点的保留
* @hasLabel:<label1,label2> 上个结果节点包含所有这些标签子节点的保留
* @noLabel:<label1,label2> 上个结果节点不包含所有这些标签子节点的保留
* @hasText:<text1,text2> 上个元素集合中text属性含有这些text的保留
* @noText:<text1,text2> 上个元素集合中text属性含有这些text任意一个的都不保留
* @==:<value1,value2,value3>  上个结果字符串等于这些value的任意一个保留
* @!=:<value1,value2,value3> 上个结果字符串不等于全部这些value的保留
* @containText:<text1,text2> 上个结果中有文本包含任意一个text的保留
*

比如：
@label:<h>->@fileter:<@hasClass(content)>  找出所有不包含content节点的h节点

```



## 5. Json提取
```
@jsonArr:<keyName> 提取key为keyName的数组
@jsonObj:<keyName> 提取key为keyName的对象
@jsonValue:<keyName>提取key为keyName的某个属性

组合使用
@jsonArr<keyName> -> @jsonObj:<keyName2> -> @jsonValue:<name>
```

