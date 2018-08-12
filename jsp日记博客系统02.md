# 一、日志信息显示
改进：导航栏主页功能实现，在主页链接中加上：href="main?all=true"即可
要实现日志信息显示，需要查询日志信息
改进diarydao.java，新增查询日志内容函数
查询语句为：String sql="select * from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId and t1.diaryId=?";

在日记显示页，需要显示的有：日记名、日记类别、日记日期、日记内容
在diary的model中新增日记类别属性

新增diaryservlet.java，用来处理前台点击日记标题跳转，由于后面会加入其他操作，这里采用判断【action参数内容】来选择执行的操作。
```
<a href="diary?action=show&diaryId=${diary.diaryId}">
```
web.xml中配置diary的servlet

当前台传来show时，获取diaryId，查询数据库，返回diary对象，将diary放到req中，然后将mainPage的页面换成show的：
```java
Diary diary=diaryDao.diaryShow(con, diaryId);
    request.setAttribute("diary", diary);
    request.setAttribute("mainPage", "diary/diaryShow.jsp");
    request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
```
在diaryShow.jsp中完善前台界面：发布时间：${获取}，日志类别：${获取}
```
<div class="diary_info">
    发布时间：『 <fmt:formatDate value="${diary.releaseDate }" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>』&nbsp;&nbsp;日志类别：${diary.typeName}
</div>
```
加入日志内容，并加入样式
加入修改|返回|删除三个按钮

# 二、日志信息添加
由于添加日志和修改日志公用同一个页面，所以这里需要先用servlet处理，并传递修改还是添加操作：
```html
<li class="active"><a href="diary?action=preSave"><i class="icon-pencil"></i>&nbsp;写日记</a></li></a></li>
```
**注意：若是添加，则内容为空，若是修改，则要把原来日志内容放入**
需要添加三条信息到diary：标题、类别、内容
添加diarydao.diaryAdd函数，新增添加日志函数，返回影响条数

diaryservlet.java中，新增【判断参数】，若为preSave则，则给mainPage参数放入diary/dairySave.jsp页面，并【服务器转发】mainTemp.jsp页面。

diarySave.jsp页面：
页面需要用到在线文本编辑器，这里使用【ckeditor】进行文本编辑。
ckeditor需要下载包，然后放入【webcontent/js】文件目录下
ckeditor在jsp中使用需要引入：引入内容为ckeditor.js
ckeditor作用在【textarea】标签上：<textarea class="ckeditor">
```jsp
<div>
    <textarea class="ckeditor" id="content" name="content">${diary.content }</textarea>
</div>
```

添加下拉框：日志类别信息在session的diaryTypeCountList变量中有，这里直接使用。
```jsp
<div class="diary_type">
    <select id="typeId" name="typeId">
        <option value="">请选择日志类别...</option>
        <c:forEach var="diaryTypeCount" items="${diaryTypeCountList }">
            <option value="${diaryTypeCount.diaryTypeId }">${diaryTypeCount.typeName }</option>
        </c:forEach>
    </select>
</div>
```
在日记表单提交时，添加：【onsubmit="return checkForm()"】表单验证
获取title
获取ckeditor的content=CKEDITOR.instances.content.getData();
获取typeId
判断三者是否合法，若合法则返回true

表单提交的action="diary?action=save"
在diaryservlet中，新增【判断参数】，若为save，则执行save方法，获取前台三个数据构造diary，调用数据库插入操作，判断操作影响数，
若大于0，则【服务器跳转】首页：main?all=true。
若小于0，req先储存当前diary，req然后存入error信息，req放入diarySave页面，【服务器跳转mianTemp】

# 三、日志信息修改和删除
### 删除
新增diarydao.diaryDelete函数--输入diaryId和con--返回影响条数
新增diaryservlet.diarydelete函数，从前台接收diaryid，调用删除函数，然后【服务器跳转】主页

diaryShow.jsp页面添加删除方法，加入确认提示框：
```jsp
onclick="diaryDelete(${diary.diaryId})"
```
点击该方法则跳转到diaryDelete的js函数中：
```jsp
function diaryDelete(diaryId){
    if(confirm("确认需要删除？"){
        window.location="diary/action=delete&diaryId="+${diaryId};
    }
}
```

### 修改
新增diarydao.diaryUpdate函数

修改diaryservlet，在修改时跳转到的diarysave界面上需要带有点击的日记信息，这个可以在presave判断给主页导入diarysave页面之前进行处理。
在之前presave是用来将写日志的组建添加到页面写日志的，action=presave后面没有接diaryId参数，此时判断为写日志，此时不执行查询操作，塞入页面直接跳转，这样也就没有了内容。
若接了diaryId参数，判断为修改日志。需要调用查询语句加载日志内容到req的diary，然后跳转maintemp，此时可以界面可以获取diary内容。下拉框option中判断若下拉框数据源diaryTypeCount.diaryTypeId==diary.typeId时，标为'selected'，否则''。
```java
if(StringUtil.isNotEmpty(diaryId)){
    con=dbUtil.getCon();
    Diary diary=diaryDao.diaryShow(con, diaryId);
    request.setAttribute("diary", diary);
}
request.setAttribute("mainPage", "diary/diarySave.jsp");
request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
```
# 四、日志信息保存
在前台表单中新建一个隐藏域：
```jsp
<input type="hidden" id="diaryId" name="diaryId" value="${diary.diaryId }"/>
```
在表单提交时会传递日志id信息过来。
保存这里需要判断为：
若传递的diaryId没有，则为新建日志，用插入操作
若传递的diaryId有，则为修改日志，用更新操作

插入和更新操作在diarydao中完成

# 五、日志类别信息列表显示
点击导航栏的日志类别管理，请求：diaryType?action=list

