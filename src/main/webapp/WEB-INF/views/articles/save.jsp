<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dev.linkcentral.database.entity.Member" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>글작성</title>
</head>
<body>
<%
    Member currentUser = (Member) session.getAttribute("member");
    String currentUsername = "";
    if (currentUser != null) {
        currentUsername = currentUser.getNickname();
    }
%>

<form action="/api/v1/article/save" method="post">
    writer: <input type="text" name="writer" value="${member.nickname}" readonly> <br>
    title: <input type="text" name="title"> <br>
    content: <textarea name="content" cols="30" rows="10"></textarea> <br>
    <input type="submit" value="글작성">
</form>

<%-- Java 코드를 여기에 추가하여 양식 처리 로직을 작성하세요 --%>

</body>
</html>
