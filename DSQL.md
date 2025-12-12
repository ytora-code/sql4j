# DSQL

## 1 自定义DSL语法格式

```
select name, age from user
where 1=1
if: name not is empty
	and name like concat('%', ${name}, '%')
if: age ge 20
	and age >= ${age}
if: length(ids) > 0
	and id in (${ids})
```

有参数如下：

```json
{
    "name": "张三",
    "age": 21,
    "ids": ['1', 2, true, 3.0]
}
```

经过语法处理，得到字符串

```sql
select name, age from user where 1=1 and name like concat('%', ?, '%') and age >= 21 and id in (?, ?, ?, ?)
```

经过占位符处理，最后执行SQL：

```sql
select name, age from user where 1=1 and name like concat('%', '张三', '%') and age >= 21 and id in ('1', 2, true, 3.0)
```



### 1.1 if

if关键字，语法`if: bool-expr`

```
if: bool-expr
	if-block
```

通过缩进，判断哪些行属于该if，并根据bool-expr的取值，选择性地渲染if-block

if必须出现在每一行的开头，并且后面必须跟一个空格

示例：

```
你好!
if: name eq '张三'
	张三
if: name eq '李四'
	李四
```

如果参数为`{"name": "张三"}`，那么最后的字符串如下

```
你好
	张三
```



### 1.2 for

for关键字，语法`for: arr`

```
for: arr
	for-block
```

for: 后面必须跟数组或字符串

如果是数组，则对数据元素进行循环；如果是字符串，则以逗号分隔后形成数组，再循环数组

数组长度为多少，for-block就循环多少次，并且for-block里面可以使用`${index}`和`${item}`来获取当前遍历元素的下标和元素本身

for必须出现在每一行的开头，并且后面必须跟一个空格

示例：

```
人员情况如下：
for: persons
	${index} - ${item.name} - ${item.age}
```

如果参数为`{"persons": [{"name": "zs", "age": 22}, {"name": "ls, "age": 12}]}`，那么最后渲染得到的字符串如下

```
人员情况如下：
	0 - 'zs' - 22
	1 - 'ls' - 12
```



### 1.3 expr表达式

if后面跟着的就是一个返回bool类型数据的表达式

表达式格式：`leftValue operator rightValue`

leftValue 是左值，rightValue 是右值，operator 操作符，整个表达式会返回一个bool值

leftValue和rightValue的内容如下：

* 字面量：字符串（单引号或双引号包裹），数字（整数或小数）

* 函数：函数必须返回一个值，最终参与比较的实际就是这个值

* 关键字：`null`，表示空；`empty`，表示空字符串或者空；`true`，表示真；`false`，表示假

* 变量：比如name，来自参数里面的name字段

* 元组：多个值组成的序列，比如：`('zs', 12, null, name, length(ids))`

  元组里面的元素可以是字符串、数值、null、empty、函数，暂时不支持元组嵌套

如何确定值的类型呢？规则如下：

1. 如果值和关键字一致，则认为是关键字
2. 如果值的开头和结尾是`'`或`""`，则认为中间被包裹的部分就是字符串
3. 如果值的开头是`(`, 结尾是`)`，则认为是元组，将中间包裹部分按照逗号分隔形成字符串
4. 如果值的开头和结尾是不对称的引号或者括号，则报错
5. 值的开头和结尾都没有`'`或`""`：
   1. 如果值的内容只有数字和点号`.`，点号没出现在开头且至多出现一次，则是数字
   2. 如果值的内容出现了非数字，且以`(*)`结尾，则认为是函数
   3. 走到这，则认为是变量



`operator`内容如下：

* 比较符号：`==`、 `!=`、`<`、`<=`、 `>`、 `>=`

  判断左值是否等于、不等于、小于、小于等于、大于、大于等于右值

* `is`：表示左值是右值，等价于 = 

* `not is`：在is基础上取反，表示左值不是右值，等价于 != 

* `is not`：等价于not is，更符合语法习惯的写法

* `like`：此时左值和右值必须是字符串，表示右值是左值的子字符串

* `not like`：在like基础上取反

* `in`：此时右值必须是元组，表示左值是右值中的一个

  此时左值会逐一跟右值元组的元素一一匹配，如果有一个匹配上，整个表达式返回true，否则false

* `not in`：在in的基础上取反

* `between`：此时右值必须是长度为`2`的元组，表示左值介于元组的两个元素之间（闭区间）

* `not between`：在between基础上取反

示例：

如果参数为`{"name": "zs", age: 20, sex: ""}`，那么

