# 一、需求分析
1、日记列表显示
2、日记分类
3、个人中心--头像和标语
4、日记增删改

数据库3张表：
user
diary
diaryType
**注意：主外键关联**

# 二、登陆界面实现
首先需要登陆必须与数据库用户表进行数据通信，在数据库用户表中存入用户名和密码模拟注册。

建立util包，建立数据库工具类--连接数据库--关闭数据库。

建立dao包，建立UserDao.java与数据库通信--查询数据库登陆。
rs.next()为true则表示查到数据

建立web包，建立LoginServlet.java处理登陆请求。
servlet类需要继承httpServlet类，重写doPost和doGet方法，doGet调用doPost。
由于是请求，前端将用户名和密码发过来，这里获取下然后进行数据库查询，若存在则将用户对象存入session然后【客户端跳转mainTemp.jsp】，不存在则将错误用户名和密码及错误信息存入req然后【服务器跳转login】，此时login可以从req中获取刚才填入的信息。

引入Bootstrap
下载Bootstrap包放在WebContent目录下，在bootstrap/js下添加jQuery.js
jsp页面添加引用信息

web.xml修改首页为login.jsp

bootstrap流式栈布局包含12个栅格

login.jsp表单提交，action为login，故在web.xml配置servlet：
```jsp
<servlet>
  	<servlet-name>LoginServlet</servlet-name>
  	<servlet-class>com.java.web.LoginServlet</servlet-class>
  </servlet>
  <servlet-mapping>
  	<servlet-name>LoginServlet</servlet-name>
  	<url-pattern>/login</url-pattern>
  </servlet-mapping>
  ```

  **提交时候验证：onsubmit="return checkForm()"**
  checkForm()为js函数。onsubmit为false，则不提交表单。
  ```jsp
  <script type="text/javascript">
	function checkForm(){
		var userName=document.getElementById("userName").value;
		var password=document.getElementById("password").value;
		if(userName==null||userName==""){
			document.getElementById("error").innerHTML="用户名不能为空";
			return false;
		}
		if(password==null||password==""){
			document.getElementById("error").innerHTML="密码不能为空";
			return false;
		}
		return true;
	}
</script>
```

优化：采用properties存放系统配置项，容易维护
properties文件采用键值对形式存储数据：
```txt
url=jdbc:mysql://localhost:3306/db_blog?useSSL=false
driver=com.mysql.jdbc.Driver
userName=root
userPassword=123456
```
建立PropertiesUtil工具类--传入参数名，获得参数值
```java
public static String getValue(String key) {
    Properties pro=new Properties();
    InputStream in=new PropertiesUtil().getClass().getResourceAsStream("/blog.properties");//以src为根
    try {
        pro.load(in);
    } catch (IOException e) {
        e.printStackTrace();
    }
    return pro.getProperty(key);
}
```

优化：采用MD5加密密码
创建MD5Util工具类--传入字符串，获得加密字符串
```java
public static String MD5Encode(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    MessageDigest md5= MessageDigest.getInstance("md5");
    //jdk1.8
    //java.util.Base64.getEncoder().encodeToString("进制)//编码
    //java.util.Base64.getDecoder().decode(字符串内容）//解码
    return Base64.getEncoder().encodeToString((md5.digest(str.getBytes("utf-8"))));
}
```

记住用户名和密码
当登陆页面login.jsp上的记住勾选，则在登陆成功时需要记住用户名和密码到cookie中。
```java
private void rememberToCookie(String userName, String password, HttpServletResponse resp) {
    Cookie user=new Cookie("user", userName+"-"+password);
    user.setMaxAge(1*60*60*24*7);
    resp.addCookie(user);
}
```
**cookie单位是秒**
记住了密码后下次登陆需要显示出来，而当用户输入错误用户名和密码时不需要显示cookie
```jsp
<%
	if(request.getAttribute("user")==null){
		String userName=null;
		String password=null;
		
		Cookie[] cookies=request.getCookies();
		for(int i=0;cookies!=null&&i<cookies.length;i++){
			if("user".equals(cookies[i].getName())){
				userName=cookies[i].getValue().split("-")[0];
				password=cookies[i].getValue().split("-")[1];
			}
		}
		if(userName==null){
			userName="";
		}
		if(password==null){
			password="";
		}
		pageContext.setAttribute("user", new User(userName,password));
	}
%>
```
**注意：需要import="com.java.model.User，因为在java中需要访问User**

# 三、主界面搭建及用户请求过滤
###主界面搭建
引入自己的css：
```
<link href="${pageContext.request.contextPath}/style/blog.css" rel="stylesheet">
```
**注意：css文件不需要头信息，直接写样式即可**

导航：采用流式栈，左9右3

在前台直接定义好框架，获取req的变量来填充，使用
```jsp
<jsp:include page="${mainDiary }"></jsp:include>
```
来动态获取值,先
*login.jsp-login-loginservlet-main-mainservlet-mainTemp.jsp*
在mainservlet中，将查询好的diaryList放入req，diaryList.jsp从req获取，然后mainTemp.jsp中取diaryList.jsp，这样完成动态获取。

