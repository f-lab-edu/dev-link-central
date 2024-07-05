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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/groupfeed/update.css">

    <script>
        $(document).ready(function() {
            const feedId = $('#feedId').val();
            if (feedId) {
                loadFeedDetail(feedId);
            }

            $('#image').on('change', function() {
                if (this.files && this.files.length > 0) {
                    $('#fileStatus').text('파일 선택 완료');
                } else {
                    $('#fileStatus').text('');
                }
            });
        });

        function loadFeedDetail(feedId) {
            $.ajax({
                url: '/api/v1/group-feed/' + feedId,
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(feed) {
                    $('#title').val(feed.title);
                    $('#content').val(feed.content);
                    // 이미지 파일은 업데이트 폼에서 선택하도록 함
                },
                error: function(xhr) {
                    console.error('Failed to load 피드 상세정보:', xhr.responseText);
                }
            });
        }

        function submitUpdate() {
            const feedId = $('#feedId').val();
            const formData = new FormData($('#updateForm')[0]);

            formData.append("groupFeedUpdateRequest", new Blob([JSON.stringify({
                title: $('#title').val(),
                content: $('#content').val(),
            })], {
                type: "application/json"
            }));

            $.ajax({
                url: '/api/v1/group-feed/' + feedId,
                method: 'PUT',
                data: formData,
                processData: false,
                contentType: false,
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function() {
                    alert('피드가 성공적으로 수정되었습니다.');
                    window.location.href = '/api/v1/view/group-feed/my-feeds';
                },
                error: function(xhr) {
                    console.error('Failed to update feed:', xhr.responseText);
                }
            });
        }

        function home() {
            window.history.back();
        }
    </script>
</head>
<body>
<div class="container">
    <h1>피드 수정</h1>
    <form id="updateForm" enctype="multipart/form-data">
        <input type="hidden" id="feedId" value="${feedId}">
        <div class="form-group">
            <label for="title">제목:</label>
            <input type="text" id="title" name="title" class="form-control" required>
        </div>
        <div class="form-group">
            <label for="content">내용:</label>
            <textarea id="content" name="content" class="form-control" rows="5" required></textarea>
        </div>
        <div class="form-group">
            <label for="image">이미지 파일:</label>
            <div class="file-input-wrapper">
                <input type="file" id="image" name="image" class="file-input">
                <label for="image" class="file-label">이미지 수정</label>
                <span id="fileStatus" class="file-status"></span>
            </div>
        </div>
        <div class="form-actions">
            <button type="button" class="menu-button" onclick="home()">이전으로</button>
            <button type="button" onclick="submitUpdate()">수정하기</button>
        </div>
    </form>
</div>
</body>
</html>
