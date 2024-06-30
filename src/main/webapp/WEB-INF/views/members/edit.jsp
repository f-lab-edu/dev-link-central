<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
            padding-top: 30px;
            margin: 0;
            display: flex;
            justify-content: center;
            align-items: flex-start;
            height: 100vh;
        }

        .container {
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
            width: 100%;
            max-width: 400px;
            margin-top: 40px;
        }

        form {
            display: flex;
            flex-direction: column;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            font-weight: bold;
            margin-bottom: 5px;
            display: block;
        }

        input {
            width: calc(100% - 20px);
            padding: 8px;
            box-sizing: border-box;
            margin-top: 5px;
            margin-bottom: 10px;
            border: 1px solid #ced4da;
            border-radius: 4px;
            background-color: #fff;
            color: #495057;
        }

        .password-feedback {
            font-size: 0.9em;
            margin-bottom: 10px;
        }

        .password-feedback.valid {
            color: green;
        }

        .password-feedback.invalid {
            color: red;
        }

        .btn-container button {
            width: 75px;
            padding: 10px;
            color: #fff;
            background-color: #007bff;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            margin-left: 10px;
        }

        .btn-container {
            display: flex;
            justify-content: flex-end;
        }

        .btn-container button:first-child {
            margin-left: 0;
        }

        .btn-container button:hover {
            background-color: #0056b3;
        }
    </style>

    <script type="text/javascript">
        $(document).ready(function() {
            // 사용자 정보를 가져와서 폼에 채움
            $.ajax({
                url: '/api/v1/member/info',
                type: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(response) {
                    console.log("Member info response:", response);  // 응답 확인
                    $('#name').val(response.name);
                    $('#email').val(response.email);
                    $('#nickname').val(response.nickname);
                    $('#id').val(response.userId);  // userId를 id로 매핑
                },
                error: function(xhr, status, error) {
                    console.error("사용자 정보를 가져오는 데 실패했습니다:", xhr.responseText);
                    alert('사용자 정보를 가져오는 데 실패했습니다.');
                }
            });

            // '현재 비밀번호 확인' 입력 칸에서 입력값을 확인
            $('#currentPassword').on('input', function() {
                var currentPassword = $(this).val();

                if (currentPassword.length > 0) {
                    $.ajax({
                        url: '/api/v1/public/member/check-current-password',
                        type: 'POST',
                        data: { password: currentPassword },
                        headers: {
                            'Authorization': 'Bearer ' + localStorage.getItem("jwt") // 인증 헤더 추가
                        },
                        success: function(response) {
                            console.log("Server response:", response);
                            if (response.result) {
                                $('#passwordFeedback').text('비밀번호가 일치합니다.').removeClass('invalid').addClass('valid');
                            } else {
                                $('#passwordFeedback').text('비밀번호가 일치하지 않습니다.').removeClass('valid').addClass('invalid');
                            }
                        },
                        error: function(xhr, status, error) {
                            console.error("비밀번호 확인 실패:", xhr.responseText);
                        }
                    });
                } else {
                    $('#passwordFeedback').text('').removeClass('valid invalid');
                }
            });

            $('#updateForm').on('submit', function(e) {
                e.preventDefault();

                var currentPassword = $('#currentPassword').val();
                var newPassword = $('#password').val();
                var nickname = $('#nickname').val();
                var id = $('#id').val();  // id 추가
                var name = $('#name').val();  // name 추가

                console.log("Update request data:", {
                    id: id,
                    name: name,
                    nickname: nickname,
                    password: newPassword
                });

                // 현재 비밀번호 확인
                $.ajax({
                    url: '/api/v1/public/member/check-current-password',
                    type: 'POST',
                    data: { password: currentPassword },
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("jwt") // 인증 헤더 추가
                    },
                    success: function(response) {
                        if (response.result) {
                            // 현재 비밀번호가 일치하는 경우, 회원 정보를 업데이트
                            $.ajax({
                                url: '/api/v1/member',
                                type: 'PUT',
                                contentType: 'application/json',
                                data: JSON.stringify({
                                    id: id,
                                    name: name,
                                    nickname: nickname,
                                    password: newPassword
                                }),
                                headers: {
                                    'Authorization': 'Bearer ' + localStorage.getItem("jwt") // 인증 헤더 추가
                                },
                                success: function(response) {
                                    alert('회원 정보가 성공적으로 업데이트되었습니다.');
                                    location.reload(); // 페이지 새로고침
                                },
                                error: function(xhr, status, error) {
                                    console.error("회원 정보 업데이트 실패:", xhr.responseText);
                                    alert('회원 정보 업데이트 실패.');
                                }
                            });
                        } else {
                            // 현재 비밀번호가 일치하지 않는 경우
                            alert('현재 비밀번호가 일치하지 않습니다.');
                        }
                    },
                    error: function(xhr, status, error) {
                        console.error("비밀번호 확인 실패:", xhr.responseText);
                    }
                });
            });
        });

        function home() {
            window.location.href = "/api/v1/view/member/";
        }
    </script>
</head>
<body>
<div class="container">
    <form id="updateForm">
        <input type="hidden" id="id" value="${member.id}"/>
        <div class="form-group">
            <label for="name">이름 :</label>
            <input type="text" class="form-control" value="${member.name}" id="name" readonly autocomplete="name">
        </div>
        <div class="form-group">
            <label for="email">이메일 :</label>
            <input type="email" class="form-control" value="${member.email}" id="email" readonly autocomplete="email">
        </div>
        <div class="form-group">
            <label for="nickname">닉네임 :</label>
            <input type="text" class="form-control" value="${member.nickname}" id="nickname" autocomplete="username">
        </div>
        <div class="form-group">
            <label for="currentPassword">현재 비밀번호 확인 :</label>
            <input type="password" class="form-control" id="currentPassword" required autocomplete="current-password">
            <div id="passwordFeedback" class="password-feedback"></div>
        </div>
        <div class="form-group">
            <label for="password">새 비밀번호 입력 :</label>
            <input type="password" class="form-control" id="password" required autocomplete="new-password">
        </div>
        <div class="btn-container">
            <button type="button" onclick="home()">이전</button>
            <button id="btn-update" class="btn btn-primary">저장</button>
        </div>
    </form>
</div>
</body>
</html>
