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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/groupfeed/detail.css">

    <script>
        $(document).ready(function() {
            const feedId = $('#feedDetailContainer').data('feed-id');
            if (feedId) {
                loadFeedDetail(feedId);
            }
        });

        function loadFeedDetail(feedId) {
            $.ajax({
                url: '/api/v1/group-feed/' + feedId,
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(feed) {
                    $('.feed-title').html('<strong>제목:</strong> ' + feed.title);
                    $('.feed-content').html('<strong>상세 내용:</strong><br><div class="content-text">' + feed.content + '</div>');
                    if (feed.imageUrl) {
                        $('.feed-image').attr('src', feed.imageUrl).show();
                    } else {
                        $('.feed-image').hide();
                    }
                },
                error: function(xhr) {
                    console.error('Failed to load 피드 상세정보:', xhr.responseText);
                }
            });
        }

        function deleteFeed() {
            const feedId = $('#feedDetailContainer').data('feed-id');
            $.ajax({
                url: '/api/v1/group-feed/' + feedId,
                method: 'DELETE',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function() {
                    alert('피드가 성공적으로 삭제되었습니다.');
                    window.location.href = '/api/v1/view/group-feed/my-feeds';
                },
                error: function(xhr) {
                    console.error('Failed to delete feed:', xhr.responseText);
                }
            });
        }

        function editFeed() {
            const feedId = $('#feedDetailContainer').data('feed-id');
            window.location.href = '/api/v1/view/group-feed/update/' + feedId;
        }

        function home() {
            window.history.back();
        }
    </script>
</head>
<body>
<div class="feed-detail-container" id="feedDetailContainer" data-feed-id="${feedId}">
    <div class="feed-header">
        <h2>피드 상세보기</h2>
        <div class="action-buttons">
            <button class="btn btn-secondary" onclick=home()>이전</button>
            <button class="btn btn-primary" onclick="editFeed()">수정</button>
            <button class="btn btn-danger" onclick="deleteFeed()">삭제</button>
        </div>
    </div>
    <div class="feed-title"></div>
    <img src="" alt="Feed Image" class="feed-image">
    <p class="feed-content"></p>
</div>
</body>
</html>
