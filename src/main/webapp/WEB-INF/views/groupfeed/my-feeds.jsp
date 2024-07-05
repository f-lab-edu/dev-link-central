<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Your Page Title</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <!-- Bootstrap JS 및 jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"
            integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU="
            crossorigin="anonymous">
    </script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 JS -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/groupfeed/my-feeds.css">

    <script>
        $(document).ready(function() {
            loadMyFeeds();
        });

        function loadMyFeeds() {
            $('#feedContainer').empty(); // 기존 피드 지우기
            $.ajax({
                url: '/api/v1/group-feed/my-feeds',
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(response) {
                    const feeds = response.feeds;
                    if (feeds.length > 0) {
                        feeds.forEach(feed => {
                            const feedHtml =
                                '<div class="feed-item" onclick="viewFeedDetail(' + feed.id + ')">' +
                                '<div class="feed-title">' + feed.title + '</div>' +
                                '</div>';
                            $('#feedContainer').append(feedHtml);
                        });
                    } else {
                        $('#feedContainer').append('<p>피드가 없습니다.</p>');
                    }
                },
                error: function(xhr) {
                    console.error('Failed to load feeds:', xhr.responseText);
                }
            });
        }

        function viewFeedDetail(feedId) {
            window.location.href = "/api/v1/view/group-feed/detail/" + feedId;
        }

        function home() {
            window.location.href = "/api/v1/view/member/";
        }
    </script>
</head>
<body>
<div class="container">
    <div class="title-container">
        <div class="title">나의 피드</div>
        <button class="menu-button" onclick="home()">나가기</button>
    </div>
    <div class="feed-container" id="feedContainer"></div>
    <div class="loading-spinner" id="loadingSpinner" style="display: none;">
        <div class="spinner-border text-primary" role="status">
            <span class="sr-only">Loading...</span>
        </div>
    </div>
</div>
</body>
</html>