```
-- 下面表达式为false，因为age=20，20不大于21
age > 21
-- 下面表达式为true
name == 'zs'
-- 下面表达式为false，因为age是数字，数字不等于字符串'20'
age is '20'
-- 下面表达式为false，因为sex是空字符串，并不是null
sex is null
-- 下面表达式为true
sex is empty
-- 下面表达式为true
name like 'z'
-- 下面表达式为true，因为hobby == null（hobby在参数中不存在），而null在右值元组中存在
hobby in (null, empty, 20, "", "zs", length(name))
-- 下面表达式为true，因为age=20，20介于10~30之间
age between (10, 30)
```

注意：null会和null匹配成功，empty会和empty匹配成功，空字符串`""`会和empty匹配成功

如果函数参与匹配，那么实际参与匹配的是函数的返回值，比如`length('zs')`会和2匹配成功



`复合expr`：多个expr可以通过连接符（and、or），或者括号组成一个复合expr

```
(name is 'zs' or age < 12) and sex not is empty
```

上面的符合表达式为false



### 1.4 内置函数

函数：接受一个或多个参数（参数可以是字面量，表达式，或者其他函数），然后返回一个值

函数有内置函数，也可以有自定义函数。内置函数如下：

* length(param)：如果param是数组，返回数组长度；如果param是字符串，返回字符串的字符长度
* trim(strParam)：输入一个字符串，返回该字符串进行trim操作后的字符串
* number(strParam)：输入一个字符串，返回该字符串转换后的数字，比如number('21') => 21，如果参数是'abc'则报错
* string(numberParam)：输入一个数字，返回该数字的字符串形式
* ifnull(param1, param2)：如果param1为null，则返回param2
* ifempty(param1, param2)：如果param1为empty，则返回param2
* if(bool-expr, param1, param2)：如果bool-expr为true，则返回param1， 否则返回param2
* typeof(param)：输入参数，返回参数类型，比如`number`、`string`、`array`、`bool`、`tuple`
* now()：返回当前时间，格式为yyyy-MM-dd HH:mm:ss
* mill()：返回当前的毫秒戳
* id()：返回一个全局唯一的字符串

函数可以用在expr里面作为左值和右值，也可以用在if-block里面和for-block里面



### 1.5 模板占位符

`${value}`，可以用在模板中任何非expr的地方使用

占位符里面的内容可以是字符串、数值、变量、函数，但不能是元组，元组具有歧义，会报错

占位符里面的内容一般是变量或者函数，字符串或数值这样的字面量填入占位符没有意义



示例：

```
if: name not is null
	my name is ${name}
if: age > 0
	and age is ${age}
```

如果参数`{"name": "zs", "age": 22}`，那么解析后得到的字符串如下

```
my name is 'zs' 
and age is 22
```



数组：

如果参数类型是数组，比如`ids: ['1', 2, true, 3.0]`，那么会将数组元素拼接到一起，以逗号分开

```
-- 下面模板解析结果是：my ids is '1',2,true,3.0
my ids is ${ids}
```



默认值：

如果模板占位符里面的值是null，那么该模板占位符不会被渲染到最后的结果里面

模板占位符可以设置默认值,，语法：`${value, defaultValue}`，表示要渲染的值为null时，就使用默认值

```
-- hobby为null，最后解析的结果是：my hobby is '唱','跳','RAP','篮球'
my hobby is ${hobby, ['唱', '跳', 'RAP', '篮球']}
```



### 1.6 预编译占位符

语法：`#{value}`，更符合SQL的占位符写法





## 2 自定义文件类型

DSL语法规则已经有了，现在需要定义一个DSL文件

文件后缀名：`dsql`

内容格式：

```dsql
-- 这是一行注释
------------------------------------------
id -> getById
args -> id: number, name: string, ids: number[], address: org.test.Address
desc -> 这是一个段
type -> select
template ->
select * from user
if: name is not null
	name like ${concat('%', name, '%')}
------------------------------------------
-- 其他段

------------------------------------------
```



上面演示了一个dsql文件的内容，可见，dsql文件以`行`为基本单位

当某行以`-- `，两个横杠+一个空格开头，则改行是`注释行`

如果某行开头的横杠数量超过20个，则改行是`分段行`

两个分段行中间的内容就是一个段落，一个dsql文件可以有任意多个段落

段落格式：`字段 -> 值`

段落中的每一行是`段落行`，段落行分为两部分，键、值，两部分以`->`分隔

有几个内置字段

* id：该段落的id
* desc：该段落描述
* args：供模板使用的参数，仅仅定义了参数名称和类型，真正的参数值需要使用者提供
* type：该段落类型
* template：模板字符串，也就是第一部分里面介绍的那种模板语法
