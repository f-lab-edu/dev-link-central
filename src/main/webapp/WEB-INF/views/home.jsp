<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <title>프로젝트!</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            text-align: center;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }

        form {
            margin-top: 20px;
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            width: 300px;
            margin: auto;
        }

        label {
            font-weight: bold;
        }

        input {
            margin-bottom: 10px;
            padding: 8px;
            width: 100%;
            box-sizing: border-box;
        }

        input[type="submit"], .signup-button, .password-reset-button {
            margin-top: 20px;
            padding: 10px 20px;
            font-size: 16px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            color: #fff;
        }

        input[type="submit"] {
            background-color: #4caf50;
        }

        .signup-button {
            background-color: #3498db;
        }

        .password-reset-button {
            background-color: #e74c3c;
        }

        p {
            color: red;
            margin-top: 10px;
        }
    </style>

    <script>
        function studyRecruitmentArticle() {
            window.location.href = "/api/v1/article/save";
        }

        function studyRecruitmentArticleList() {
            window.location.href = "/article/";
        }

        function studyRecruitmentArticleList() {
            window.location.href = "/article/";
        }
    </script>

</head>
<body>
<h2 style="color: #333;">안녕하세요!</h2>
<p>로그인을 해주세요 :)</p>

<form action="/login" method="post">
    <div>
        <label for="email">이메일: </label>
        <input id="email" name="email" type="email" required>
    </div>
    <div>
        <label for="password">패스워드: </label>
        <input id="password" name="password" type="password" required>
    </div>
    <input type="submit" value="로그인하기">
</form>

<p>${loginMessage}</p>

<p>처음이신가요? 회원가입은 여기서 해주세요!</p>
<a class="signup-button" href="/members/join-form">회원가입하기</a>

<!-- 비밀번호 찾기 버튼 추가 -->
<a class="password-reset-button" href="/reset-password">비밀번호 찾기</a>



<button onclick="studyRecruitmentArticle()">스터디 모집 게시판 글등록</button>

<button onclick="studyRecruitmentArticleList()">스터디 모집 게시판 글목록</button>


</body>
</html>