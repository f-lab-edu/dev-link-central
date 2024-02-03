<%@ page import="dev.linkcentral.service.dto.request.ArticleRequestDTO" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>detail</title>
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"
            integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU=" crossorigin="anonymous"></script>

    <script>
        <% ArticleRequestDTO article = (ArticleRequestDTO) request.getAttribute("article"); %>
        const articleId = <%= article.getId() %>;

        function updateReq() {
            console.log("수정 요청");
            location.href = "/api/v1/article/update/" + articleId;
        }

        function listReq() {
            console.log("목록 요청");
            location.href = "/api/v1/article/paging?page=${page}";
        }

        function deleteReq() {
            console.log("삭제 요청");
            $.ajax({
                url: "/api/v1/article/delete/" + articleId,
                type: "DELETE",
                success: function (response) {
                    alert("게시글이 삭제되었습니다.");
                    location.href = "/api/v1/article/paging?page=${page}";
                },
                error: function(error) {
                    alert("삭제 중 오류가 발생했습니다: " + error);
                }
            });
        }
    </script>

    <script>
        // 서버에 '좋아요' 상태를 변경해달라는 요청
        function toggleLike() {
            $.post("/api/v1/article/" + articleId + "/like", function() {
                updateLikesCount();
            }).fail(function() {
                console.log("좋아요 변경 요청 실패");
            });
        }

        function updateLikesCount() {
            // 캐시 방지를 위해 타임스탬프를 URL에 추가
            // 캐시된 데이터 대신 최신 데이터를 가져오기 위해 타임스탬프를 사용
            var timestamp = new Date().getTime();
            var query = "/api/v1/article/" + articleId + "/likes-count?_=" + timestamp;

            $.get(query, function(likesCount) {
                $('#likesCount').text(likesCount);
            });
        }

        $(document).ready(function() {
            updateLikesCount();
        });
    </script>
</head>
<body>


<table>
    <tr>
        <th>id</th>
        <td><%= article.getId() %>
        </td>
    </tr>
    <tr>
        <th>writer</th>
        <td><%= article.getWriter() %>
        </td>
    </tr>
    <tr>
        <th>title</th>
        <td><%= article.getTitle() %>
        </td>
    </tr>
    <tr>
        <th>date</th>
        <td><%= article.getCreatedAt() %>
        </td>
    </tr>
    <tr>
        <th>contents</th>
        <td><%= article.getContent() %>
        </td>
    </tr>
    <tr>
        <th>조회수</th>
        <td><%= article.getViews() %></td>
    </tr>
</table>
<button onclick="listReq()">목록</button>
<button onclick="updateReq()">수정</button>
<button onclick="deleteReq()">삭제</button>

<button id="likeButton" onclick="toggleLike()">좋아요</button>
<span id="likesCount">0</span> 좋아요

</body>
</html>
