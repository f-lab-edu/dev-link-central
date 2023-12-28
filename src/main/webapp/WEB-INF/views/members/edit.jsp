<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Your Page Title</title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">

    <!-- Bootstrap JS 및 jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 JS -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>

    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f8f9fa;
            padding-top: 50px; /* Adjust based on your header height */
        }

        .container {
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
            margin-top: 20px;
        }

        form {
            max-width: 400px;
            margin: 0 auto;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            font-weight: bold;
        }

        input {
            width: 100%;
            padding: 10px;
            box-sizing: border-box;
            margin-top: 5px;
            margin-bottom: 10px;
            border: 1px solid #ced4da;
            border-radius: 4px;
            background-color: #fff;
            color: #495057;
        }

        button {
            display: block;
            width: 100%;
            padding: 10px;
            color: #fff;
            background-color: #007bff;
            border: 1px solid #007bff;
            border-radius: 4px;
            cursor: pointer;
        }

        button:hover {
            background-color: #0056b3;
        }
    </style>

    <script>
        $(document).ready(function () {
            // 정보 수정 기능 초기화
            init();

            function init() {
                $("#btn-update").on("click", function () {
                    update();
                });
            }

            function update() {
                let data = {
                    id: $("#id").val(),
                    username: $("#name").val(),
                    password: $("#password").val(),
                    email: $("#email").val(),
                    nickname: $("#nickname").val()
                };

                $.ajax({
                    type: "PUT",
                    url: "/edit",
                    data: JSON.stringify(data),
                    contentType: "application/json; charset=utf-8",
                    dataType: "json"
                }).done(function (resp) {
                    alert("회원수정이 완료되었습니다.");
                    location.href = "/";
                }).fail(function (error) {
                    alert(JSON.stringify(error));
                });
            }
        });
    </script>
</head>
<body>

<div class="container">
    <form id="updateForm">
        <!-- 어떤회원이 수정하는지 모르니깐 hidden값 넣어주기 -->
        <input type="hidden" id="id" value="${member.id}"/>

        <div class="form-group">
            <label for="name">이름 :</label>
            <input type="text" class="form-control" value="${member.name}" id="name" readonly>
        </div>

        <div class="form-group">
            <label for="password">비밀번호 :</label>
            <input type="password" class="form-control" id="password">
        </div>

        <div class="form-group">
            <label for="email">이메일 :</label>
            <input type="email" class="form-control" value="${member.email}" id="email">
        </div>

        <div class="form-group">
            <label for="nickname">닉네임 :</label>
            <input type="text" class="form-control" value="${member.nickname}" id="nickname">
        </div>
    </form>

    <button id="btn-update" class="btn btn-dark" onclick="update()">저장하기</button>
</div>

</body>
</html>