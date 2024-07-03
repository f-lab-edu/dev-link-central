<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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

    <title>dev-link-central</title>
    <style>
        body {
            background-color: #f4f4f4;
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh;
            margin: 0;
        }

        .form-container {
            background: #ffffff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
            width: 100%;
            max-width: 400px;
        }

        .btn-primary {
            background-color: #007bff;
            border: none;
        }

        .form-control {
            border-radius: 5px;
            border: 1px solid #ccc;
        }

        .form-label {
            font-weight: bold;
        }

        .link-button {
            text-decoration: none;
            color: #007bff;
            cursor: pointer;
        }
    </style>

    <script>
        $(document).ready(function () {

            // JWT 토큰이 있는지 확인
            const jwtToken = localStorage.getItem("jwt");
            if (jwtToken) {
                // JWT 토큰이 있다면 로그인 후 페이지로 리디렉션
                window.location.href = "/api/v1/view/member/login";
            }

            $("#loginButton").click(function (e) {
                e.preventDefault(); // 기본 form 제출을 방지

                var email = $("#email").val();
                var password = $("#password").val();

                $.ajax({
                    type: "POST",
                    url: "/api/v1/public/member/login",
                    contentType: "application/json",
                    data: JSON.stringify({email: email, password: password}),

                    success: function (response) {
                        console.log("로그인 응답: ", response);

                        // 'accessToken' 키를 사용하여 토큰을 로컬 스토리지에 저장
                        if (response && response.accessToken) {
                            localStorage.setItem("jwt", response.accessToken);
                            groupFeedView();
                        } else {
                            // 응답에서 'accessToken'을 찾을 수 없는 경우
                            console.error("응답에서 accessToken을 찾을 수 없습니다.");
                            alert("로그인 실패: 응답에서 토큰을 찾을 수 없습니다.");
                        }
                    },
                    error: function (xhr) {
                        console.error("로그인 요청 실패: ", xhr);
                        alert("로그인 요청 실패");
                    }
                });
            });
        });

        function groupFeedView() {
            $.ajax({
                type: "GET",
                url: "/api/v1/group-feed/auth/member-info",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function (response) {
                    var memberId = response.memberId;
                    if (!memberId) {
                        alert('회원 ID가 존재하지 않습니다.');
                        return;
                    }
                    window.location.href = "/api/v1/view/group-feed/list?memberId=" + memberId;
                },
                error: function (xhr) {
                    alert("회원 정보를 가져올 수 없습니다: " + xhr.responseText);
                }
            });
        }
    </script>

</head>
<body>
<div class="form-container">
    <h2 class="text-center mb-4">Login</h2>
    <form id="loginForm">
        <div class="form-group">
            <label for="email" class="form-label">이메일:</label>
            <input type="email" class="form-control" id="email" name="email" required>
        </div>
        <div class="form-group">
            <label for="password" class="form-label">패스워드:</label>
            <input type="password" class="form-control" id="password" name="password" required>
        </div>
        <button type="submit" class="btn btn-primary btn-block" id="loginButton">로그인하기</button>
    </form>
    <p class="text-center mt-4">
        처음이신가요? <a href="/api/v1/view/member/join-form" class="link-button">회원가입하기</a>
    </p>
    <p class="text-center">
        <a href="/api/v1/view/member/reset-password" class="link-button">비밀번호 찾기</a>
    </p>
</div>
</body>
</html>