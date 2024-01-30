<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>프로필 보기</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
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
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            text-align: center;
            width: 50%;
            max-width: 600px;
        }

        .profile-image {
            width: 200px;
            height: 200px;
            border-radius: 50%;
            margin-bottom: 30px;
            border: 3px solid #ddd;
        }

        .profile-bio {
            margin-top: 20px;
            font-size: 18px;
            color: #333;
            line-height: 1.6;
        }

        .edit-button {
            display: block;
            margin-top: 30px;
            padding: 15px 30px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-size: 18px;
            font-weight: bold;
        }

        .edit-button:hover {
            background-color: #45a049;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
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
    <img class="profile-image" src="${not empty profile.imageUrl ? profile.imageUrl : '/images/default.png'}" alt="Profile Image">

    <p class="profile-bio">소개: ${profile.bio}</p>

    <!-- 프로필 수정 버튼 -->
    <a href="/api/v1/profile/edit?memberId=${profile.memberId}" class="edit-button">프로필 수정</a>
</div>
</body>
</html>
