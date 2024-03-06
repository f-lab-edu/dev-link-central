<%@ page import="dev.linkcentral.service.dto.request.ProfileRequestDTO" %>
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
    $(document).ready(function() {
      $('form').submit(function(event) {
        event.preventDefault(); // 폼 기본 제출 막기

        var formData = new FormData(this);
        const token = localStorage.getItem('jwt');

        $.ajax({
          url: '/api/v1/profile/update',
          type: 'POST',
          data: formData,
          contentType: false, // 멀티파트 폼 데이터 사용을 위해 false 설정
          processData: false, // jQuery가 데이터를 처리하지 않도록 설정
          beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', 'Bearer ' + token);
          },
          success: function(response) {
            console.log('성공:', response);
            window.location.href = response.redirectUrl;
          },
          error: function(xhr, status, error) {
            console.error('실패:', error);
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
