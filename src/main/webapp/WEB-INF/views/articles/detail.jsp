<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dev.linkcentral.service.dto.article.ArticleViewDTO" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Bootstrap CSS -->
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <!-- Popper.js -->
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/umd/popper.min.js"></script>
    <!-- Bootstrap JS -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 CSS and JS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/articles/detail.css">

    <script>
        <% ArticleViewDTO article = (ArticleViewDTO) request.getAttribute("article"); %>

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
            console.log("Editing comment ID:", commentId);
            var commentRow = $("#comment-row-" + commentId);
            if (commentRow.length === 0) {
                console.error("No comment row found for ID:", commentId);
                return;
            }
            var commentContent = $("#comment-content-" + commentId).text();  // 기존 댓글 내용 가져오기

            // "닉네임: 댓글내용" 형식에서 "댓글내용"만 추출
            var contentOnly = commentContent.split(":").slice(1).join(":").trim();

            var editHtml = "<td colspan='2'><input type='text' class='edit-input' id='edit-content-" + commentId + "' value='" + escapeHtml(contentOnly) + "'>";
            editHtml += "<button class='btn-save' onclick='submitCommentEdit(" + commentId + ")'>저장</button></td>";
            commentRow.html(editHtml);
        }

        // HTML 인코딩 함수 추가
        function escapeHtml(text) {
            var map = {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                '"': '&quot;',
                "'": '&#039;'
            };
            return text.replace(/[&<>"']/g, function(m) { return map[m]; });
        }

        // AJAX를 통해 수정된 댓글 내용을 서버에 전송
        function submitCommentEdit(commentId) {
            var newContent = $("#edit-content-" + commentId).val().trim();
            if (!newContent) {
                alert("댓글 내용을 입력해주세요.");
                return;
            }

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
                    $("#comment-content-" + commentId).text(newContent);
                    $("#edit-modal").modal('hide');
                    window.location.reload();
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
                // success: function(likesCount) {
                success: function(response) {
                    // $('#likesCount').text(likesCount);
                    $('#likesCount').text(response.likesCount);

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
                url: "/api/v1/article/" + articleId + "/comments",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify({ contents: contents }),
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") },
                success: function(response) {
                    $('#contents').val(''); // 입력 필드 초기화
                    // 새로운 댓글만 페이지에 추가
                    var newCommentHtml = "<tr>" +
                        "<td>" + response.id + "</td>" +
                        "<td>" + response.writerNickname + "</td>" +
                        "<td>" + response.contents + "</td>" +
                        "<td>" + response.createdAt + "</td>" +
                        "</tr>";
                    $("#comment-list table tbody").prepend(newCommentHtml);
                    window.location.reload();
                },
                error: function(xhr, status, error) {
                    alert('댓글 작성 실패: ' + xhr.responseText);
                }
            });
        }

        // 댓글 목록 로드 AJAX 함수
        $(document).ready(function() {
            var currentPage = 0; // 현재 페이지 번호
            var pageSize = 3; // 한 페이지에 보여줄 댓글 수
            var isFetchingComments = false; // AJAX 요청 중복 방지
            var hasMoreComments = true; // 더 불러올 댓글이 있는지

            function loadComments() {
                if (isFetchingComments || !hasMoreComments) return; // 중복 요청 및 더 이상 불러올 댓글이 없을 때 요청 방지

                isFetchingComments = true;
                $.ajax({
                    url: "/api/v1/article/" + articleId + "/comments",
                    type: "GET",
                    data: {
                        offset: currentPage * pageSize,
                        limit: pageSize
                    },
                    headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") },
                    success: function(response) {
                        if (response.comments.length > 0) {
                            var commentsHtml = response.comments.map(function(comment) {
                                return "<tr id='comment-row-" + comment.id + "'>" +
                                    "<td id='comment-content-" + comment.id + "'><span class='bold'>" + comment.nickname + "</span>: " + comment.contents + "</td>" +
                                    "<td class='right-align'>" + comment.createdAt +
                                    " <button onclick='editComment(" + comment.id + ")'>수정</button>" +
                                    " <button onclick='deleteComment(" + comment.id + ")'>삭제</button></td>" +
                                    "</tr>";
                            }).join('');
                            $("#comment-list table tbody").append(commentsHtml);
                            currentPage++; // 페이지 번호 증가

                            // 서버로부터 받은 hasMoreComments 값을 기반으로 메시지 표시 여부 결정
                            if (response.hasMoreComments) {
                                $("#moreCommentsIndicator").show();
                            } else {
                                $("#moreCommentsIndicator").hide();
                            }
                        } else {
                            hasMoreComments = false; // 더 불러올 댓글이 없음
                            $("#moreCommentsIndicator").hide();
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
                if ($(window).scrollTop() + $(window).height() > $(document).height() - 100) {
                    loadComments(); // 댓글 로드 함수 호출
                }
            });

            loadComments(); // 초기 댓글 로드
        });
    </script>

    <%-- 스터디 그룹 요청 함수 --%>
    <script>
        $(document).ready(function() {
            // 페이지 로드 시 버튼과 메시지를 숨김
            $("#requestJoinStudyGroup").hide();
            $("#noStudyGroupMessage").hide();

            var articleId = $('#articleData').data('articleId');
            var authorId = <%= article.getWriterId() != null ? article.getWriterId() : "null" %>; // 게시글 작성자의 ID

            console.log("초기 상태에서 버튼 숨김");

            // 그룹 존재 여부 확인 함수
            function checkStudyGroupExists(authorId) {
                if (authorId !== "null") {
                    $.ajax({
                        url: "/api/v1/study-group/user/" + authorId + "/group-existence",
                        type: "GET",
                        headers: {
                            'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                        },
                        success: function(response) {
                            console.log("Group existence check response:", response); // 응답 확인 로그

                            // 응답 구조와 존재 여부 확인
                            if (response && response.exists) {
                                console.log("exists property:", response.exists);
                                if (response.exists) {
                                    // 그룹이 존재하는 경우, 가입 요청 버튼을 활성화
                                    $("#requestJoinStudyGroup").show();
                                    $("#requestJoinStudyGroup").data("studyGroupId", response.groupId); // 그룹 ID 설정
                                    console.log("스터디 그룹이 존재함, 버튼 활성화, 스터디 그룹 ID:", response.groupId);
                                } else {
                                    // 그룹이 존재하지 않는 경우, 가입 요청 버튼을 숨기고 메시지를 표시
                                    console.log("스터디 그룹이 존재하지 않음, 버튼 숨김");
                                    $("#requestJoinStudyGroup").hide();
                                    $("#noStudyGroupMessage").show();
                                }
                            } else {
                                // 예상하지 못한 응답 구조인 경우, 버튼을 숨기고 메시지를 표시
                                console.warn("Unexpected response structure:", response);
                                $("#requestJoinStudyGroup").hide();
                                $("#noStudyGroupMessage").show();
                            }
                        },
                        error: function(xhr, status, error) {
                            console.error("스터디 그룹 존재 여부 확인 실패:", status, error);
                            $("#requestJoinStudyGroup").hide(); // 오류 발생 시 버튼 숨김
                            $("#noStudyGroupMessage").show();
                        }
                    });
                } else {
                    console.error("articleAuthorId is null");
                }
            }

            // 스터디 그룹 가입 요청 버튼 클릭 이벤트 처리
            $("#requestJoinStudyGroup").click(function() {
                var studyGroupId = $(this).data("studyGroupId");
                console.log("스터디 그룹 ID:", studyGroupId); // 스터디 그룹 ID 확인 로그
                if (studyGroupId) {
                    $.ajax({
                        url: "/api/v1/study-group/" + studyGroupId + "/join-requests",
                        type: "POST",
                        headers: {
                            'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                        },
                        success: function() {
                            alert("스터디 그룹 가입 요청이 성공적으로 전송되었습니다.");
                        },
                        error: function(xhr, status, error) {
                            alert("스터디 그룹 가입 요청 실패: " + xhr.responseText);
                        }
                    });
                } else {
                    alert("현재, 스터디 그룹이 생성되어 있지 않습니다.");
                }
            });

            // 로그인한 사용자 정보와 게시글 작성자 정보를 비교하여 버튼 표시 제어
            $.ajax({
                type: "GET",
                url: "/api/v1/article/member-info",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function (response) {
                    console.log("memberId: " + response.memberId);

                    var loggedInMemberId = response.memberId; // 로그인한 사용자의 ID
                    var articleAuthorId = <%= article.getWriterId() != null ? article.getWriterId() : "null" %>; // 게시글 작성자의 ID

                    console.log("loggedInMemberId: " + loggedInMemberId);
                    console.log("articleAuthorId: " + articleAuthorId);

                    if (articleAuthorId !== "null") {
                        // 로그인한 사용자 ID와 게시글 작성자 ID 비교
                        if (loggedInMemberId == articleAuthorId) {  // 타입 일치를 위해 == 사용
                            // 게시글 작성자: 목록, 수정, 삭제 버튼 활성화, 가입 요청 버튼 비활성화
                            $("#updateBtn").show();
                            $("#deleteBtn").show();
                            $("#requestJoinStudyGroup").hide();
                            $("#noStudyGroupMessage").hide(); // 메시지도 숨김
                        } else {
                            // 다른 사용자: 목록 버튼만 활성화, 수정, 삭제 버튼 비활성화, 가입 요청 버튼 활성화
                            $("#updateBtn").hide();
                            $("#deleteBtn").hide();
                            checkStudyGroupExists(authorId);
                        }
                    } else {
                        console.error("articleAuthorId is null");
                    }
                },
                error: function (xhr) {
                    alert("회원 정보를 가져올 수 없습니다: " + xhr.responseText);
                }
            });
        });
    </script>
</head>
<body>

<div class="container">
    <div class="header">
        <span class="title">스터디 모집 상세보기</span>
        <div class="button-group">
            <button id="listBtn" onclick="listReq()">목록</button>
            <span id="noStudyGroupMessage" style="display:none; color: red; margin-left: 10px;">
            그룹이 생성되어 있지 않습니다.
            </span>
            <button id="updateBtn" onclick="updateReq()" style="display:none;">수정</button>
            <button id="deleteBtn" onclick="deleteReq()" style="display:none;">삭제</button>
        </div>

        <button id="requestJoinStudyGroup" style="display:none;">스터디 그룹 가입 요청</button>
    </div>
    <div class="detail-grid">
        <div>
            <label for="articleId">글 번호:</label>
            <div class="detail-box" id="articleId"><%= article.getId() %></div>
        </div>
        <div>
            <label for="title">스터디 제목:</label>
            <div class="detail-box" id="title"><%= article.getTitle() %></div>
        </div>
        <div>
            <label for="author">작성자:</label>
            <div class="detail-box" id="author"><%= article.getWriter() %></div>
        </div>
    </div>
    <label for="content">스터디 상세 내용:</label>
    <div class="content-box" id="content"><%= article.getContent() %></div>
    <div class="stats">
        <div class="like">
            <button onclick="toggleLike()">좋아요</button>
            <span id="likesCount">0</span>
        </div>
        <div class="views">
            조회수: <span id="viewsCount"><%= article.getViews() %></span>
        </div>
        <div class="date">
            작성일: <span><%= article.getCreatedAt() %></span>
        </div>
        <div class="data">
            <div id="articleData" data-article-id="<%= article.getId() %>"></div>
        </div>
    </div>

    <hr size="10"/>

    <div class="comment-title">댓글</div>

    <!-- 댓글 목록 출력 부분 -->
    <div id="comment-list" class="comment-list">
        <table>
            <thead>
            <tr>
            </tr>
            </thead>
            <tbody>
            <!-- 서버로부터 받은 댓글 데이터를 동적으로 삽입합니다. -->
            </tbody>
        </table>
    </div>

    <div id="moreCommentsIndicator">
        더 많은 댓글이 있습니다.
    </div>

    <!-- 댓글 작성 부분 -->
    <div id="comment-write" class="comment-write">
        <input type="text" id="contents" placeholder="댓글을 입력하세요..." />
        <button id="comment-write-btn" onclick="commentWrite()">댓글 작성</button>
    </div>
</div>
</body>
</html>