查询日志类别信息，在diarytypedao中新建diarytypelist函数，获取diaryptypeid和typename两个属性，返回diarytypelist列表。
新建diarytypeservlet.java，配置xml属性：
查询diarytypelist，将其放入req中

新建目录diarytype，在下新建文件diarytypelist.jsp用来放入mainPage中
修改mainTemp中的日志管理链接：请求diaryType?action=list

使用bootstrap插入表格组件，在每行中放入修改和删除两个按钮，在日记列表标题栏添加添加日志类别按钮

# 六、日志类别信息添加
diaryTypeDao中新建diaryTypeAdd函数，向数据库中插入

在diaryTypeList.jsp中新建按钮请求：diaryType?action=preSave，【此时无typeId】，服务器跳转：diaryType/diaryTypeSave.jsp，该页面用来修改和添加日志类别，包含：
--日志类别名称：取出req中的日志类别名（修改操作）
--保存按钮，请求：diaryType?action=save，提交表单
--返回按钮，请求：上一页面

save条件中：判断当前req的id是否为空：
--空，执行插入
--非空，执行更新

# 七、日志类别修改
在diaryTypeList.jsp中修改按钮请求：diaryType?action=preSave&diaryTypeId=${diaryType.diaryTypeId }，【此时带有id】

diaryTypeDao中新建diaryTypeUpdate函数，向数据库更新数据（更新数据时需要先加载当前存在的数据，即先查询出类别来）

presave条件中：判断是否有id：
--有：将id对应类名存入req，跳转diaryTypeSave，此时该页面获取到类名，显示在文本框内
--无：跳转diaryTypeSave，直接显示空白文本框（添加操作）

**注意：presave主要作用为：区分添加和修改操作，添加操作：塞页面，空白数据；修改操作：塞页面，有数据。根本目的是为了文本框中是否显示数据**

# 八、日志类别删除和个人信息显示
### 日志类别删除
删除需要注意的是：当该类别下有日志时不能删除该类别，这个需要做判断。

delete条件中：判断，若当前类别下有日志，则不能删除，否则删除
是否存在日志：diaryDao.existDiaryWithTypeId(con,diaryTypeId)返回true or false
删除日志类别：diaryTypeDao.diaryTypeDelete(con,diaryTypeId)

执行完后需要【服务器跳转】diaryType?action=list

可以在页面地下加一个：显示错误信息
```
<div align="center"><font color="red">${error}</font></div>
```
### 个人信息显示
新建userServlet专门用来塞网页的，塞user/userSave.jsp给mainPage变量。

当点击导航栏的个人中心时跳转：user?action=preSave，跳转到userServlet，然后塞网页。

userSave网页，采用流式栅格栈，左4右8：
--4：用户头像
--8：上传文件|昵称|心情
8采用form表单，表单内容为table，四行两列，最后一行为提交按钮和返回按钮。

# 九、个人信息修改
### 文件上传
上传文件里面：
```jsp
<tr>
    <td width="20%">头像路径：</td>
    <td><input type="file" id="imagePath" name="imagePath"/></td>
</tr>
```
表单提交：
```jsp
<form action="user?action=save" method="post" enctype="multipart/form-data" onsubmit="return checkForm()">
```
enctype由于带图片上传，图片是二进制的所以和普通表单提交不一样，且不能使用req来获取表单属性值

save判断条件：
此时获取表单信息：
```java
FileItemFactory factory=new DiskFileItemFactory();
ServletFileUpload upload=new ServletFileUpload(factory);
List<FileItem> items=null;
try {
    items=upload.parseRequest(request);
} catch (FileUploadException e) {
    e.printStackTrace();
}
Iterator<FileItem> itr=items.iterator();

HttpSession session=request.getSession();

User user=(User)session.getAttribute("currentUser");
boolean imageChange=false;
while(itr.hasNext()){
    FileItem item=(FileItem)itr.next();
    if(item.isFormField()){
        String fieldName=item.getFieldName();
        if("nickName".equals(fieldName)){
            user.setNickName(item.getString("utf-8"));
        }
        if("mood".equals(fieldName)){
            user.setMood(item.getString("utf-8"));
        }
    }else if(!"".equals(item.getName())){
        try{
            imageChange=true;
            String imageName=DateUtil.getCurrentDateStr();
            user.setImageName(imageName+"."+item.getName().split("\\.")[1]);
            String filePath=PropertiesUtil.getValue("imagePath")+imageName+"."+item.getName().split("\\.")[1];
            item.write(new File(filePath));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
```
**Tips：当带图上传表单操作时，流程如下：**
1-新建工厂：FileItemFactory factory=new DiskFileItemFactory();

2-新建文件上传，传入工厂实例：ServletFileUpload upload=new ServletFileUpload(factory);

3-req转换：List<FileItem> items=items=upload.parseRequest(request);

4-获取列表迭代器：Iterator<FileItem> itr=items.iterator();

5-获取项：
FileItem item=(FileItem)itr.next();
if(item.isFormField()){
    String fieldName=item.getFieldName();//获取到表单域的名字
    item.getString("utf-8")//获取到表单域的值
}else if(!"".equals(item.getName())){
    String fileName=item.getName()//获取到图片名称
    item.write(new File(...));//直接用项的写函数，上传到地址，【注意：此处采用绝对地址】
}
### 信息更新
从session中获取当前userId，将其置入上面的user中，至此user信息完备，调用数据库进行更新操作，若
--影响>0则：将其重新覆盖到session中，【服务器跳转】到main?all=true
--影响<=0则：将其置入req中，让前台获取到，并返回错误信息，塞页面userSave.jsp再【服务器跳转】mainTemp.jsp

当image判断没有上传图片时，使用原来的图片
当image判断使用了新图片时，使用心得

这样就完成了---