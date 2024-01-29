<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>프로필 보기</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .profile-container {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            text-align: center;
        }

        .profile-image {
            width: 150px;
            height: 150px;
            border-radius: 50%;
            margin-bottom: 20px;
        }

        .profile-bio {
            margin-top: 10px;
            font-size: 16px;
            color: #333;
        }

        .edit-button {
            display: block;
            margin-top: 20px;
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }

        .edit-button:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
<div class="profile-container">
    <h1>프로필</h1>

    <%-- 로그인된 사용자의 이름 표시 --%>
    <c:if test="${not empty loggedInUserName}">
        <h2>사용자 이름: ${loginUserName}</h2>
    </c:if>
    <!-- 조건부 이미지 표시 -->
    <img class="profile-image" src="${not empty profile.imageUrl ? profile.imageUrl : '/image.jpg'}" alt="Profile Image">
    <p class="profile-bio">소개: ${profile.bio}</p>

    <!-- 프로필 수정 버튼 -->
    <a href="/api/v1/profile/edit?memberId=${profile.memberId}" class="edit-button">프로필 수정</a>
</div>
</body>
</html>