<%@ page import="dev.linkcentral.service.dto.request.ProfileRequestDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>프로필 수정</title>
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
