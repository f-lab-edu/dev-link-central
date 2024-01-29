<%@ page import="dev.linkcentral.service.dto.request.ProfileRequestDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>프로필 수정</title>
</head>
<body>
<form action="/api/v1/profile/update" method="post">

    <input type="hidden" name="memberId" value="${profile.memberId}">

    Bio: <input type="text" name="bio" value="${profile.bio}"><br>
    Image URL: <input type="text" name="imageUrl" value="${profile.imageUrl}"><br>

    <input type="submit" value="저장">

</form>
</body>
</html>