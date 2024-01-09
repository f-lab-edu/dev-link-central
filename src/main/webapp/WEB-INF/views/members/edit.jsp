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
            margin: 0;
        }

        .container {
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
            margin-top: 20px;
            width: 80%;
            max-width: 400px;
            margin: auto;
        }

        form {
            text-align: left;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            font-weight: bold;
            display: block;
            margin-bottom: 5px;
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

        .error-message {
            color: #dc3545;
            font-size: 12px;
            margin-top: 5px;
        }
    </style>

    <script>
        $(document).ready(function () {
            init();

            function init() {
                $("#btn-update").on("click", function () {
                    checkCurrentPassword();
                });
            }

            function checkCurrentPassword() {
                let currentPassword = $("#currentPassword").val();

                $(".error-message").remove();

                $.post("/check-current-password", { id: $("#id").val(), password: currentPassword })
                    .done(function (resp) {
                        console.log("Response:", resp); // 응답 로그 추가

                        if (resp.result) {
                            update();
                        } else {
                            let errorMessage = "현재 비밀번호가 일치하지 않거나, 입력하지 않는 문구가 있습니다.";
                            $("#currentPassword").after("<div class='error-message'>" + errorMessage + "</div>");
                        }
                    })
                    .fail(function (error) {
                        console.error("Error checking current password:", error);
                    });
            }

            function update() {
                let data = {
                    id: $("#id").val(),
                    name: $("#name").val(),
                    password: $("#password").val(),
                    nickname: $("#nickname").val()
                };

                console.log("업데이트 데이터:", data);

                // 데이터 객체를 폼 데이터로 변환
                let formData = new FormData();

                // 빈 값 또는 null 값이 아닌 경우에만 FormData에 추가
                Object.keys(data).forEach(key => {
                    if (data[key] !== null && data[key] !== "") {
                        formData.append(key, data[key]);
                    }
                });

                $.ajax({
                    type: "PUT",
                    url: "/edit",
                    data: formData,
                    processData: false,
                    contentType: false
                })
                    .done(function (resp) {
                        alert("회원 수정이 완료되었습니다.");
                        location.href = "/";
                    })
                    .fail(function (error) {
                        let errorMessage = "현재 비밀번호가 일치하지 않습니다.";

                        if ($("#currentPassword + .error-message").length === 0) {
                            $("#currentPassword").after("<div class='error-message'>" + errorMessage + "</div>");
                        }
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
            <input type="text" class="form-control" value="${member.name}" id="name">
        </div>
        <div class="form-group">
            <label for="currentPassword">현재 비밀번호 확인 :</label>
            <input type="password" class="form-control" id="currentPassword" required>
        </div>
        <div class="form-group">
            <label for="password">새 비밀번호 입력 :</label>
            <input type="password" class="form-control" id="password" required>
        </div>
        <div class="form-group">
            <label for="email">이메일 :</label>
            <input type="email" class="form-control" value="${member.email}" id="email" readonly>
        </div>
        <div class="form-group">
            <label for="nickname">닉네임 :</label>
            <input type="text" class="form-control" value="${member.nickname}" id="nickname">
        </div>
    </form>
    <button id="btn-update" class="btn btn-dark">저장하기</button>
</div>
</body>
</html>
