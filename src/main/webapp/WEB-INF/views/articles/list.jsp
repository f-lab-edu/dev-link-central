<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>list</title>
</head>
<body>
<table>
  <tr>
    <th>id</th>
    <th>writer</th>
    <th>title</th>
    <th>date</th>
    <th>hits</th>
  </tr>
  <c:forEach items="${articleList}" var="article">
    <tr>
      <td><c:out value="${article.id}"/></td>

      <td><c:out value="${article.writer}"/></td>

      <td><a href="<c:url value='/api/v1/article/${article.id}'/>"><c:out value="${article.title}"/></a></td>

      <td><c:out value="${article.createdAt}"/></td>

        <%--조회수, 좋아요 추가하기--%>
    </tr>
  </c:forEach>
</table>
</body>
</html>