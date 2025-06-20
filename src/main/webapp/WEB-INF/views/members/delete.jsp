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
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/members/delete.css">

    <title>회원 탈퇴</title>
    <script>
        function deleteMember(event) {
            event.preventDefault();
            var password = $('#password').val();

            $.ajax({
                url: "/api/v1/members",
                type: 'DELETE',
                contentType: 'application/json',
                data: JSON.stringify({password: password}),
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(response) {
                    if (response.code === "20000") {
                        alert('회원 탈퇴가 완료되었습니다.');
                        localStorage.removeItem('jwt');
                        window.location.href = "/";
                    } else {
                        alert('회원 탈퇴 실패: ' + (response.message || "처리 중 오류가 발생했습니다."));
                    }
                },
                error: function() {
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
