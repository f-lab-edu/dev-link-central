<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
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


    <title>회원 탈퇴</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .container {
            background-color: #ffffff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            width: 100%;
            min-height: 300px;
            max-width: 430px;
        }

        .page-header {
            background-color: #007bff;
            color: #ffffff;
            padding: 20px;
            border-radius: 8px 8px 50px 50px;
            text-align: center;
            font-size: 24px;
            box-shadow: 0 2px 5px rgba(0, 0, 255, 0.2);
            margin-bottom: 20px;
            position: relative;
        }

        .menu-button, .btn-primary {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 10px 20px;
            cursor: pointer;
            border-radius: 4px;
            font-size: 16px;
            width: 25%;
        }

        .menu-button:hover, .btn-primary:hover {
            background-color: #0056b3;
        }

        form {
            width: 100%;
        }

        label {
            font-weight: bold;
            margin-top: 10px;
        }

        input[type="password"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 7px;
            border: 1px solid #ced4da;
        }

        .btn-container button {
            width: 80px;
            padding: 12px;
            color: #fff;
            background-color: #007bff;
            border: none;
            border-radius: 7px;
            cursor: pointer;
            margin-left: 13px;
        }

        .invalid-feedback {
            color: #dc3545;
        }

        .btn-container {
            display: flex;
            justify-content: flex-end;
            margin-top: 30px;
        }

        .menu-button:hover, .btn-primary:hover {
            background-color: #0056b3;
        }
    </style>

    <script>
        function deleteMember(event) {
            event.preventDefault();
            var password = $('#password').val();

            $.ajax({
                url: "/api/v1/member?password=" + encodeURIComponent(password),
                type: 'DELETE',
                contentType: 'application/json',
                data: JSON.stringify({password: password}),
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(memberDeleteResponse) {
                    if (memberDeleteResponse.success) {
                        alert(memberDeleteResponse.message);
                        localStorage.removeItem('jwt');
                        // 로그아웃 후 홈페이지로 리디렉션
                        window.location.href = "/";
                    } else {
                        alert(memberDeleteResponse.message);
                    }
                },
                error: function(xhr, status, error) {
                    alert('회원 비밀번호가 일치하지 않습니다.');
                }
            });
        }

        function home() {
            window.location.href = "/api/v1/view/member/";
        }
    </script>
</head>
<body>
<div class="container">
    <form id="deleteForm" onsubmit="deleteMember(event)">
        <div class="page-header">회원 탈퇴</div>
        <label for="password">현재 비밀번호 입력:</label>
        <c:choose>
            <c:when test="${empty message}">
                <div class="form-floating mb-3">
                    <input type="password" name="password" class="form-control" id="password" required>
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
        <div class="btn-container">
            <button type="button" class="menu-button" onclick="home()">이전</button>
            <button type="submit" class="btn btn-primary">탈퇴</button>
        </div>
    </form>
</div>
</body>
</html>
