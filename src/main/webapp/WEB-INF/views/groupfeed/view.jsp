<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <!-- Bootstrap CSS -->
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <!-- Popper.js -->
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/umd/popper.min.js"></script>
    <!-- Bootstrap JS -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 CSS and JS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/groupfeed/view.css">

    <script>
        function profileView(memberId) {
            $.ajax({
                type: 'GET',
                url: '/api/v1/group-feed/auth/member-info',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(response) {
                    if (!response.memberId) {
                        alert('회원 ID가 존재하지 않습니다.');
                        return;
                    }
                    const loggedInUserId = response.memberId;
                    if (loggedInUserId === memberId) {
                        const memberId = response.memberId;
                        window.location.href = "/api/v1/view/profile/view?memberId=" + memberId;
                    } else {
                        window.location.href = "/api/v1/view/profile/view?memberId=" + memberId;
                    }
                },
                error: function(xhr, status, error) {
                    alert('사용자 정보를 가져오는 중 오류가 발생했습니다: ' + xhr.responseText);
                }
            });
        }

        let offset = 0;
        const limit = 3;
        let isLoading = false;
        let hasMoreFeeds = true;
        let userId = null;

        function getUserId() {
            return $.ajax({
                type: 'GET',
                url: '/api/v1/group-feed/auth/member-info',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                }
            });
        }

        function loadFeeds() {
            if (isLoading || !hasMoreFeeds) return;

            isLoading = true;
            $('#loadingSpinner').show();

            $.ajax({
                url: '/api/v1/group-feed/user/' + userId,
                method: 'GET',
                data: {
                    offset: offset,
                    limit: limit
                },
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(response) {
                    const { feeds } = response;
                    if (feeds.length > 0) {
                        feeds.forEach(feed => {
                            const profileImageUrl = feed.profileImageUrl ? feed.profileImageUrl + '?t=' + new Date().getTime() : '/images/default.png';
                            const feedHtml =
                                '<div class="feed-item">' +
                                '<div class="writer-info">' +
                                '<div class="writer-details">' +
                                '<img src="' + profileImageUrl + '" alt="Profile Image" class="profile-image" onclick="profileView(' + feed.memberId + ')">' +
                                '<p class="writer-name" onclick="profileView(' + feed.memberId + ')">' + feed.writer + '</p>' +
                                '</div>' +
                                '<h3 class="feed-title">' + feed.title + '</h3>' +
                                '</div>' +
                                (feed.imageUrl ? '<img src="' + feed.imageUrl + '" alt="Feed Image" class="feed-image">' : '') +
                                '<p class="feed-content">' + feed.content + '</p>' +
                                '<div class="feed-created">' +
                                '<button id="like-button-' + feed.id + '" class="btn btn-secondary like-button" onclick="toggleLike(' + feed.id + ')">좋아요 <span id="like-count-' + feed.id + '">' + (feed.likeCount || 0) + '</span></button>' +
                                '<small>' + new Date(feed.createdAt).toLocaleString() + '</small>' +
                                '</div>' +
                                '<div class="comments-section" id="comments-section-' + feed.id + '">' +
                                '<h4>댓글</h4>' +
                                '<div id="comments-' + feed.id + '"></div>' +
                                '<div class="form-group">' +
                                '<textarea id="comment-input-' + feed.id + '" class="form-control" rows="2" placeholder="댓글을 입력하세요"></textarea>' +
                                '<button class="btn btn-primary mt-2" onclick="postComment(' + feed.id + ')">댓글 작성</button>' +
                                '</div>' +
                                '</div>' +
                                '</div>';
                            $('#feedContainer').append(feedHtml);
                            loadComments(feed.id);
                        });
                        offset += limit;
                        if (feeds.length < limit) {
                            hasMoreFeeds = false;
                        }
                    } else {
                        if (offset === 0) {
                            $('#feedContainer').append('<p class="no-feeds-message">작성된 피드 또는 스터디 그룹이 없습니다.</p>');
                        }
                        hasMoreFeeds = false;
                    }
                },
                error: function(xhr) {
                    console.error('Failed to load feeds:', xhr.responseText);
                },
                complete: function() {
                    isLoading = false;
                    $('#loadingSpinner').hide();
                }
            });
        }


        $(document).ready(function() {
            getUserId().done(function(response) {
                userId = response.memberId;
                loadFeeds();
            }).fail(function(xhr, status, error) {
                console.error('사용자 ID를 가져오지 못했습니다.', xhr.responseText);
            });
        });



        function loadComments(feedId) {
            $.ajax({
                url: '/api/v1/group-feed/' + feedId + '/comments',
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(response) {
                    const comments = response.comments;
                    const commentsContainer = $('#comments-' + feedId);
                    commentsContainer.empty();
                    comments.forEach(comment => {
                        const commentHtml =
                            '<div class="comment-item" id="comment-' + comment.id + '">' +
                            '<div class="comment-inline">' +
                            '<p class="comment-writer">' + comment.writerNickname + ':&nbsp;</p>' +
                            '<p class="comment-content" id="comment-content-' + comment.id + '">' + comment.content + '</p>' +
                            '<p class="comment-created" id="comment-created-' + comment.id + '" style="margin-left: auto;"><small>' + new Date(comment.createdAt).toLocaleString() + '</small></p>' +
                            '<div class="comment-actions" id="comment-actions-' + comment.id + '">' +
                            '<a href="#" onclick="editComment(event, ' + comment.id + ', ' + feedId + ')">수정</a>' +
                            '<a href="#" onclick="deleteComment(event, ' + comment.id + ', ' + feedId + ')">삭제</a>' +
                            '</div>' +
                            '</div>' +
                            '</div>';
                        commentsContainer.append(commentHtml);
                    });
                },
                error: function(xhr) {
                    console.error('Failed to load comments:', xhr.responseText);
                }
            });
        }

        function editComment(event, commentId, feedId) {
            event.preventDefault();

            var commentContentElement = $('#comment-content-' + commentId);
            var commentCreatedElement = $('#comment-created-' + commentId);
            var commentActionsElement = $('#comment-actions-' + commentId);

            commentCreatedElement.hide();
            commentActionsElement.hide();

            var currentContent = commentContentElement.text();
            var editHtml =
                '<div class="input-group">' +
                '<textarea id="edit-comment-input-' + commentId + '" class="form-control edit-comment-textarea">' + currentContent + '</textarea>' +
                '<div class="input-group-append">' +
                '<button class="btn btn-primary" onclick="saveComment(' + commentId + ', ' + feedId + ')">저장</button>' +
                '</div>' +
                '</div>';
            commentContentElement.replaceWith('<div id="edit-comment-' + commentId + '">' + editHtml + '</div>');
        }

        function saveComment(commentId, feedId) {
            var newContent = $('#edit-comment-input-' + commentId).val();
            if (newContent) {
                $.ajax({
                    url: '/api/v1/group-feed/' + feedId + '/comments/' + commentId,
                    method: 'PUT',
                    contentType: 'application/json',
                    data: JSON.stringify({ content: newContent }),
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                    },
                    success: function() {
                        loadComments(feedId);
                    },
                    error: function(xhr) {
                        console.error('Failed to edit comment:', xhr.responseText);
                    }
                });
            }
        }

        function deleteComment(event, commentId, feedId) {
            event.preventDefault();

            if (confirm('댓글을 삭제하시겠습니까?')) {
                $.ajax({
                    url: '/api/v1/group-feed/' + feedId + '/comments/' + commentId,
                    method: 'DELETE',
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                    },
                    success: function() {
                        loadComments(feedId);
                    },
                    error: function(xhr) {
                        console.error('Failed to delete comment:', xhr.responseText);
                    }
                });
            }
        }

        function postComment(feedId) {
            const content = $('#comment-input-' + feedId).val();
            if (!content) {
                alert('댓글을 입력하세요.');
                return;
            }

            $.ajax({
                url: '/api/v1/group-feed/' + feedId + '/comments',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ content: content }),
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function() {
                    $('#comment-input-' + feedId).val('');
                    loadComments(feedId);
                },
                error: function(xhr) {
                    console.error('Failed to post comment:', xhr.responseText);
                }
            });
        }

        function toggleLike(feedId) {
            $.ajax({
                url: '/api/v1/group-feed/' + feedId + '/like',
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(response) {
                    $('#like-count-' + feedId).text(response.likeCount);
                },
                error: function(xhr) {
                    console.error('Failed to toggle like:', xhr.responseText);
                }
            });
        }

        $(window).on('scroll', function() {
            if ($(window).scrollTop() + $(window).height() >= $(document).height() - 100) {
                loadFeeds();
            }
        });

        function groupFeedSave() {
            $.ajax({
                type: "GET",
                url: "/api/v1/group-feed/auth/member-info",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function (response) {
                    var memberId = response.memberId;
                    if (!memberId) {
                        alert('회원 ID가 존재하지 않습니다.');
                        return;
                    }
                    window.location.href = "/api/v1/view/group-feed/create?memberId=" + memberId;
                },
                error: function (xhr) {
                    alert("회원 정보를 가져올 수 없습니다: " + xhr.responseText);
                }
            });
        }

        function home() {
            window.location.href = "/api/v1/view/member/";
        }

        function studyRecruitmentArticlePaging() {
            window.location.href = "/api/v1/view/article/paging";
        }
    </script>
