<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>글작성</title>
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"
            integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU="
            crossorigin="anonymous">
    </script>
</head>
<body>
<form id="articleForm">
    writer: <input type="text" name="writer" value="${nickname}" readonly><br>
    title: <input type="text" name="title"><br>
    content: <textarea name="content" cols="30" rows="10"></textarea><br>
    <button type="button" id="saveArticleBtn">등록하기</button>
</form>
</body>
</html>