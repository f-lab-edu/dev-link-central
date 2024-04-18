<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dev.linkcentral.presentation.dto.request.ArticleCreateRequest" %>
<%@ page import="dev.linkcentral.presentation.dto.request.ArticleCommentRequest" %>
<%@ page import="java.util.List" %>
<%@ page import="dev.linkcentral.database.entity.Member" %>
<%@ page import="org.springframework.data.domain.Page" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- jQuery library -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
    <!-- Bootstrap JS 및 jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 JS -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>

    <style>
        body {
            font-family: 'Arial', sans-serif;
            margin: 20px;
            padding: 0;
            background-color: #f4f4f4;
            color: #333;
        }
        table {
            width: 100%;
            margin-top: 20px;
            border-collapse: collapse;
        }
        th, td {
            padding: 10px;
            border: 1px solid #ddd;
            text-align: left;
        }
        th {
            background-color: #f7f7f7;
            color: #333;
        }
        tr:nth-child(even) {
            background-color: #f2f2f2;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            font-size: 16px;
            cursor: pointer;
            text-align: center;
            text-decoration: none;
            outline: none;
            color: #fff;
            background-color: #007bff;
            border: none;
            border-radius: 5px;
            box-shadow: 0 9px #999;
        }
        .btn:hover {background-color: #0056b3}
        .btn:active {
            background-color: #0056b3;
            box-shadow: 0 5px #666;
            transform: translateY(4px);
        }
        .input-field {
            padding: 10px;
            margin: 5px 0;
            border: 1px solid #ddd;
            border-radius: 5px;
            width: calc(100% - 22px);
        }
        #comment-write {
            background-color: #fff;
            padding: 15px;
            margin-top: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0,0,0,.1);
        }
        #comment-list {
            margin-top: 20px;
        }
        #comment-list table {
            width: 100%;
            background-color: #fff;
            margin-top: 10px;
        }
        #comment-list thead th {
            background-color: #f7f7f7;
        }
        #comment-write-btn {
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 4px;
            padding: 10px 20px;
            font-size: 14px;
            cursor: pointer;
            margin-left: 10px;
        }
        #comment-write-btn:hover {
            background-color: #218838;
        }

        #leaderMessage {
            color: #007bff;
            font-weight: bold;
            margin-top: 10px;
        }
    </style>

    <script>
        <% ArticleCreateRequest article = (ArticleCreateRequest) request.getAttribute("article"); %>
        var articleId = <%= article.getId() %>;
        console.log("Article ID:", articleId);

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
                url: "/api/v1/article/" + articleId,
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                type: "DELETE",
                success: function (response) {
                    alert("게시글이 삭제되었습니다.");
                    window.location.href = "/api/v1/view/article/paging";
                },
                error: function (error) {
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

        // AJAX를 통해 수정된 댓글 내용을 서버에 전송
        function submitCommentEdit(commentId) {
            var newContent = $("#edit-content-" + commentId).val();
            $.ajax({
                url: "/api/v1/article/comment/" + commentId,
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                type: "PUT",
                contentType: "application/json",
                data: JSON.stringify({ contents: newContent }),
                success: function() {
                    // 성공 시 댓글 목록을 다시 로드
                    location.reload();
                },
                error: function(xhr, status, error) {
                    alert("댓글 수정 실패: 다른 유저가 작성한 댓글입니다." + error);
                }
            });
        }

        function deleteComment(commentId) {
            if(confirm("댓글을 삭제하시겠습니까?")) {
                $.ajax({
                    url: "/api/v1/article/comment/" + commentId,
                    type: "DELETE",
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                    },
                    success: function() {
                        alert("댓글이 삭제되었습니다.");
                        location.reload();
                    },
                    error: function(xhr, status, error) {
                        alert("댓글 삭제 실패: 다른 유저가 작성한 댓글입니다." + error);
                    }
                });
            }
        }
    </script>


    <script type="text/javascript">
        var loggedInUserNickname = '<c:out value="${member.nickname}"/>'; // 로그인한 사용자의 닉네임
    </script>


    <script>
        // 서버에 '좋아요' 상태를 변경해달라는 요청
        function toggleLike() {
            const token = localStorage.getItem('jwt'); // 로그인 후 저장된 JWT 토큰을 가져옵니다
            if (!token) {
                console.log("인증 토큰이 없습니다.");
                return;
            }

            $.ajax({
                url: "/api/v1/article/" + articleId + "/like",
                type: "POST",
                headers: {
                    'Authorization': 'Bearer ' + token
                },
                success: function () {
                    updateLikesCount();
                },
                error: function () {
                    console.log("좋아요 변경 요청 실패");
                }
            });
        }

        // '좋아요' 수 업데이트 함수
        function updateLikesCount() {
            $.ajax({
                url: "/api/v1/article/" + articleId + "/likes-count",
                type: "GET",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(likesCount) {
                    $('#likesCount').text(likesCount);
                },
                error: function(xhr, status, error) {
                    console.error("Error updating likes count:", status, error);
                }
            });
        }

        // 페이지 로드 시 '좋아요' 수 업데이트
        $(document).ready(function () {
            updateLikesCount();
        });

        // 댓글 작성 AJAX 함수
        function commentWrite() {
            var contents = $('#contents').val(); // 사용자가 입력한 댓글 내용을 가져옵니다.
            if (!contents) {
                alert('댓글 내용을 입력해주세요.');
                return;
            }
            $.ajax({
                url: "/api/v1/article/" + articleId + "/comments", // 서버의 댓글 저장 API 경로
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify({ contents: contents }),
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") }, // 필요한 경우 JWT 토큰 추가
                success: function(comment) {
                    $('#contents').val(''); // 입력 필드 초기화
                    // 새로운 댓글만 페이지에 추가
                    var newCommentHtml = "<tr>" +
                        "<td>" + comment.id + "</td>" +
                        "<td>" + comment.nickname + "</td>" +
                        "<td>" + comment.contents + "</td>" +
                        "<td>" + comment.createdAt + "</td>" +
                        "</tr>";
                    $("#comment-list table tbody").prepend(newCommentHtml); // 댓글 목록의 맨 위에 새 댓글 추가
                    location.reload(); // 페이지 새로고침
                },
                error: function(xhr, status, error) {
                    alert('댓글 작성 실패: ' + xhr.responseText);
                }
            });
        }

        // 댓글 목록 로드 AJAX 함수
        var currentPage = 0; // 현재 페이지 번호
        var isFetchingComments = false; // AJAX 요청 중복 방지
        var hasMoreComments = true; // 더 불러올 댓글이 있는지

        function loadComments() {
            if(isFetchingComments || !hasMoreComments) return; // 중복 요청 및 더 이상 불러올 댓글이 없을 때 요청 방지

            isFetchingComments = true;
            $.ajax({
                url: "/api/v1/view/article/" + articleId + "/comments?page=" + currentPage,
                type: "GET",
                success: function(response) {
                    if(response.comments.length > 0){
                        var commentsHtml = response.comments.map(function(comment) {
                            return "<tr>" +
                                "<td>" + comment.id + "</td>" +
                                "<td>" + comment.nickname + "</td>" +
                                "<td>" + comment.contents + "</td>" +
                                "<td>" + comment.createdAt + "</td>" +
                                "<td><button onclick='editComment(" + comment.id + ")'>수정</button>" +
                                "<button onclick='deleteComment(" + comment.id + ")'>삭제</button></td>" +
                                "</tr>";
                        }).join('');
                        $("#comment-list table tbody").append(commentsHtml);
                        currentPage++; // 페이지 번호 증가
                    } else {
                        hasMoreComments = false; // 더 불러올 댓글이 없음
                    }
                    isFetchingComments = false;
                },
                error: function(xhr, status, error) {
                    alert('댓글 목록 로딩 실패: ' + xhr.responseText);
                    isFetchingComments = false;
                }
            });
        }

        // 스크롤 이벤트 리스너를 수정하여 스크롤이 페이지 하단에 도달할 때마다 새로운 댓글을 로드
        $(window).scroll(function() {
            if($(window).scrollTop() + $(window).height() > $(document).height() - 100) {
                loadComments(); // 댓글 로드 함수 호출
            }
        });

        $(document).ready(function() {
            loadComments(); // 초기 댓글 로드
        });
    </script>


    <script>
        // 무한 스크롤
        var currentPage = 0; // 현재 페이지 번호
        var isFetchingComments = false; // AJAX 요청 중복 방지
        var hasMoreComments = true; // 더 불러올 댓글이 있는지

        function loadComments() {
            if(isFetchingComments || !hasMoreComments) return;

            isFetchingComments = true;
            $.ajax({
                url: "/api/v1/view/article/" + articleId + "/comments?page=" + currentPage,
                type: "GET",
                success: function(response) {
                    if(response.comments.length > 0){
                        // 댓글 데이터를 동적으로 삽입하는 부분 수정
                        var commentsHtml = response.comments.map(function(comment) {
                            return "<tr id='comment-row-" + comment.id + "'>" +
                                "<td>" + comment.id + "</td>" +
                                "<td>" + comment.nickname + "</td>" +
                                "<td id='comment-content-" + comment.id + "'>" + comment.contents + "</td>" +
                                "<td>" + comment.createdAt + "</td>" +
                                "<td><button onclick='editComment(" + comment.id + ")'>수정</button>" +
                                "<button onclick='deleteComment(" + comment.id + ")'>삭제</button></td>" +
                                "</tr>";
                        }).join('');
                        $("#comment-list table tbody").append(commentsHtml); // 기존 댓글에 새로운 댓글 추가
                        currentPage++; // 페이지 번호 증가
                    } else {
                        hasMoreComments = false; // 더 불러올 댓글이 없음
                    }
                    isFetchingComments = false;
                },
                error: function(xhr, status, error) {
                    alert('댓글 목록 로딩 실패: ' + xhr.responseText);
                    isFetchingComments = false;
                }
            });
        }

        // 스크롤 이벤트 리스너
        $(window).scroll(function() {
            // 페이지 끝에 도달했는지 확인
            if($(window).scrollTop() + $(window).height() >= $(document).height() - 100) { // 조금 더 일찍 로드하도록 조정
                loadComments(); // 추가 댓글 로드
            }
        });

        $(document).ready(function() {
            loadComments(); // 초기 댓글 로드
        });
    </script>

    <%-- 스터디 그룹 요청 함수 --%>
    <script>
        $(document).ready(function() {
            // 게시글 ID를 기반으로 스터디 그룹 상세 정보를 조회하는 AJAX 요청
            var articleId = $('#articleData').data('articleId');

            $.ajax({
                url: "/api/v1/study-group/details/" + articleId,
                type: "GET",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(response) {
                    // 서버 응답에서 스터디 그룹의 이름과 주제를 표시
                    $("#studyGroupName").text(response.groupName);
                    $("#studyTopic").text(response.studyTopic);

                    if (!response.leaderStatus) {
                        $("#requestJoinStudyGroup").data("studyGroupId", response.id).show();
                    } else {
                        $("#requestJoinStudyGroup").hide();
                        $("#leaderMessage").show(); // 메시지를 표시
                    }
                },

                error: function(xhr, status, error) {
                    console.error("스터디 그룹 정보 조회 실패:", status, error);
                    $("#studyGroupSection").hide();
                }
            });

            // 스터디 그룹 가입 요청 버튼 클릭 이벤트 처리
            $("#requestJoinStudyGroup").click(function() {
                var studyGroupId = $(this).data("studyGroupId");
                console.log("--> studyGroupId: " + studyGroupId);

                $.ajax({
                    url: "/api/v1/study-group/" + studyGroupId + "/join-requests",
                    type: "POST",
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                    },
                    success: function(response) {
                        alert("스터디 그룹 가입 요청이 성공적으로 전송되었습니다.");
                    },
                    error: function(xhr, status, error) {
                        alert("해당 게시글 사용자가 스터디 그룹을 생성하지 않았습니다.");
                    }
                });
            });
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
<span id="likesCount">0</span>


<%--스터디 신청--%>
<div id="studyGroupDetails">
    <h2>스터디 그룹 이름: <span id="studyGroupName"></span></h2>
    <p>스터디 주제: <span id="studyTopic"></span></p>
    <!-- 여기에 더 많은 스터디 그룹 정보를 표시할 수 있습니다 -->
</div>

<button id="requestJoinStudyGroup">스터디 그룹 가입 요청</button>


<div id="articleData" data-article-id="<%= article.getId() %>"></div>


<div id="leaderMessage" style="display: none;">해당 게시글의 스터디 그룹을 생성한 유저입니다.</div>


<!-- 댓글 작성 부분 -->
<div id="comment-write">
    <input type="text" id="contents" placeholder="내용">
    <button id="comment-write-btn" onclick="commentWrite()">댓글작성</button>
</div>

<!-- 댓글 목록 출력 부분 수정 -->
<div id="comment-list">
    <table>
        <thead>
        <tr>
            <th>댓글번호</th>
            <th>작성자</th>
            <th>내용</th>
            <th>작성시간</th>
            <th>작업</th>
        </tr>
        </thead>
        <tbody>
        <!-- 서버로부터 받은 댓글 데이터를 동적으로 삽입합니다. -->
        </tbody>
    </table>
</div>




</body>
</html>