</head>
<body>
<div class="title-container">
    <span class="site-title">dev-link-central</span>
</div>

<div class="title">
    <div class="button-container">
        <button class="menu-button" onclick="home()">메뉴</button>
        <button class="group-feed-create-button" onclick="studyRecruitmentArticlePaging()">스터디 참여</button>
        <button class="group-feed-create-button" onclick="groupFeedSave()">글작성</button>
    </div>
</div>
<div class="feed-container" id="feedContainer">
    <c:forEach items="${feeds}" var="feed">
        <div class="feed-item">
            <div class="writer-info">
                <div class="writer-details">
                    <c:choose>
                        <c:when test="${not empty feed.profileImageUrl}">
                            <img src="${feed.profileImageUrl}?t=${System.currentTimeMillis()}" alt="Profile Image" class="profile-image" onclick="profileView(${feed.writerId})">
                        </c:when>
                        <c:otherwise>
                            <img src="/images/default.png" alt="Default Profile Image" class="profile-image" onclick="profileView(${feed.writerId})">
                        </c:otherwise>
                    </c:choose>
                    <p class="writer-name" onclick="profileView(${feed.writerId})">${feed.writer}</p>
                </div>
                <h2 class="feed-title">${feed.title}</h2>
            </div>
            <p class="feed-content">${feed.content}</p>
            <c:choose>
                <c:when test="${not empty feed.imageUrl}">
                    <img src="${feed.imageUrl}" alt="Feed Image" class="feed-image">
                </c:when>
                <c:otherwise>
                    <!-- No image available -->
                </c:otherwise>
            </c:choose>
            <p class="feed-created">
                <small>작성자: ${feed.writer}</small>
                <button id="like-button-${feed.id}" class="btn btn-secondary like-button" onclick="toggleLike(${feed.id})">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                        <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                    </svg>
                    좋아요 <span id="like-count-${feed.id}">${feed.likeCount || 0}</span>
                </button>
            </p>
            <div class="comments-section" id="comments-section-${feed.id}">
                <h4>댓글</h4>
                <div id="comments-${feed.id}"></div>
                <div class="form-group">
                    <textarea id="comment-input-${feed.id}" class="form-control" rows="2" placeholder="댓글을 입력하세요"></textarea>
                    <button class="btn btn-primary">
                        <span>댓글 작성</span>
                    </button>
                </div>
            </div>
        </div>
    </c:forEach>
</div>
<div class="loading-spinner" id="loadingSpinner" style="display: none;">
    <div class="spinner-border text-primary" role="status">
        <span class="sr-only">Loading...</span>
    </div>
</div>
</body>
</html>
