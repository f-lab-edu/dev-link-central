<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
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

    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f8f9fa;
            color: #333;
            padding: 20px;
        }

        .form-container {
            background-color: #ffffff;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            padding: 20px;
            width: 100%;
            max-width: 500px;
            margin: auto;
            overflow: hidden;
        }

        .form-title {
            font-size: 22px;
            font-weight: bold;
            color: #333;
            text-align: center;
            margin-bottom: 20px;
        }

        label {
            font-weight: bold;
            color: #333;
            display: block;
            margin-bottom: 5px;
        }

        input[type="text"],
        textarea {
            width: 100%;
            padding: 8px;
            margin-bottom: 10px;
            border-radius: 4px;
            border: 1px solid #ccc;
        }

        button {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 8px 15px;
            text-transform: uppercase;
            border-radius: 4px;
            cursor: pointer;
            display: inline-block;
            margin-left: auto;
        }

        button:hover {
            background-color: #0056b3;
        }

        .button-container {
            text-align: right;
            margin-top: 10px;
        }

        .button-container button {
            margin-left: 10px;
        }
    </style>

    <script>
        function home() {
            window.history.back();
        }
    </script>
</head>
<body>
<div class="form-container">
    <div class="form-title">스터디 모집 글 작성</div>
    <form id="articleForm">
        <label for="writer">작성자:</label>
        <input type="text" id="writer" name="writer" value="${nickname}" readonly><br>

        <label for="title">스터디 제목:</label>
        <input type="text" id="title" name="title"><br>

        <label for="content">스터디 상세 내용 작성:</label>
        <textarea id="content" name="content" cols="30" rows="10"></textarea><br>

        <div class="button-container">
            <button type="button" onclick="home()">이전으로</button>
            <button type="button" id="saveArticleBtn">등록하기</button>
        </div>
    </form>
</div>
</body>
</html>
