<%@ page import="dev.linkcentral.service.dto.request.ArticleRequestDTO" %>
<%@ page import="dev.linkcentral.service.dto.request.ArticleCommentRequestDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="dev.linkcentral.database.entity.Member" %>
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

        // 댓글 수정 버튼 클릭 시 호출될 함수
        function editComment(commentId) {
            // 수정할 댓글의 내용을 입력할 input 필드와 저장 버튼 생성
            var commentContent = $("#comment-content-" + commentId).text();
            var editHtml = "<input type='text' id='edit-content-" + commentId + "' value='" + commentContent + "'>";
            editHtml += "<button onclick='submitCommentEdit(" + commentId + ")'>저장</button>";
            $("#comment-row-" + commentId).html(editHtml);
        }

        // 새로운 댓글 추가 함수
        const commentWrite = () => {
            const contents = document.getElementById("contents").value;
            const id = '<%= article.getId() %>';
            $.ajax({
                type: "post",
                url: "/api/v1/article/comment/save",
                contentType: "application/json",
                data: JSON.stringify({
                    "contents": contents,
                    "articleId": id
                }),
                success: function (res) {
                    // 댓글 추가 후 목록 새로고침
                    location.reload();
                },
                error: function (err) {
                    console.log("요청실패", err);
                }
            });
        }

        // AJAX를 통해 수정된 댓글 내용을 서버에 전송
        function submitCommentEdit(commentId) {
            var newContent = $("#edit-content-" + commentId).val();
            $.ajax({
                url: "/api/v1/article/comment/update/" + commentId,
                type: "PUT",
                contentType: "application/json",
                data: JSON.stringify({ contents: newContent }),
                success: function() {
                    // 성공 시 댓글 목록을 다시 로드
                    location.reload();
                },
                error: function(xhr, status, error) {
                    alert("댓글 수정 실패: " + error);
                }
            });
        }
    </script>

    <script>
        // 서버에 '좋아요' 상태를 변경해달라는 요청
        function toggleLike() {
            $.post("/api/v1/article/" + articleId + "/like", function () {
                updateLikesCount();
            }).fail(function () {
                console.log("좋아요 변경 요청 실패");
            });
        }

        function updateLikesCount() {
            // 캐시 방지를 위해 타임스탬프를 URL에 추가
            // 캐시된 데이터 대신 최신 데이터를 가져오기 위해 타임스탬프를 사용
            var timestamp = new Date().getTime();
            var query = "/api/v1/article/" + articleId + "/likes-count?_=" + timestamp;

            $.get(query, function (likesCount) {
                $('#likesCount').text(likesCount);
            });
        }

        $(document).ready(function () {
            updateLikesCount();
        });
    </script>

    <script type="text/javascript">
        var loggedInUserNickname = '<c:out value="${member.nickname}"/>'; // 로그인한 사용자의 닉네임
    </script>

    <script>
        const commentWrite = () => {
            const contents = document.getElementById("contents").value;
            const id = '<%= article.getId() %>';
            $.ajax({
                type: "post",
                url: "/api/v1/article/comment/save",
                contentType: "application/json",
                data: JSON.stringify({
                    "contents": contents,
                    "articleId": id
                }),
                success: function (res) {
                    let output = "<table>";
                    output += "<tr><th>댓글번호</th>";
                    output += "<th>작성자</th>";
                    output += "<th>내용</th>";
                    output += "<th>작성시간</th></tr>";
                    for (let i in res) {
                        output += "<tr id='comment-row-" + res[i].id + "'>";
                        output += "<td>" + res[i].id + "</td>";
                        output += "<td>" + res[i].nickname + "</td>";
                        output += "<td id='comment-content-" + res[i].id + "'>" + res[i].contents + "</td>";
                        output += "<td>" + res[i].createdAt + "</td>";

                        if (loggedInUserNickname === res[i].nickname) {
                            output += "<td><button onclick='editComment(" + res[i].id + ")'>수정</button></td>";
                        }
                        output += "</tr>";
                    }
                    output += "</table>";
                    document.getElementById('comment-list').innerHTML = output;
                    document.getElementById('contents').value = '';
                },
                error: function (err) {
                    console.log("요청실패", err);
                }
            });
        }
    </script>


    <script>
        function deleteComment(commentId) {
            if(confirm("댓글을 삭제하시겠습니까?")) {
                $.ajax({
                    url: "/api/v1/article/comment/delete/" + commentId,
                    type: "DELETE",
                    success: function() {
                        alert("댓글이 삭제되었습니다.");
                        location.reload();
                    },
                    error: function(xhr, status, error) {
                        alert("댓글 삭제 실패: " + error);
                    }
                });
            }
        }
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
        <td><%= article.getViews() %>
        </td>
    </tr>
</table>
<button onclick="listReq()">목록</button>
<button onclick="updateReq()">수정</button>
<button onclick="deleteReq()">삭제</button>

<button id="likeButton" onclick="toggleLike()">좋아요</button>
<span id="likesCount">0</span> 좋아요

<!-- 댓글 작성 부분 -->
<div id="comment-write">
    <input type="text" id="contents" placeholder="내용">
    <button id="comment-write-btn" onclick="commentWrite()">댓글작성</button>
</div>


<%--댓글 출력 부분--%>
<div id="comment-list">
    <table>
        <tr>
            <th>댓글번호</th>
            <th>작성자</th>
            <th>내용</th>
            <th>작성시간</th>
        </tr>
        <%
            List<ArticleCommentRequestDTO> commentList = (List<ArticleCommentRequestDTO>) request.getAttribute("commentList");
            if (commentList != null) {
                for (ArticleCommentRequestDTO comment : commentList) {
        %>
        <tr id="comment-row-<%= comment.getId() %>">
            <td><%= comment.getId() %></td>
            <td><%= comment.getNickname() %></td>
            <td id="comment-content-<%= comment.getId() %>"><%= comment.getContents() %></td>
            <td><%= comment.getCreatedAt() %></td>
            <% if (session.getAttribute("member") != null &&
                    comment.getNickname().equals(((Member)session.getAttribute("member")).getNickname())) { %>
            <td>
                <button onclick="editComment(<%= comment.getId() %>)">수정</button>
                <button onclick="deleteComment(<%= comment.getId() %>)">삭제</button>
            </td>
            <% } %>
        </tr>
        <%
                }
            }
        %>
    </table>
</div>


</body>
</html>
