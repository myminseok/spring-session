<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" import="java.util.Enumeration"%>

<html>

<head>
    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>

    <script>
        $(document).ready(function(){
            $("#addBtn1").click(function(){
                var params="key="+$("#attr").val()+"&value="+$("#value").val()
                $.ajax({
                    url:"add",
                    type:"GET",
                    data:params,
                    success:function(){
                       window.location.reload(true);
                    }
                });
            });
        });
    </script>

</head>

<body>

<h1>Session Replication Demo with Redis</h1>


<h3>${message}</h3>



* following values will be duplicated redis when add new value with <font color="red"> TTL <% out.println(com.springapp.SessionManager.REDIS_TTL);%> <% out.println(com.springapp.SessionManager.REDIS_TTL_UNIT);%> </font><br>
* to extends TTL, refresh this page.<br><br>


<table border="1">
    <tr>
        <th>SESSION ATTRIBUTE</th>
        <th>VALUE</th>
        <th></th>
    </tr>
    <tr>
        <td><input type="text" id="attr"  size="30" placeholder="attribute name here"></td>
        <td><input type="text" id="value"  size="30" placeholder="value here"></td>
        <td><input type="button" id="addBtn1" value="ADD" style="background-color:lightgreen"></td>
    </tr>
    <tr>
        <td>session id</td>
        <td><%out.println(session.getId());%></td>
        <td></td>
    </tr>

<%

    String attributeKey=null;
    Enumeration<String> items = session.getAttributeNames();
    while(items.hasMoreElements()){
        attributeKey=items.nextElement();
        out.println("<tr>");
        out.println("<td>"+attributeKey+"</td>");
        out.println("<td>"+session.getAttribute(attributeKey)+"</td>");
        out.println(" <td></td>");
        out.println("</tr>");
    }
%>
</table>

</body>

</html>
