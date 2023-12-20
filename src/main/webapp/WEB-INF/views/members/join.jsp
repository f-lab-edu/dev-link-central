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
    </style>
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
    <script>
        $(document).ready(function () {
            // 닉네임 중복 체크 함수
            function checkNickname(callback) {
                var nickname = $("#nickname").val();
                if (nickname) {
                    $.ajax({
                        type: "GET",
                        url: "/members/" + nickname + "/exists",
                        success: function (result) {
                            if (result) {
                                // 중복된 경우
                                $("#nicknameError").text("이미 사용 중인 닉네임입니다.");
                                $("#nicknameStatus").text("");
                                callback(false);
                            } else {
                                // 중복되지 않은 경우
                                $("#nicknameError").text("");
                                $("#nicknameStatus").text("사용 가능한 닉네임입니다.");
                                callback(true);
                            }
                        },
                        error: function () {
                            console.error("닉네임 중복 체크 실패");
                            callback(false);
                        }
                    });
                } else {
                    callback(false);
                }
            }

            // 닉네임 중복 체크 버튼 클릭 시 이벤트
            $("#checkNicknameButton").click(function () {
                checkNickname(function (isValid) {
                    // 사용 가능한 닉네임인 경우 회원가입 버튼 활성화
                    if (isValid) {
                        $("#signupButton").prop("disabled", false);
                    }
                });
            });

            // 끝
        });
    </script>
</head>
<body>

<div class="container">
    <form action="/new" method="post">
        <div class="form_group">
            <label for="name">이름</label>
            <input type="text" id="name" name="name" placeholder="이름을 입력하세요.">

            <label for="password">비밀번호</label>
            <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요.">

            <label for="email">이메일</label>
            <input type="email" id="email" name="email" placeholder="이메일을 입력하세요.">

            <label for="nickname">닉네임</label>
            <input type="text" id="nickname" name="nickname" placeholder="닉네임을 입력하세요.">
            <span id="nicknameError" style="color: red;"></span>
            <span id="nicknameStatus" style="color: green;"></span>

            <!-- 중복 체크 버튼 추가 -->
            <button type="button" id="checkNicknameButton">닉네임 중복 확인</button>

            <button type="submit" id="signupButton" disabled>회원가입</button>
        </div>
    </form>
</div>
</body>
</html>
