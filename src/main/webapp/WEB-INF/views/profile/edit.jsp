<%@ page import="dev.linkcentral.presentation.dto.request.ProfileRequest" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- jQuery library -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
    <!-- Bootstrap JS 및 jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 JS -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>

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
                            // memberId가 없는 경우, 사용자에게 메시지를 표시하거나 기타 처리를 합니다.
                            console.error("memberId가 유효하지 않습니다.");
                        }
                    },
                    error: function (xhr, status, error) {
                        console.error('실패:', error);
                        // 사용자에게 에러 메시지 표시
                        Swal.fire('오류', '프로필 업데이트에 실패했습니다.', 'error');
                    }
                });
            });
        });
    </script>
</head>
<body>
<h1>프로필 수정</h1>

<form action="/api/v1/profile/update" method="post" enctype="multipart/form-data">
    <input type="hidden" name="memberId" value="${profile.memberId}">

    <label for="bio">Bio:</label>
    <input type="text" id="bio" name="bio" value="${profile.bio}"><br><br>

    <label for="image">Upload Image:</label>
    <input type="file" id="image" name="image"><br><br>

    <input type="submit" value="저장">
</form>
</body>
</html>