###请求过滤
拦截器Filter--httpservlet下的Filter
web.xml配置Filter：
```
<filter>
  	<filter-name>loginFilter</filter-name>
  	<filter-class>com.java.filter.LoginFilter</filter-class>
  </filter>
  <filter-mapping>
  	<filter-name>loginFilter</filter-name>
  	<url-pattern>/*</url-pattern>
  </filter-mapping>
  ```
重写方法doFilter，转换下req，resp
```
HttpServletRequest req=(HttpServletRequest)sreq;
HttpServletResponse resp=(HttpServletResponse)sresp;
HttpSession session=req.getSession();
```
判断流程：若当前session无用户，则返回login.jsp,注意排除掉login.jsp，因为这个界面无需过滤：
```java
if(obj==null&&path.indexOf("login")<0) {//若此时请求mainTemp.jsp会出错
    resp.sendRedirect("login.jsp");
}else {
    filter.doFilter(sreq, sresp);
}
```
可以调试使用：String path=req.getServletPath();来查看请求的地址

# 四、日记列表显示和分页实现
先导入数据库数据，来模拟已建好的日记和类别

###日记列表显示
由于需要引入日记，这里建立一个日记model和日记dao--查询日记列表
查询函数：
--为了方便扩展使用【拼接字符串】方式构建查询语句
--为了方便显示日记类别等使用【连接查询】连接两张表查询
--由于数据库date类别和java的util里date不同，所以不能直接setReleaseDate，这里采用一个DateUtil工具类进行转换，在数据库获取时获取date字符串形式，通过工具转换成util date类型赋值。
```java
public List<Diary> diaryList(Connection con)throws Exception{
	List<Diary> diaryList=new ArrayList<Diary>();
	StringBuffer sb=new StringBuffer("select * from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId ");
	sb.append(" order by t1.releaseDate desc");
	PreparedStatement pstmt=con.prepareStatement(sb.toString());
	ResultSet rs=pstmt.executeQuery();
	while(rs.next()){
		Diary diary=new Diary();
		diary.setDiaryId(rs.getInt("diaryId"));
		diary.setTitle(rs.getString("title"));
		diary.setContent(rs.getString("content"));
		diary.setReleaseDate(DateUtil.formatString(rs.getString("releaseDate"), "yyyy-MM-dd HH:mm:ss"));
		diaryList.add(diary);
	}
	return diaryList;
}
```

引用上面流程：*login.jsp-login-loginservlet-main-mainservlet-mainTemp.jsp*
mainservlet负责mainTemp.jsp页面的显示后台处理，在这里查询获取日记列表，并存入req中。

diaryList.jsp中构建前台日记列表显示：使用bootstrap的列表组建，采用【c标签和fmt标签】，前者负责遍历diaryList列表值，后者负责日期格式化操作。
```jsp
<div class="diary_datas">
	<ul>
		<c:forEach var="diary" items="${diaryList }">
			<li>『<fmt:formatDate value="${diary.releaseDate }" type="date" pattern="yyyy-MM-dd"/>』<span>&nbsp;<a href="#">${diary.title }</a></span></li>
		</c:forEach>
	</ul>
</div>
```

###分页
采用bootstrap分页组件来构建前台代码，分页中的每页项由后台构建
```jsp
<div class="pagination pagination-centered">
	<ul>
		${pageCode }
	</ul>
</div>
```
需要分页，则必须知道当前日记列表能分成多少页，这依赖于每页显示的列表数，在查询过程中，可以查询出所有列表，然后利用【limit】限制查询结果，结合当前页，决定显示的是哪些数据
起始数据=(当前页-1)*每页大小
查询长度=每页大小

修改diaryList函数，传入所需参数：如起始数据和查询长度，返回一页数据
修改MainServlet.java，调用日记列表查询函数时给所需要的值，可以将每页大小存到配置项中
MainServlet.java新增分页项输出函数，传入--当前页--总页数--每页大小
总页数可计算：int totalPage=totalNum%pageSize==0?totalNum/pageSize:totalNum/pageSize+1;
开始拼字符串：
```jsp
StringBuffer pageCode=new StringBuffer();
pageCode.append("<li><a href='main?page=1'>首页</a></li>");
if(currentPage==1){
	pageCode.append("<li class='disabled'><a href='#'>上一页</a></li>");
}else{
	pageCode.append("<li><a href='main?page="+(currentPage-1)+"'>上一页</a></li>");
}
for(int i=currentPage-2;i<=currentPage+2;i++){
	if(i<1||i>totalPage){
		continue;
	}
	if(i==currentPage){
		pageCode.append("<li class='active'><a href='#'>"+i+"</a></li>");
	}else{
		pageCode.append("<li><a href='main?page="+i+"'>"+i+"</a></li>");
	}
}
if(currentPage==totalPage){
	pageCode.append("<li class='disabled'><a href='#'>下一页</a></li>");
}else{
	pageCode.append("<li><a href='main?page="+(currentPage+1)+"'>下一页</a></li>");
}
pageCode.append("<li><a href='main?page="+totalPage+"'>尾页</a></li>");
return pageCode.toString();
```

