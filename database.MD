## sqlite表结构设计
RuleInfo表

| 字段 | 类型 | 备注  |
|:---|:---|:----|
| id | Integer |主键|
|enabled| Integer| 是否激活，0不使用，1使用|
|rule|Text|具体规则json串|


