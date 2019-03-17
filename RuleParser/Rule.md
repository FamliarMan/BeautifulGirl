# 选择元素规则设计
## 1. 通过类名查找
```
@class:<name>    查找所有class为name的元素
@class:<name>[0] 查找class为name的第一个元素 （里面的数字可以随意填）
@class:regex<n[.]me>  查找满足该正则的class元素
@class:regex<n[.]me>[0] 查找满足该正则的class元素的第一个
```
## 2. 通过id查找
```
@id:<content>  查找id为content的元素
@id:regex<n[.]me>  查找id满足该正则的所有元素
@id:regex<n[.]me>[0] 查找id满足该正则的第一个元素
```
## 3. 通过标签查找
```
@label:<a>  查找标签为a的所有元素
@label:<a>[0]  查找标签为a的第一个元素（里面的数字可以随意填）
```

## 4. 获取属性
```
@id:<content> -> @property:<href> 获取id为content元素的href属性的值
```
## 5. 获取文本内容
```
@label<p>[0] -> @text 获取第一个p元素的文本内容
```
## 4. 通过正则匹配
```
@regex:<n[.]me>  查找满足该正则的所有文本
@regex:<(n([.])me)>[1] 查找满足该正则文本，并提取第一个括号中的内容
```
## 5. 组合使用
```
@class:<name> -> @id:<content> 查找所有class为name的元素下id为content的元素 
@class:<name> -> @class:<content>[0]  所有class为name的元素下第一个class为content的元素，累加起来返回一个列表
```
## 注意事项
由于"<",">"符号被用作了特殊括号，如果某个属性里面有相应字符需要写成"\<","\>"