<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <title>나만의 게시판 만들기!</title>
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

        input[type="submit"] {
            background-color: #4caf50;
            color: #fff;
            cursor: pointer;
        }

        p {
            color: red;
            margin-top: 10px;
        }

        .signup-button {
            margin-top: 20px;
            background-color: #3498db;
            color: #fff;
            padding: 10px 20px;
            font-size: 16px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
    </style>
</head>
<body>
<h2 style="color: #333;">안녕하세요!</h2>
<p>로그인을 해주세요 :)</p>

<form action="/login" method="post">
    <div>
        <label for="userId">아이디: </label>
        <input id="userId" name="name" type="text" required>
    </div>
    <div>
        <label for="password">패스워드: </label>
        <input id="password" name="password" type="password" required>
    </div>
    <input type="submit" value="로그인하기">
</form>

<p>${loginMessage}</p>

<p>처음이신가요? 회원가입은 여기서 해주세요!</p>
<input class="signup-button" type="button" onclick="location.href='/members/join-form';" value="회원가입하기">
</body>
</html>