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
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/members/edit.css">

    <script type="text/javascript">
        $(document).ready(function() {
            // 사용자 정보를 가져와서 폼에 채움
            $.ajax({
                url: '/api/v1/members',
                type: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(response) {
                    if (response.code === "20000") {
                        $('#name').val(response.body.name);
                        $('#email').val(response.body.email);
                        $('#nickname').val(response.body.nickname);
                        $('#id').val(response.body.userId);
                    } else {
                        alert(response.message || "회원 정보를 가져오지 못했습니다.");
                    }
                },
                error: function(xhr) {
                    alert('사용자 정보를 가져오는 데 실패했습니다.');
                }
            });

            // '현재 비밀번호 확인' 입력 칸에서 입력값을 확인
            $('#currentPassword').on('input', function() {
                var currentPassword = $(this).val();

                if (currentPassword.length > 0) {
                    $.ajax({
                        url: '/api/v1/public/members/check-password',
                        type: 'POST',
                        data: { password: currentPassword },
                        headers: {
                            'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                        },
                        success: function(response) {
                            console.log("Server response:", response);
                            if (response.code === "20000" && response.body === true) {
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

                var id = $('#id').val();
                var name = $('#name').val();
                var nickname = $('#nickname').val();
                var currentPassword = $('#currentPassword').val();
                var newPassword = $('#password').val();

                // 현재 비밀번호 확인
                $.ajax({
                    url: '/api/v1/public/members/check-password',
                    type: 'POST',
                    data: { password: currentPassword },
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                    },
                    success: function(response) {
                        if (response.code === "20000" && response.body === true) {
                            $.ajax({
                                url: '/api/v1/members',
                                type: 'PUT',
                                contentType: 'application/json',
                                data: JSON.stringify({
                                    id: id,
                                    name: name,
                                    nickname: nickname,
                                    currentPassword: currentPassword,
                                    newPassword: newPassword
                                }),
                                headers: {
                                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                                },
                                success: function(response) {
                                    if (response.code === "20000") {
                                        alert('회원 정보가 성공적으로 업데이트되었습니다.');
                                        window.location.href = "/api/v1/view/member/";
                                    } else {
                                        alert('회원 정보 업데이트 실패: ' + response.message);
                                    }
                                },
                                error: function(xhr) {
                                    let msg = "회원 정보 업데이트 실패.";
                                    try {
                                        const res = JSON.parse(xhr.responseText);
                                        if (res && res.message) msg = res.message;
                                    } catch (e) {}
                                    alert(msg);
                                    console.error("회원 정보 업데이트 실패:", xhr.responseText);
                                }
                            });
                        } else {
                            alert('현재 비밀번호가 일치하지 않습니다.');
                        }
                    },
                    error: function(xhr) {
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
