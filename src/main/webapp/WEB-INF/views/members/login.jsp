<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>프로젝트!</title>
</head>
<body>
<p><%= request.getAttribute("memberName") %>님 환영합니다!</p>
</body>
</html>