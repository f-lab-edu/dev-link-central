<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>프로젝트!</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }

        header {
            background-color: #333;
            color: white;
            padding: 10px;
            text-align: center;
        }

        .container {
            margin: 20px;
        }

        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 15px;
            margin: 5px;
            border: none;
            border-radius: 3px;
            cursor: pointer;
        }

        button:hover {
            background-color: #45a049;
        }

        p {
            font-size: 18px;
        }
    </style>


    <script>
        function logout() {
            alert("로그아웃 되었습니다.");
            window.location.href = '/logout';
        }

        function editProfile() {
            window.location.href = '/edit-form';
        }

        function deletePage() {
            window.location.href = "/api/delete-page";
        }

        function studyRecruitmentArticlePaging() {
            window.location.href = "/api/v1/article/paging";
        }
    </script>
</head>
<body>
<header>
    <h1>프로젝트!</h1>
</header>
<div class="container">
    <p><%= request.getAttribute("memberName") %>님 환영합니다!</p>

    <button onclick="logout()">로그아웃</button>

    <button onclick="editProfile()">회원수정</button>

    <button onclick="deletePage()">회원탈퇴</button>

    <button onclick="studyRecruitmentArticlePaging()">스터디 모집 게시판 페이징 목록</button>
</div>
</body>
</html>