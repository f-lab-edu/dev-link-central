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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/groupfeed/save.css">

    <script>
        $(document).ready(function() {
            const memberId = new URLSearchParams(window.location.search).get('memberId');
            console.log("memberId: " + memberId);

            if (memberId) {
                $.ajax({
                    type: 'GET',
                    url: '/api/v1/group-feed/auth/member-info',
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                    },
                    success: function(data) {
                        $('#memberId').val(memberId);
                        $('#name').val(data.name);
                        $('#writer').val(data.nickname);
                    },
                    error: function(xhr, status, error) {
                        alert('작성자 정보를 가져오는 중 오류가 발생했습니다: ' + xhr.responseText);
                    }
                });
            }

            $('.file-input-button').click(function() {
                $('#image').click();
            });

            $('#image').change(function(event) {
                var input = event.target;
                if (input.files && input.files[0]) {
                    var reader = new FileReader();
                    reader.onload = function(e) {
                        $('#imagePreview').attr('src', e.target.result);
                        $('#imagePreview').show();
                    };
                    reader.readAsDataURL(input.files[0]);
                }
            });

            $('#feedForm').submit(function(event) {
                event.preventDefault();
                var formData = new FormData(this);
                $.ajax({
                    type: 'POST',
                    url: '/api/v1/group-feed',
                    data: formData,
                    processData: false,
                    contentType: false,
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                    },
                    success: function(response) {
                        alert('피드 생성이 완료되었습니다.');
                        window.location.href = '/';
                    },
                    error: function(xhr, status, error) {
                        alert('피드 생성 중 오류가 발생했습니다: ' + xhr.responseText);
                    }
                });
            });
        });

        function home() {
            window.history.back();
        }
    </script>
</head>

<body>
<form id="feedForm" enctype="multipart/form-data">
    <h2>피드 작성</h2>
    <input type="hidden" name="memberId" id="memberId" />

    <div>
        <label for="name">작성자:</label>
        <input type="text" name="writer" id="name" readonly required />
    </div>
    <div>
        <label for="title">제목:</label>
        <input type="text" name="title" id="title" required />
    </div>
    <div>
        <label for="content">내용:</label>
        <textarea name="content" id="content" required></textarea>
    </div>
    <div>
        <label for="image">이미지:</label>
        <input type="file" name="image" id="image" class="file-input" />
        <img id="imagePreview" src="#" alt="Image Preview" style="display: none;" />
    </div>
    <div class="form-actions">
        <button type="button" class="file-input-button">이미지 추가</button>
        <button type="button" class="menu-button" onclick="home()">이전으로</button>
        <input type="submit" value="등록하기" />
    </div>
</form>
</body>
</html>
