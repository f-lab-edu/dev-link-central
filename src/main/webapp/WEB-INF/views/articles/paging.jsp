<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"
            integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU="
            crossorigin="anonymous">
    </script>

    <script>
        $(document).ready(function() {
            // 로컬 스토리지에서 JWT 토큰 확인
            const token = localStorage.getItem('jwt');
            if (token) {
                // 토큰이 존재하면 글작성 버튼 표시
                $('#writeButton').show();
            } else {
                // 토큰이 없으면 글작성 버튼 숨김
                $('#writeButton').hide();
            }
        });

        function saveReq() {
            const token = localStorage.getItem('jwt');
            if (token) {
                // AJAX 요청을 통해 서버에 JWT 전달
                $.ajax({
                    url: '/api/v1/view/article/save-form',
                    type: 'GET',
                    beforeSend: function(xhr) {
                        xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                    },
                    success: function(response) {
                        // 서버로부터 응답이 성공적으로 돌아왔을 때
                        // 응답으로 받은 HTML을 현재 페이지에 삽입하거나 새로운 페이지로 이동
                        document.body.innerHTML = response; // 현재 페이지에 폼 삽입
                        $("#saveArticleBtn").on("click", function (e){
                            e.preventDefault();

                            const formData = {
                                writer: $('input[name="writer"]').val(),
                                title: $('input[name="title"]').val(),
                                content: $('textarea[name="content"]').val()
                            };

                            $.ajax({
                                url: "/api/v1/article/save",
                                type: 'POST',
                                contentType: 'application/json',
                                data: JSON.stringify(formData),
                                beforeSend: function(xhr) {
                                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwt'));
                                },
                                success: function(response) {
                                    console.log('성공:', response);
                                    alert('글이 성공적으로 작성되었습니다.');
                                    window.location.href = "/api/v1/view/article/paging";
                                },
                                error: function(xhr, status, error) {
                                    console.log('오류:', status, error);
                                    alert('글 작성에 실패했습니다.');
                                }
                            });
                        });
                    },
                    error: function(xhr, status, error) {
                        console.error('Error: ' + error);
                        alert('인증 오류가 발생했습니다. 로그인 페이지로 이동합니다.');
                        window.location.href = '/';
                    }
                });
            } else {
                alert('로그인이 필요합니다.');
                window.location.href = '/login';
            }
        }
    </script>
</head>
<body>

<% if ((Boolean) request.getAttribute("isAuthenticated")) { %>
<button onclick="saveReq()">글작성</button>
<% } %>

<button id="writeButton" onclick="saveReq()" style="display:none;">글작성</button>

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
                <td><a href="/api/v1/view/profile/view?memberId=${article.writerId}">${article.title}</a></td>
                <td><a href="/api/v1/view/article/${article.id}?page=${articlePage.number + 1}">${article.writer}</a></td>
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

<a href="/api/v1/view/article/paging?page=1">처음 페이지</a>

<!-- 이전 페이지 링크 -->
<c:choose>
    <c:when test="${articlePage.number eq 0}">
        <a href="#">이전</a>
    </c:when>
    <c:otherwise>
        <a href="/api/v1/view/article/paging?page=${articlePage.number}">이전</a>
    </c:otherwise>
</c:choose>


<!-- 페이지 번호 링크 -->
<c:forEach begin="${startPage}" end="${endPage}" var="page">
    <c:choose>
        <c:when test="${page eq articlePage.number + 1}">
            <span>${page}</span>
        </c:when>
        <c:otherwise>
            <a href="/api/v1/view/article/paging?page=${page}">${page}</a>
        </c:otherwise>
    </c:choose>
</c:forEach>


<!-- 다음 페이지 링크 -->
<c:choose>
    <c:when test="${articlePage.number + 1 eq articlePage.totalPages}">
        <a href="#">다음</a>
    </c:when>
    <c:otherwise>
        <a href="/api/v1/view/article/paging?page=${articlePage.number + 2}">다음</a>
    </c:otherwise>
</c:choose>


<!-- 마지막 페이지 링크 -->
<a href="/api/v1/view/article/paging?page=${articlePage.totalPages}">마지막 페이지</a>

</body>
</html>