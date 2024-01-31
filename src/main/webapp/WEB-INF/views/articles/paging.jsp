<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>

    <script>
        function saveReq() {
            location.href = "/api/v1/article/save";
        }
    </script>
</head>
<body>

<% if (session.getAttribute("member") != null) { %>
<button onclick="saveReq()">글작성</button>
<% } %>

<table>
    <tr>
        <th>id</th>
        <th>writer</th>
        <th>title</th>
        <th>date</th>
    </tr>
    <c:if test="${not empty articleList}">
        <c:forEach items="${articleList}" var="article">
            <tr>
                <td>${article.id}</td>
                <td><a href="/api/v1/profile/view?memberId=${article.writerId}">${article.title}</a></td>
                <td><a href="/api/v1/article/${article.id}?page=${articlePage.number + 1}">${article.writer}</a></td>
                <td>${article.createdAt}</td>
            </tr>
        </c:forEach>
    </c:if>
    <c:if test="${empty articleList}">
        <tr>
            <td colspan="5">게시글이 없습니다.</td>
        </tr>
    </c:if>
</table>

<a href="/api/v1/article/paging?page=1">처음 페이지</a>

<!-- 이전 페이지 링크 -->
<c:choose>
    <c:when test="${articlePage.number eq 0}">
        <a href="#">이전</a>
    </c:when>
    <c:otherwise>
        <a href="/api/v1/article/paging?page=${articlePage.number}">이전</a>
    </c:otherwise>
</c:choose>


<!-- 페이지 번호 링크 -->
<c:forEach begin="${startPage}" end="${endPage}" var="page">
    <c:choose>
        <c:when test="${page eq articlePage.number + 1}">
            <span>${page}</span>
        </c:when>
        <c:otherwise>
            <a href="/api/v1/article/paging?page=${page}">${page}</a>
        </c:otherwise>
    </c:choose>
</c:forEach>


<!-- 다음 페이지 링크 -->
<c:choose>
    <c:when test="${articlePage.number + 1 eq articlePage.totalPages}">
        <a href="#">다음</a>
    </c:when>
    <c:otherwise>
        <a href="/api/v1/article/paging?page=${articlePage.number + 2}">다음</a>
    </c:otherwise>
</c:choose>


<!-- 마지막 페이지 링크 -->
<a href="/api/v1/article/paging?page=${articlePage.totalPages}">마지막 페이지</a>

</body>
</html>