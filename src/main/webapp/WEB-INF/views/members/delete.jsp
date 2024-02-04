<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>회원 탈퇴</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }

        .container {
            max-width: 400px;
            margin: 50px auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        form {
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        label {
            font-weight: bold;
            margin-bottom: 10px;
        }

        .form-floating {
            position: relative;
            margin-bottom: 15px;
        }

        input {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
            margin-bottom: 5px;
            box-sizing: border-box;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        button {
            width: 100%;
            padding: 10px;
            color: #fff;
            background-color: #007bff;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        button:hover {
            background-color: #0056b3;
        }

        .has-danger input {
            border-color: #dc3545;
        }

        .invalid-feedback {
            color: #dc3545;
            margin-top: 5px;
        }
    </style>

</head>
<body>
<div class="container">
    <form action="/api/v1/member/delete" method="post">
        <label class="form-label mt-4">회원 탈퇴</label>

        <c:choose>
            <c:when test="${empty message}">
                <div class="form-floating mb-3">
                    <input type="password" name="password" class="form-control" id="password" required>
                    <label for="password">비밀번호</label>
                </div>
            </c:when>
            <c:otherwise>
                <div class="form-floating mb-3 has-danger">
                    <input type="password" name="password" class="form-control is-invalid" id="passwordError" required>
                    <label for="passwordError">비밀번호</label>
                    <div class="invalid-feedback">${message}</div>
                </div>
            </c:otherwise>
        </c:choose>

        <button type="submit" class="btn btn-primary">회원 탈퇴</button>
    </form>
</div>
</body>
</html>