<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dev.linkcentral.service.dto.request.ArticleRequestDTO" %>
<%@ page import="dev.linkcentral.service.dto.request.ArticleCommentRequestDTO" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>detail</title>
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"
            integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU="
            crossorigin="anonymous">
    </script>

    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f8f9fa;
            color: #333;
            margin: 0;
            padding: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }

        th, td {
            padding: 10px;
            border: 1px solid #ddd;
        }

        th {
            background-color: #f2f2f2;
        }

        button {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 10px 20px;
            margin: 5px;
            border-radius: 5px;
            cursor: pointer;
        }

        button:hover {
            background-color: #0056b3;
        }

        #comment-write {
            margin-top: 20px;
        }

        #comment-write input {
            padding: 10px;
            margin-right: 10px;
            width: 80%;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        #comment-write-btn {
            padding: 10px 20px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        #comment-write-btn:hover {
            background-color: #218838;
        }

        #comment-list table {
            width: 100%;
            background-color: #fff;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        #loading {
            text-align: center;
            padding: 20px;
        }

        #likeButton {
            background-color: #ffc107;
            color: black;
        }

        #likeButton:hover {
            background-color: #e0a800;
        }

        #loading {
            text-align: center;
            padding: 10px;
            display: none; /* Hidden by default, shown when loading */
        }

        #comment-table {
            width: 100%;
            background-color: #fff;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            border-collapse: collapse;
            margin-top: 20px;
        }

        #comment-table th, #comment-table td {
            padding: 10px;
            border: 1px solid #ddd;
        }

        #comment-table th {
            background-color: #f2f2f2;
        }

        #comment-table td {
            background-color: #ffffff;
        }
    </style>

    <script>
        // 게시글 수정 페이지로 이동하는 함수
        function updateReq() {
            console.log("수정 요청");
            window.location.href = "/api/v1/view/article/update-form/" + articleId;
        }

        // 게시글 목록 페이지로 이동하는 함수
        function listReq() {
            console.log("목록 요청");
            window.location.href = "/api/v1/view/article/paging?page=${page}";
        }

        // 게시글 삭제 요청 함수
        function deleteReq() {
            console.log("삭제 요청");
            $.ajax({
                url: "/api/v1/article/delete/" + articleId,
                type: "DELETE",
                success: function (response) {
                    alert("게시글이 삭제되었습니다.");
                    window.location.href = "/api/v1/article/paging?page=${page}";
                },
                error: function (error) {
                    alert("삭제 중 오류가 발생했습니다: " + error);
                }
            });
        }

        // 서버에 '좋아요' 상태를 변경해달라는 요청
        function toggleLike() {
            $.post("/api/v1/article/" + articleId + "/like", function () {
                updateLikesCount();
            }).fail(function () {
                console.log("좋아요 변경 요청 실패");
            });
        }

        // '좋아요' 수 업데이트 함수
        function updateLikesCount() {
            var query = "/api/v1/article/" + articleId + "/likes-count";
            $.get(query, function (likesCount) {
                $('#likesCount').text(likesCount);
            });
        }

        // 페이지 로드 시 '좋아요' 수 업데이트
        $(document).ready(function () {
            updateLikesCount();
        });


        // 댓글 추가
        const commentWrite = () => {
            const contents = document.getElementById("contents").value;
            const token = localStorage.getItem('token'); // 로그인 후 저장된 JWT 토큰

            $.ajax({
                type: "post",
                url: "/api/v1/article/" + articleId + "/comments",
                headers: {
                    'Authorization': `Bearer ${token}` // 토큰을 헤더에 포함하여 전송
                },
                contentType: "application/json",
                data: JSON.stringify({ "contents": contents }),
                success: function (savedCommentDTO) {
                    console.log("요청성공", savedCommentDTO);
                    // 새 댓글을 목록의 맨 위에 추가합니다.
                    $('#comment-list').prepend(
                        '<tr>' +
                        '<td>' + savedCommentDTO.id + '</td>' +
                        '<td>' + savedCommentDTO.nickname + '</td>' +
                        '<td>' + savedCommentDTO.contents + '</td>' +
                        '<td>' + savedCommentDTO.createdAt + '</td>' +
                        '</tr>'
                    );
                    document.getElementById('contents').value = '';
                },
                error: function (err) {
                    console.log("요청실패", err);
                }
            });
        }



        <% ArticleRequestDTO article = (ArticleRequestDTO) request.getAttribute("article"); %>
        var articleId = <%= article.getId() %>;
        console.log("Article ID:", articleId);



        // 댓글 무한 스크롤
        let page = 0;
        let isLoading = false;
        let totalPages = undefined; // 전체 페이지 수를 저장할 변수

        function loadComments() {
            if (isLoading || (typeof totalPages !== 'undefined' && page >= totalPages)) {
                return;
            }
            isLoading = true;
            $('#loading').show();

            $.ajax({
                url: "/api/v1/article/" + articleId + "/comments?page=" + page,
                type: 'GET',
                success: function (response) {
                    // 서버로부터 받은 댓글 데이터 처리
                    response.comments.forEach(function(comment) {
                        $('#comment-list').append(
                            '<tr>' +
                            '<td>' + comment.id + '</td>' +
                            '<td>' + comment.nickname + '</td>' +
                            '<td>' + comment.contents + '</td>' +
                            '<td>' + comment.createdAt + '</td>' +
                            '</tr>'
                        );
                    });
                    totalPages = response.totalPages; // 전체 페이지 수 업데이트
                    page += 1; // 페이지 번호 증가

                    if (page >= totalPages) {
                        $('#loading').hide(); // 더 이상 로드할 페이지가 없으면 로딩 인디케이터 숨김
                    }
                },
                error: function (xhr) {
                    console.error("Error loading comments:", xhr.responseText);
                },
                complete: function() {
                    isLoading = false;
                    if (page >= totalPages) {
                        $('#loading').hide(); // AJAX 호출 완료 후 처리
                    }
                }
            });
        }

        // 문서 끝에 도달했는지 확인하는 조건을 수정
        function onScroll() {
            if ($(window).scrollTop() + $(window).height() >= $('#comment-list').offset().top + $('#comment-list').outerHeight()) {
                loadComments();
            }
        }

        // 초기 페이지 로딩 시 첫 번째 페이지의 댓글을 로드하고, 스크롤 이벤트를 바인딩합니다.
        $(document).ready(function () {
            loadComments();
            $(window).scroll(onScroll);
        });
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

        // '좋아요' 수 업데이트 함수
        function updateLikesCount() {
            var query = "/api/v1/article/" + articleId + "/likes-count";
            $.get(query, function (likesCount) {
                $('#likesCount').text(likesCount);
            });
        }

        // 페이지 로드 시 '좋아요' 수 업데이트
        $(document).ready(function () {
            updateLikesCount();
        });


        // 댓글 추가
        const commentWrite = () => {
            const contents = document.getElementById("contents").value;
            const token = localStorage.getItem('token'); // 로그인 후 저장된 JWT 토큰

            $.ajax({
                type: "post",
                url: "/api/v1/article/" + articleId + "/comments",
                headers: {
                    'Authorization': `Bearer ${token}` // 토큰을 헤더에 포함하여 전송
                },
                contentType: "application/json",
                data: JSON.stringify({ "contents": contents }),
                success: function (savedCommentDTO) {
                    console.log("요청성공", savedCommentDTO);
                    // 새 댓글을 목록의 맨 위에 추가합니다.
                    $('#comment-list').prepend(
                        '<tr>' +
                        '<td>' + savedCommentDTO.id + '</td>' +
                        '<td>' + savedCommentDTO.nickname + '</td>' +
                        '<td>' + savedCommentDTO.contents + '</td>' +
                        '<td>' + savedCommentDTO.createdAt + '</td>' +
                        '</tr>'
                    );
                    document.getElementById('contents').value = '';
                },
                error: function (err) {
                    console.log("요청실패", err);
                }
            });
        }



        <% ArticleRequestDTO article = (ArticleRequestDTO) request.getAttribute("article"); %>
        var articleId = <%= article.getId() %>;
        console.log("Article ID:", articleId);



        // 댓글 무한 스크롤
        let page = 0;
        let isLoading = false;
        let totalPages = undefined; // 전체 페이지 수를 저장할 변수

        function loadComments() {
            if (isLoading || (typeof totalPages !== 'undefined' && page >= totalPages)) {
                return;
            }
            isLoading = true;
            $('#loading').show();

            $.ajax({
                url: "/api/v1/article/" + articleId + "/comments?page=" + page,
                type: 'GET',
                success: function (response) {
                    // 서버로부터 받은 댓글 데이터 처리
                    response.comments.forEach(function(comment) {
                        $('#comment-list').append(
                            '<tr>' +
                            '<td>' + comment.id + '</td>' +
                            '<td>' + comment.nickname + '</td>' +
                            '<td>' + comment.contents + '</td>' +
                            '<td>' + comment.createdAt + '</td>' +
                            '</tr>'
                        );
                    });
                    totalPages = response.totalPages; // 전체 페이지 수 업데이트
                    page += 1; // 페이지 번호 증가

                    if (page >= totalPages) {
                        $('#loading').hide(); // 더 이상 로드할 페이지가 없으면 로딩 인디케이터 숨김
                    }
                },
                error: function (xhr) {
                    console.error("Error loading comments:", xhr.responseText);
                },
                complete: function() {
                    isLoading = false;
                    if (page >= totalPages) {
                        $('#loading').hide(); // AJAX 호출 완료 후 처리
                    }
                }
            });
        }

        // 문서 끝에 도달했는지 확인하는 조건을 수정
        function onScroll() {
            if ($(window).scrollTop() + $(window).height() >= $('#comment-list').offset().top + $('#comment-list').outerHeight()) {
                loadComments();
            }
        }

        // 초기 페이지 로딩 시 첫 번째 페이지의 댓글을 로드하고, 스크롤 이벤트를 바인딩합니다.
        $(document).ready(function () {
            loadComments();
            $(window).scroll(onScroll);
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
        <td><%= article.getViews() %>
        </td>
    </tr>
</table>
<button onclick="listReq()">목록</button>
<button onclick="updateReq()">수정</button>
<button onclick="deleteReq()">삭제</button>

<button id="likeButton" onclick="toggleLike()">좋아요</button>
<span id="likesCount">0</span> 좋아요

<%--댓글 작성 부분--%>
<div id="comment-write">
    <input type="text" id="contents" placeholder="내용">
    <button id="comment-write-btn" onclick="commentWrite()">댓글작성</button>
</div>


<%--댓글 출력 부분--%>
<table id="comment-table">
    <thead>
    <tr>
        <th>댓글번호</th>
        <th>작성자</th>
        <th>내용</th>
        <th>작성시간</th>
    </tr>
    </thead>
    <tbody id="comment-list">
        <%-- AJAX를 통해 여기에 댓글이 동적으로 추가 한다. --%>
    </tbody>
</table>

<%--로딩 인디케이터--%>
<div id="loading">
    Loading more comments...
</div>

</body>
</html>