#五、日记归类
###日记类别归类
新建日记类别model，根据需求需要显示日记类别下的数量，所以添加第三属性为日记数量
新建日记类别dao--返回日记类别列表
**注意：此处不能使用内连接查询，因为当类别下无日记数据时，该类别不会显示，采用【外连接查询】**
采用右连接查询：right join：
SELECT typeName,typeId,COUNT(typeId) FROM t_diary RIGHT JOIN t_diarytype ON t_diary.`typeId`=t_diarytype.`diaryTypeId` GROUP BY typeName

MainServlet.java中将查询到的结果（日记类别和该类数量）放入session中，可以跨模块显示
在前台mainTemp.jspt中的日记类别栏进行c标签遍历输出查询结果

###日记日期归类
mysql日期格式化函数：DATA_FORMAT(xxx,'%Y年%m月')，可以将xxx日期类型转换为后面的格式，降序DESC
```java
String sql="SELECT DATE_FORMAT(releaseDate,'%Y年%m月') as releaseDateStr ,COUNT(*) AS diaryCount  FROM t_diary GROUP BY DATE_FORMAT(releaseDate,'%Y年%m月') ORDER BY DATE_FORMAT(releaseDate,'%Y年%m月') DESC;";
```
DiaryDao.java新建查询日记日期和数量查询函数

MainServlet.java中查询出日记日期和数量，放入session中
在前台mainTemp.jsp中的日记类别栏进行c标签遍历输出查询结果

#六、日志主页完成
###个人信息功能
将数据库user表信息完善，存入昵称、头像文件名、标语
建立头像文件夹，存放头像
增加头像文件地址配置参数

修改mainTemp.jsp，从currentUser中获取个人数据

修改UserDao.java，将后三者在登陆函数查询到用户时，一同存入user返回，而LoginServlet.java在登陆时调用了该函数并将结果存入currentUser变量中

###右侧栏分类点击，日记列表改变
在右侧c标签遍历中，加入a标签的链接：
日志类别：
```
<li><span><a href="main?s_typeId=${diaryTypeCount.diaryTypeId }">${diaryTypeCount.typeName }(${diaryTypeCount.diaryCount })</a></span></li>
```
日志日期：
```
<li><span><a href="main?s_releaseDateStr=${diaryCount.releaseDateStr }">${diaryCount.releaseDateStr }(${diaryCount.diaryCount })</a></span></li>
```
引用上面流程：*login.jsp-login-loginservlet-main-mainservlet-mainTemp.jsp*
带的参数会在点击mainTemp.jsp链接时请求main，即跳到mainservlet中，可以在其中获取
修改mainservlet，在其中获取上面设置的两个值，若非空，则将其添加到一个diary实例中，并存入session中（方便翻页组建查看页数等）
修改diarydao，获取日记列表函数、获取日记数量函数加入diary输入，用来传入查询条件
--获取日记列表函数：判断传入diary的id或者releasedate是否为空，非空说明存入了值，拼接查询语句：and...
**注意：当两个分类都点击过，session中保存两个查询条件，此时需要采用互斥，清除另外一个条件。**
**注意：通过url传递的字符串参数需要重新编码：s_releaseDateStr=new String(s_releaseDateStr.getBytes("ISO-8859-1"),"UTF-8");**

###搜索
当使用搜索时，默认查询所有符合条件的项，需要清除右侧session中的条件。
点击查询按钮，跳转main?all=true，带入参数all，且提交表单，获取表单输入文本框的信息，直接String s_title=request.getParameter("s_title");获取文本内容
当点击提交表单在mainservlet.java判断：
```java
if("true".equals(all)){
	if(StringUtil.isNotEmpty(s_title)){
		diary.setTitle(s_title);
	}
	session.removeAttribute("s_releaseDateStr");
	session.removeAttribute("s_typeId");
	session.setAttribute("s_title", s_title);
}
```
若表单内容不为空，则模糊查询，加入到diarydao.java中的日记列表显示函数中：
```java
if(StringUtil.isNotEmpty(s_diary.getTitle())){
	sb.append(" and t1.title like '%"+s_diary.getTitle()+"%'");
}
```
此时点搜索框已经可以查出数据，但是点击分页依然有问题，原因是分页组件请求时，查询到的total依然为总日记数，需要在【diarydao.java.diarycount函数中也加入模糊查询】，返回当前查询结果的总数。在mainservlet.java中判断语句加上：
```java
if(StringUtil.isEmpty(s_title)){
	Object o=session.getAttribute("s_title");
	if(o!=null){
		diary.setTitle((String)o);
	}
}
```
确保分页组件请求时能获取到s_title变量
完成搜索功能。