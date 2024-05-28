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

    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f4f4f9;
            color: #333;
            padding: 20px;
        }

        .feed-container {
            max-width: 700px;
            margin: 0 auto;
        }

        .feed-item {
            background: #fff;
            padding: 20px;
            margin-bottom: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            max-width: 680px;
            margin: 0 auto 20px;
        }

        .feed-item img.feed-image {
            width: 90%;
            height: 260px;
            object-fit: cover;
            border-radius: 20px;
            display: block;
            margin: 20px auto;
        }

        .profile-image {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            margin-right: 10px;
            cursor: pointer;
        }

        .writer-info {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
            justify-content: space-between;
        }

        .feed-content {
            margin-right: 20px;
        }

        .writer-details {
            display: flex;
            align-items: center;
        }

        .writer-name {
            font-size: 1.3em;
            margin-top: 15px;
            font-weight: normal;
            cursor: pointer;
            transition: color 0.3s;
        }

        .writer-name:hover {
            color: #007bff;
        }

        .feed-title {
            font-size: 1.5em;
            margin-left: 150px;
            text-align: left;
            flex-grow: 1;
            font-weight: bold;
        }

        .loading-spinner {
            text-align: center;
            margin-top: 20px;
        }

        .title {
            text-align: center;
            margin-bottom: 20px;
            font-size: 27px;
            font-weight: bold;
            color: #007bff;
        }

        .feed-content {
            margin-top: 20px;
            padding-left: 35px;
        }

        .feed-created {
            text-align: right;
            margin-top: 10px;
        }
    </style>

    <script>
        function profileView() {
            $.ajax({
                type: 'GET',
                url: '/api/v1/group-feed/auth/member-info',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function (response) {
                    if (response.memberId === null || response.memberId === '') {
                        alert('회원 ID가 존재하지 않습니다.');
                        return;
                    }
                    const memberId  = response.memberId;
                    window.location.href = "/api/v1/view/profile/view?memberId=" + memberId;
                },
                error: function (xhr, status, error) {
                    alert('작성자 정보를 가져오는 중 오류가 발생했습니다: ' + xhr.responseText);
                }
            });
        }

        let currentPage = 0;
        const pageSize = 3;
        let isLoading = false;
        let hasMoreFeeds = true;

        function loadFeeds() {
            if (isLoading || !hasMoreFeeds) return;

            isLoading = true;
            $('#loadingSpinner').show();

            $.ajax({
                url: '/api/v1/group-feed',
                method: 'GET',
                data: {
                    page: currentPage,
                    size: pageSize
                },
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(response) {
                    const feeds = response.feeds.content;
                    if (feeds.length > 0) {
                        feeds.forEach(feed => {
                            const feedHtml =
                                '<div class="feed-item">' +
                                '<div class="writer-info">' +
                                '<div class="writer-details">' +
                                (feed.profileImageUrl ? '<img src="' + feed.profileImageUrl + '" alt="Profile Image" class="profile-image" onclick="profileView(' + feed.writerId + ')">' : '') +
                                '<p class="writer-name" onclick="profileView(' + feed.writerId + ')">' + feed.writer + '</p>' +
                                '</div>' +
                                '<h3 class="feed-title">' + feed.title + '</h3>' +
                                '</div>' +
                                (feed.imageUrl ? '<img src="' + feed.imageUrl + '" alt="Feed Image" class="feed-image">' : '') +
                                '<p class="feed-content">' + feed.content + '</p>' +
                                '<p class="feed-created"><small>' + new Date(feed.createdAt).toLocaleString() + '</small></p>' +
                                '</div>';
                            $('#feedContainer').append(feedHtml);
                        });
                        currentPage++;
                        if (feeds.length < pageSize) {
                            hasMoreFeeds = false;
                        }
                    } else {
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

        $(window).on('scroll', function() {
            if ($(window).scrollTop() + $(window).height() >= $(document).height() - 100) {
                loadFeeds();
            }
        });

        $(document).ready(function() {
            loadFeeds();
        });
    </script>
</head>
<body>
<div class="title">그룹 피드</div>
<div class="feed-container" id="feedContainer">
    <c:forEach items="${feeds}" var="feed">
        <div class="feed-item">
            <div class="writer-info">
                <div class="writer-details">
                    <c:if test="${not empty feed.profileImageUrl}">
                        <img src="${feed.profileImageUrl}" alt="Profile Image" class="profile-image" onclick="profileView(${feed.writerId})">
                    </c:if>
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
            <p><small>작성자: ${feed.writer}</small></p>
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
