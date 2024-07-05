<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>회원가입</title>
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"
            integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU="
            crossorigin="anonymous">
    </script>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/members/join.css">

    <script>
        $(document).ready(function () {
            // 입력 필드 변경 시 검증 로직 실행
            $('input').on('input', function() {
                validateForm();
            });

            $('#checkPassword').on('input', function() {
                validatePassword();
            });

            function validateForm() {
                var isFormValid = checkFormValidity();
                $("#signupButton").prop("disabled", !isFormValid); // 조건에 따라 회원가입 버튼 활성화/비활성화
            }

            function checkFormValidity() {
                // 필수 입력 필드가 채워졌는지 확인
                var name = $("#name").val();
                var password = $("#password").val();
                var confirmPassword = $("#checkPassword").val();
                var email = $("#email").val();
                var nickname = $("#nickname").val();

                return name && email && password && confirmPassword && nickname && validatePassword() && validateEmail();
            }

            // 회원가입 로직
            $("#signupButton").click(function (e) {
                e.preventDefault();
                if (validatePassword() && validateEmail()) {
                    var formData = {
                        name: $("#name").val(),
                        password: $("#password").val(),
                        email: $("#email").val(),
                        nickname: $("#nickname").val()
                    };

                    $.ajax({
                        type: "POST",
                        url: "/api/v1/public/member/register",
                        contentType: "application/json",
                        data: JSON.stringify(formData),
                        success: function (response) {
                            alert("회원가입 성공");
                            window.location.href = "/";
                        },
                        error: function (xhr) {
                            if (xhr.status === 400 && xhr.responseText === "닉네임이 이미 사용 중입니다.") {
                                $("#nicknameError").text("이미 사용 중인 닉네임입니다.");
                            } else {
                                alert("회원가입 실패");
                            }
                        }
                    });
                }
            });

            function validatePassword() {
                var password = $("#password").val();
                var confirmPassword = $("#checkPassword").val();

                var passwordMessage = $("#passwordMessage");

                var passwordRegex = /^(?=.*[a-z])(?=.*\d).{8,20}$/;

                if (!passwordRegex.test(password)) {
                    passwordMessage.text("비밀번호는 최소 8~20자리, 하나의 소문자와 숫자를 포함해야 합니다.").css("color", "red");
                    return false;
                } else if (password !== confirmPassword) {
                    passwordMessage.text("비밀번호가 일치하지 않습니다.").css("color", "red");
                    return false;
                } else {
                    passwordMessage.text("비밀번호가 일치합니다.").css("color", "green");
                    return true;
                }
            }

            function validateEmail() {
                var email = $("#email").val();
                var emailRegex = /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/;
                var emailError = $("#emailError");

                if (!emailRegex.test(email)) {
                    emailError.text('올바른 이메일 주소를 입력해주세요.');
                    return false;
                } else {
                    emailError.text('');
                    return true;
                }
            }
        });

        function cancelButtonClicked() {
            window.location.href = "/api/v1/view/member/";
        }
    </script>
</head>
<body>
<div class="container">
    <form id="signupForm">
        <div class="form_group">
            <label for="email">이메일</label>
            <input type="email" id="email" name="email" placeholder="이메일을 입력하세요.">
            <span id="emailError" class="error-message" style="color: red;"></span>

            <label for="name">이름</label>
            <input type="text" id="name" name="name" placeholder="이름을 입력하세요.">

            <label for="password">비밀번호</label>
            <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요.">

            <label for="checkPassword">비밀번호 확인</label>
            <input type="password" id="checkPassword" name="confirmPassword" placeholder="비밀번호를 다시 입력하세요.">
            <div id="passwordMessage"></div>

            <label for="nickname">닉네임</label>
            <input type="text" id="nickname" name="nickname" placeholder="닉네임을 입력하세요.">
            <span id="nicknameError" class="error-message"></span>
            <span id="nicknameStatus" class="success-message"></span>

            <div class="buttons">
                <button type="button" class="btn btn-danger" onclick="cancelButtonClicked()">나가기</button>
                <button type="submit" id="signupButton" disabled>회원가입</button>
            </div>
        </div>
    </form>
</div>
</body>
</html>
