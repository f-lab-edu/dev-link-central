<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>회원가입</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh;
        }

        .container {
            background-color: #fff;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
            border-radius: 8px;
            width: 300px;
        }

        form {
            display: flex;
            flex-direction: column;
        }

        .form_group {
            margin-bottom: 15px;
        }

        label {
            font-weight: bold;
            margin-bottom: 5px;
            display: block;
        }

        input {
            padding: 8px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            width: 100%;
        }

        button {
            background-color: #4caf50;
            color: white;
            padding: 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        button:hover {
            background-color: #45a049;
        }

        #passwordMatchMessage {
            margin-top: 10px;
            color: green;
        }

        #passwordMismatchMessage {
            margin-top: 10px;
            color: red;
        }
    </style>
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
    <script>
        $(document).ready(function () {
            // 닉네임 중복 체크
            $("#checkNicknameButton").click(function () {
                var nickname = $("#nickname").val();
                if (!nickname) {
                    $("#nicknameError").text("닉네임을 입력해주세요.");
                    return;
                }

                $.ajax({
                    type: "GET",
                    url: "/api/v1/member/" + nickname + "/exists",
                    success: function (result) {
                        // 서버가 true를 반환하면 닉네임이 중복.
                        if (result) {
                            $("#nicknameError").text("이미 사용 중인 닉네임입니다.");
                            $("#signupButton").prop("disabled", true);
                        } else {
                            $("#nicknameError").text("사용 가능한 닉네임입니다.");
                            $("#signupButton").prop("disabled", false);
                        }
                    },
                    error: function (xhr) {
                        var errorMessage = xhr.status === 400 ? xhr.responseText : "오류가 발생했습니다.";
                        $("#nicknameError").text(errorMessage);
                    }
                });
            });

            // 회원가입
            $("#signupButton").click(function (e) {
                e.preventDefault();
                if (validatePassword()) {
                    var formData = {
                        name: $("#name").val(),
                        password: $("#password").val(),
                        email: $("#email").val(),
                        nickname: $("#nickname").val()
                    };

                    $.ajax({
                        type: "POST",
                        url: "/api/v1/member/register",
                        data: formData,
                        success: function () {
                            alert("회원가입 성공");
                            window.location.href = '/';
                        },
                        error: function () {
                            alert("회원가입 실패");
                        }
                    });
                }
            });
        });

        function validatePassword() {
            var password = document.getElementById("password").value;
            var confirmPassword = document.getElementById("checkPassword").value;

            var matchMessage = document.getElementById("passwordMatchMessage");
            var mismatchMessage = document.getElementById("passwordMismatchMessage");

            if (password === confirmPassword) {
                matchMessage.innerHTML = "비밀번호가 일치합니다.";
                mismatchMessage.innerHTML = "";
                return true;
            } else {
                matchMessage.innerHTML = "";
                mismatchMessage.innerHTML = "비밀번호가 일치하지 않습니다.";
                return false;
            }
        }
    </script>


</head>
<body>
<div class="container">
    <form id="signupForm">
        <div class="form_group">
            <label for="email">이메일</label>
            <input type="email" id="email" name="email" placeholder="이메일을 입력하세요.">

            <label for="name">이름</label>
            <input type="text" id="name" name="name" placeholder="이름을 입력하세요.">

            <label for="password">비밀번호</label>
            <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요.">

            <label for="checkPassword">비밀번호 확인</label>
            <input type="password" id="checkPassword" name="confirmPassword" placeholder="비밀번호를 다시 입력하세요.">
            <div id="passwordMatchMessage"></div>
            <div id="passwordMismatchMessage"></div>

            <label for="nickname">닉네임</label>
            <input type="text" id="nickname" name="nickname" placeholder="닉네임을 입력하세요.">
            <span id="nicknameError" style="color: red;"></span>
            <span id="nicknameStatus" style="color: green;"></span>

            <button type="button" id="checkNicknameButton">닉네임 중복 확인</button>
            <button type="submit" id="signupButton" disabled>회원가입</button>
        </div>
    </form>
</div>
</body>
</html>