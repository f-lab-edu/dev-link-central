<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <title>회원 목록 조회</title>
</head>
<body>
<div>
  <h1>회원 목록</h1>
  <table>
    <thead>
    <tr>
      <th>email</th>
      <th>username</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="member" items="${members}">
      <tr>
        <td>${member.email}</td>
        <td>${member.username}</td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
</div>
</body>
</html>