<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>글작성</title>
</head>
<body>
<form action="/article/save" method="post">
    writer: <input type="text" name="writer"> <br>
    title: <input type="text" name="title"> <br>
    content: <textarea name="content" cols="30" rows="10"></textarea> <br>
    <input type="submit" value="글작성">
</form>

<%-- Java 코드를 여기에 추가하여 양식 처리 로직을 작성하세요 --%>

</body>
</html>