<%@ page import="dev.linkcentral.service.dto.profile.ProfileDetailsDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile/edit.css">
    <title>프로필 수정</title>

    <script>
        $(document).ready(function () {
            $('form').submit(function (event) {
                event.preventDefault(); // 기본 폼 제출 방지

                var formData = new FormData(this);
                const token = localStorage.getItem('jwt');

                $.ajax({
                    url: '/api/v1/profile/update',
                    type: 'POST',
                    data: formData,
                    contentType: false,
                    processData: false,
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                    },
                    success: function(response) {
                        console.log("응답 메시지: " + response.message);
                        var memberId = response.memberId;
                        if (memberId) {
                            window.location.href = "/api/v1/view/profile/view?memberId=" + memberId;
                        } else {
                            console.error("memberId가 유효하지 않습니다.");
                        }
                    },
                    error: function (xhr, status, error) {
                        console.error('실패:', error);
                        Swal.fire('오류', '프로필 업데이트에 실패했습니다.', 'error');
                    }
                });
            });

            $('#image').change(function(event) {
                var input = event.target;
                if (input.files && input.files[0]) {
                    var reader = new FileReader();
                    reader.onload = function(e) {
                        $('#imagePreview').attr('src', e.target.result).show();
                    };
                    reader.readAsDataURL(input.files[0]);
                }
            });
        });
    </script>

    <script>
        function goBack() {
            window.history.back();
        }
    </script>
</head>
<body>
<div class="form-container">
    <h2>프로필 수정</h2>
    <form action="/api/v1/profile/update" method="post" enctype="multipart/form-data">
        <input type="hidden" name="memberId" value="${profile.memberId}">

        <label for="image">프로필 사진:</label>
        <div class="file-input-wrapper">
            <button type="button" class="file-input-button">파일 선택</button>
            <input type="file" id="image" name="image" class="file-input">
        </div>
        <img id="imagePreview" src="#" alt="Image Preview" />

        <label for="bio">짧은 소개:</label>
        <input type="text" id="bio" name="bio" value="${profile.bio}">

        <div class="form-actions">
            <button type="button" class="menu-button" onclick="goBack()">이전으로</button>
            <input type="submit" value="저장하기">
        </div>
    </form>
</div>
</body>
</html>
