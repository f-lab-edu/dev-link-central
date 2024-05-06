<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- jQuery library -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">

    <!-- Bootstrap JS 및 jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 JS -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>

    <title>프로젝트!</title>
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

        input[type="submit"], .signup-button, .password-reset-button {
            margin-top: 20px;
            padding: 10px 20px;
            font-size: 16px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            color: #fff;
        }

        input[type="submit"] {
            background-color: #4caf50;
        }

        .signup-button {
            background-color: #3498db;
        }

        .password-reset-button {
            background-color: #e74c3c;
        }

        p {
            color: red;
            margin-top: 10px;
        }
    </style>

    <script>
        function studyRecruitmentArticlePaging() {
            window.location.href = "/api/v1/view/article/paging";
        }

        $(document).ready(function () {
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
                            window.location.href = response.redirectUrl
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
    </script>


</head>
<body>
<h2 style="color: #333;">안녕하세요!</h2>
<p>로그인을 해주세요 :)</p>

<form id="loginForm">
    <div>
        <label for="email">이메일: </label>
        <input id="email" name="email" type="email" required>
    </div>
    <div>
        <label for="password">패스워드: </label>
        <input id="password" name="password" type="password" required>
    </div>
    <input type="submit" value="로그인하기" id="loginButton">
</form>

<p>${loginMessage}</p>

<p>처음이신가요? 회원가입은 여기서 해주세요!</p>
<a class="signup-button" href="/api/v1/view/member/join-form">회원가입하기</a>

<!-- 비밀번호 찾기 버튼 추가 -->
<a class="password-reset-button" href="/api/v1/view/member/reset-password">비밀번호 찾기</a>

<button onclick="studyRecruitmentArticle()">스터디 모집 게시판 글등록</button>

<button onclick="studyRecruitmentArticlePaging()">스터디 모집 게시판 페이징목록</button>
</body>
</html>
